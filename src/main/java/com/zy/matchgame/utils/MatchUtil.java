package com.zy.matchgame.utils;

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
     * 获取接受用户的session
     * @param receiver
     * @return
     */
    public static Session getOnlineUser(String receiver) {
        return onlineUser.get(receiver);
    }


    /**
     * 添加用户方法
     * @param username
     * @param session
     */
    public void addUser(String username, Session session) {
        onlineUser.put(username, session);
    }

    /**
     * 用户下线，删除用户对应的session
     * @param userName
     */
    public void removeUser(String userName) {
        onlineUser.remove(userName);
    }
}
