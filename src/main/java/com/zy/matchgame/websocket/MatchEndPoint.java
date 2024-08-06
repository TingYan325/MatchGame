package com.zy.matchgame.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zy.matchgame.config.WebSocketConfig;
import com.zy.matchgame.entity.GameMatchInfo;
import com.zy.matchgame.entity.Response;
import com.zy.matchgame.entity.UserMatchInfo;
import com.zy.matchgame.enums.MessageTypeEnum;
import com.zy.matchgame.enums.StatusEnum;
import com.zy.matchgame.error.GameServerError;
import com.zy.matchgame.exception.GameServerException;
import com.zy.matchgame.service.impl.QuestionServiceImpl;
import com.zy.matchgame.utils.MatchUtil;
import com.zy.matchgame.utils.ResponseUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.zy.matchgame.constant.CommonField.MATCH_TASK_NAME_PREFIX;

@Slf4j
@ServerEndpoint(value = "/websocket",configurator = WebSocketConfig.class)
@Component
public class MatchEndPoint {

    private HttpSession httpSession;

    @Autowired
    private MatchUtil matchUtil;

    @Autowired
    private ResponseUtil responseUtil;

    QuestionServiceImpl questionService;

    static Lock lock = new ReentrantLock();

    static Condition matchCond = lock.newCondition();

    /**
     * 建立websocket连接成功，将session和用户名保存
     * @param session
     * @param config
     * @return
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //获取用户名,将用户名和session保存在MatchUtil里的map中
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        matchUtil.addUser((String) httpSession.getAttribute("userName"), session);
        //返回系统成功信息
        sendToUser(responseUtil.response_Success((String) httpSession.getAttribute("userName")));
    }

    /**
     * 根据响应消息中的接收者集合来给相应的客户端响应消息
     * @param responseBody
     */
    public void sendToUser(Response<?> responseBody) {
        log.info("ChatWebsocket sendMessageAll 消息群发开始");

        Set<String> receivers = responseBody.getResponseMsg().getReceivers();
        for (String receiver : receivers) {
            MatchUtil.getOnlineUser(receiver).getAsyncRemote().sendText(JSON.toJSONString(responseBody));
        }
        log.info("ChatWebsocket sendMessageAll 消息群发结束");
    }

    /**
     * 根据客户端传来的json字符串来判断消息类型，以此来实现对应的业务逻辑
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        JSONObject jsonObject = JSON.parseObject(message);
        MessageTypeEnum type = jsonObject.getObject("type", MessageTypeEnum.class);

        switch (type) {
            case ADD_USER -> addUser(jsonObject);
            case PLAY_GAME -> playGame(jsonObject);
            case MATCH_USER -> matchUser(jsonObject);
            case GAME_OVER -> gameOver(jsonObject);
            case CANCEL_MATCH -> cancelGame(jsonObject);
            default -> throw new GameServerException(GameServerError.WEBSOCKET_ADD_USER_FAILED);
        }
    }

    /**
     * 在websocket断开连接时调用，删除用户存储的session
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        matchUtil.removeUser((String) httpSession.getAttribute("userName"));
        matchUtil.removeUserOnlineStatus((String) httpSession.getAttribute("userName"));
    }

    public void addUser(JSONObject jsonObject) {
        log.info("ChatWebsocket addUser 用户加入游戏开始 message: {}", jsonObject.toJSONString());

        String username = jsonObject.getString("username");
        StatusEnum statusEnum = matchUtil.getOnlineStatus(username);
        Response<?> response = responseUtil.response_AddUser(username);

        if(statusEnum != null) {
            if(statusEnum.compareTo(StatusEnum.GAME_OVER) == 0) {
                matchUtil.setOnlineStatus_IDLE(username);
            }
        } else {
            matchUtil.setOnlineStatus_IDLE(username);
        }

        sendToUser(response);

        log.info("ChatWebsocket addUser 用户加入游戏结束 message: {}", jsonObject.toJSONString());
    }

    @SneakyThrows
    public void matchUser(JSONObject jsonObject) {
        String username = jsonObject.getString("username");

        lock.lock();

        try{
            matchUtil.setOnlineStatus_InMatch(username);
            matchCond.signal();
        } finally {
            lock.unlock();
        }

        Thread matchThread = new Thread(() -> {
            boolean flag = true;
            String receiver = null;
            while (flag) {
                // 获取除自己以外的其他待匹配用户
                lock.lock();
                try {
                    // 当前用户不处于待匹配状态
                    if (matchUtil.getOnlineStatus(username).compareTo(StatusEnum.IN_GAME) == 0
                            || matchUtil.getOnlineStatus(username).compareTo(StatusEnum.GAME_OVER) == 0) {
                        log.info("ChatWebsocket matchUser 当前用户 {} 已退出匹配", username);
                        return;
                    }
                    // 当前用户取消匹配状态
                    if (matchUtil.getOnlineStatus(username).compareTo(StatusEnum.IDLE) == 0) {
                        // 当前用户取消匹配
                        log.info("ChatWebsocket matchUser 当前用户 {} 已退出匹配", username);
                        sendToUser(responseUtil.response_matchUserFail(username));
                        return;
                    }
                    receiver = matchUtil.getUserInMatchRandom(username);
                    if (receiver != null) {
                        // 对手不处于待匹配状态
                        if (matchUtil.getOnlineStatus(receiver).compareTo(StatusEnum.IN_MATCH) != 0) {
                            log.info("ChatWebsocket matchUser 当前用户 {}, 匹配对手 {} 已退出匹配状态", username, receiver);
                        } else {
                            matchUtil.setUserInGame(username);
                            matchUtil.setUserInGame(receiver);
                            matchUtil.setUserInRoom(username, receiver);
                            flag = false;
                        }
                    } else {
                        // 如果当前没有待匹配用户，进入等待队列
                        try {
                            log.info("ChatWebsocket matchUser 当前用户 {} 无对手可匹配", username);
                            matchCond.await();
                        } catch (InterruptedException e) {
                            log.error("ChatWebsocket matchUser 匹配线程 {} 发生异常: {}",
                                    Thread.currentThread().getName(), e.getMessage());
                        }
                    }
                } finally {
                    lock.unlock();
                }
            }

            //设置初始比赛用户信息
            UserMatchInfo senderInfo = new UserMatchInfo();
            UserMatchInfo receiverInfo = new UserMatchInfo();
            senderInfo.setUserId(username);
            senderInfo.setScore(0);
            receiverInfo.setUserId(receiver);
            receiverInfo.setScore(0);
            //存储用户比赛信息
            matchUtil.setUserMatchInfo(username, JSON.toJSONString(senderInfo));
            matchUtil.setUserMatchInfo(receiver, JSON.toJSONString(receiverInfo));
            //调用函数生成返回信息并发送
            Response<GameMatchInfo> response = responseUtil.response_matchUser(senderInfo, receiverInfo);
            sendToUser(response);
            response = responseUtil.response_matchUser(receiverInfo, senderInfo);
            sendToUser(response);

            log.info("ChatWebsocket matchUser 用户随机匹配对手结束 messageReply: {}", JSON.toJSONString(response));

        }, MATCH_TASK_NAME_PREFIX + username);
        matchThread.start();
    }

    public void playGame(JSONObject jsonObject) {
    }

    public void gameOver(JSONObject jsonObject) {
    }

    public void cancelGame(JSONObject jsonObject) {
        lock.lock();
        try {
            matchUtil.setOnlineStatus_IDLE(jsonObject.getString("username"));
        } finally {
            lock.unlock();
        }
    }
}