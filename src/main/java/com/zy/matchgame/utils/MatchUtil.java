package com.zy.matchgame.utils;

import com.zy.matchgame.enums.EnumRedisKey;
import com.zy.matchgame.enums.StatusEnum;
import jakarta.annotation.Resource;
import jakarta.websocket.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
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
     * 设置用户为 IN_GAME 状态
     * @param userId
     */
    public void setUserInGame(String userId) {
        removeUserOnlineStatus(userId);
        redisTemplate.opsForHash().put(EnumRedisKey.USER_STATUS.getKey(), userId, StatusEnum.IN_GAME.getValue());
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

    /**
     * 设置用户为 IN_MATCH 状态
     */
    public void setOnlineStatus_InMatch(String userId) {
        removeUserOnlineStatus(userId);
        redisTemplate.opsForHash().put(EnumRedisKey.USER_STATUS.getKey(), userId, StatusEnum.IN_MATCH.getValue());
    }

    /**
     * 设置处于游戏中的用户的对战信息
     */
    public void setUserMatchInfo(String userId, String userMatchInfo) {
        redisTemplate.opsForHash().put(EnumRedisKey.USER_MATCH_INFO.getKey(), userId, userMatchInfo);
    }

    /**
     * 设置处于游戏中的用户在同一房间
     * @param userId1
     * @param userId2
     */
    public void setUserInRoom(String userId1, String userId2) {
        redisTemplate.opsForHash().put(EnumRedisKey.ROOM.getKey(), userId1, userId2);
        redisTemplate.opsForHash().put(EnumRedisKey.ROOM.getKey(), userId2, userId1);
    }

    /**
     * 随机获取处于匹配状态的用户
     */
    public String getUserInMatchRandom(String userId) {
        Optional<Map.Entry<Object, Object>> any = redisTemplate.opsForHash().entries(EnumRedisKey.USER_STATUS.getKey())
                .entrySet().stream().filter(entry -> entry.getValue().equals(StatusEnum.IN_MATCH.getValue()) && !entry.getKey().equals(userId))
                .findAny();
        return any.map(entry -> entry.getKey().toString()).orElse(null);
    }

    /**
     * 从房间中获取用户
     */
    public String getUserFromRoom(String userId) {
        return redisTemplate.opsForHash().get(EnumRedisKey.ROOM.getKey(), userId).toString();
    }

    /**
     * 获取处于游戏中的用户的对战信息
     */
    public String getUserMatchInfo(String userId) {
        return redisTemplate.opsForHash().get(EnumRedisKey.USER_MATCH_INFO.getKey(), userId).toString();
    }

    public synchronized void setOnlineStatus_GAMEOVER(String sender) {
        removeUserOnlineStatus(sender);
        redisTemplate.opsForHash().put(EnumRedisKey.USER_STATUS.getKey(), sender, StatusEnum.GAME_OVER.getValue());
    }

    /**
     * 移除处于游戏中的用户的对战信息
     */
    public void removeUserMatchInfo(String userId) {
        redisTemplate.opsForHash().delete(EnumRedisKey.USER_MATCH_INFO.getKey(), userId);
    }

    /**
     * 从房间中移除用户
     */
    public void removeUserFromRoom(String userId) {
        redisTemplate.opsForHash().delete(EnumRedisKey.ROOM.getKey(), userId);
    }
}
