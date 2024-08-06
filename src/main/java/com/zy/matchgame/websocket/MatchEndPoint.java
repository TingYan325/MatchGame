package com.zy.matchgame.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zy.matchgame.config.GetHttpSessionConfig;
import com.zy.matchgame.domain.LogicImpl;
import com.zy.matchgame.entity.Response;
import com.zy.matchgame.enums.MessageTypeEnum;
import com.zy.matchgame.utils.MatchUtil;
import com.zy.matchgame.utils.ResponseUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@ServerEndpoint(value = "/match", configurator = GetHttpSessionConfig.class)
@Component
public class MatchEndPoint {

    private HttpSession httpSession;

    @Autowired
    private MatchUtil matchUtil;

    @Autowired
    private ResponseUtil responseUtil;

    @Autowired
    private LogicImpl logicImpl;

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
            case ADD_USER -> logicImpl.addUser(jsonObject);
            case PLAY_GAME -> logicImpl.playGame(jsonObject);
            case MATCH_USER -> logicImpl.matchUser(jsonObject);
            case GAME_OVER -> logicImpl.gameOver(jsonObject);
        }
    }

    /**
     * 在websocket断开连接时调用，删除用户存储的session
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        matchUtil.removeUser((String) httpSession.getAttribute("userName"));
    }
}
