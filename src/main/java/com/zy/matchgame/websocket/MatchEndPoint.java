package com.zy.matchgame.websocket;

import com.alibaba.fastjson.JSON;
import com.zy.matchgame.config.GetHttpSessionConfig;
import com.zy.matchgame.entity.Answer;
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

import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.zy.matchgame.constant.CommonField.MATCH_TASK_NAME_PREFIX;

@Slf4j
@ServerEndpoint(value = "/websocket",configurator = GetHttpSessionConfig.class)
@Component
public class MatchEndPoint {
    private String userId;

    private HttpSession httpSession;

    static MatchUtil matchUtil;

    static ResponseUtil responseUtil;

    QuestionServiceImpl questionService;

    static Lock lock = new ReentrantLock();

    static Condition matchCond = lock.newCondition();

    @Autowired
    public void ResponseUtil(ResponseUtil responseUtil) {MatchEndPoint.responseUtil = responseUtil;}

    @Autowired
    public void setMatchCacheUtil(MatchUtil matchUtil) {
        MatchEndPoint.matchUtil = matchUtil;
    }

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
        matchUtil.addUser((String) httpSession.getAttribute("username"), session);
        this.userId = (String) httpSession.getAttribute("username");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("onError", error.getMessage());
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
        Response<?> jsonObject = JSON.parseObject(message, Response.class);
        MessageTypeEnum type = jsonObject.getResponseMsg().getType();

        switch (type) {
            case ADD_USER -> addUser(jsonObject);
            case PLAY_GAME -> playGame(message);
            case MATCH_USER -> matchUser(jsonObject);
            case GAME_OVER -> gameOver(jsonObject);
            case CANCEL_MATCH -> cancelGame(jsonObject);
            default -> throw new GameServerException(GameServerError.WEBSOCKET_ADD_USER_FAILED);
            //返回系统成功信息
            //sendToUser(responseUtil.response_Success((String) httpSession.getAttribute("username")));
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

    public void addUser(Response jsonObject) {

        String username = jsonObject.getResponseMsg().getSender();
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
    }

    /**
     * 匹配用户方法
     * @param jsonObject
     */
    @SneakyThrows
    public void matchUser(Response jsonObject) {
        String username = jsonObject.getResponseMsg().getSender();

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


    /**
     * 游戏进行中执行信息更新的方法
     * @param message
     */
    public void playGame(String message) {
        log.info("接收前端返回的答案，判断对错");
        //从传过来的json数据中获得用户名和用户选择的答案
        Response<Answer> jsonObject = JSON.parseObject(message, Response.class);
        String username = jsonObject.getResponseMsg().getSender();

        Integer answerId = jsonObject.getResponseMsg().getData().getAnswerId();
        String answer = jsonObject.getResponseMsg().getData().getAnswer();
        //通过判断答案，来决定加不加分
        if(answer.compareTo(questionService.getAnswerById(answerId)) == 0) {
            UserMatchInfo userMatchInfo = JSON.parseObject(matchUtil.getUserMatchInfo(username), UserMatchInfo.class);
            userMatchInfo.setScore(userMatchInfo.getScore() + 1);
            matchUtil.setUserMatchInfo(username, JSON.toJSONString(userMatchInfo));
            sendToUser(responseUtil.response_InGameRight(username, userMatchInfo.getScore()));
        } else {
            UserMatchInfo userMatchInfo = JSON.parseObject(matchUtil.getUserMatchInfo(username), UserMatchInfo.class);
            sendToUser(responseUtil.response_InGameFail(username, userMatchInfo.getScore()));
        }
        //判断完成
    }

    public void gameOver(Response<?> jsonObject) {

    }

    public void cancelGame(Response<?> jsonObject) {
        lock.lock();
        try {
            matchUtil.setOnlineStatus_IDLE(jsonObject.getResponseMsg().getSender());
        } finally {
            lock.unlock();
        }
    }
}