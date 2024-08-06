package com.zy.matchgame.utils;

import com.zy.matchgame.enums.EnumRedisKey;
import com.zy.matchgame.enums.StatusEnum;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import org.springframework.data.redis.core.RedisTemplate;
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
     * key 是标识存储用户在线状态的 EnumRedisKey，value 为 map 类型，其中用户 userId 为 key，用户在线状态 为 value
     */
    @Resource
    private RedisTemplate<String, Map<String, String>> redisTemplate;

    /**
     * 获取在线用户的session
     * @param receiver
     * @return
     */
    public static Session getOnlineUser(String receiver) {
        return onlineUser.get(receiver);
    }

    /**
     * 获取用户在线状态的方法
     * @param username
     * @return
     */
    public StatusEnum getOnlineStatus(String username) {
        Object status = redisTemplate.opsForHash().get(EnumRedisKey.USER_STATUS.getKey(), username);
        if (status == null) {
            return null;
        }
        return StatusEnum.getStatusEnum(status.toString());
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

    /**
     * 移除用户在线状态
      * @param userId
     */
    public void removeUserOnlineStatus(String userId) {
        redisTemplate.opsForHash().delete(EnumRedisKey.USER_STATUS.getKey(), userId);
    }

    /**
     * 修改用户在线状态为IDLE
     * @param username
     */
    public void setOnlineStatus_IDLE(String username) {
        removeUserOnlineStatus(username);
        redisTemplate.opsForHash().put(EnumRedisKey.USER_STATUS.getKey(), username, StatusEnum.IDLE.getValue());
    }
}
