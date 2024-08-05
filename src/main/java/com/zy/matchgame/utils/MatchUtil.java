package com.zy.matchgame.utils;

import com.zy.matchgame.websocket.MatchEndPoint;
import jakarta.websocket.Session;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MatchUtil {
    /**
     * 用户id为键，EndPoint为值，储存在线用户
     */
    private static final Map<String, Session> onlineUser = new ConcurrentHashMap<>();


    /**
     * 添加用户方法
     * @param username
     * @param session
     */
    public void addUser(String username, Session session) {
        onlineUser.put(username, session);
    }
}
