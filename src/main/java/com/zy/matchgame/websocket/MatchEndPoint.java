package com.zy.matchgame.websocket;

import com.zy.matchgame.config.GetHttpSessionConfig;
import com.zy.matchgame.utils.MatchUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/match", configurator = GetHttpSessionConfig.class)
@Component
public class MatchEndPoint {

    private HttpSession httpSession;

    @Autowired
    private MatchUtil matchUtil;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        matchUtil.addUser((String) httpSession.getAttribute("userName"), session);
    }

    @OnMessage
    public void onMessage(String message) {

    }

    @OnClose
    public void onClose(Session session) {

    }

}
