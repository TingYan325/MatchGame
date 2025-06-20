package com.zy.matchgame.enums;

/**
 * Redis 存储 key 的枚举
 */
public enum EnumRedisKey {

    /**
     * userOnline 在线状态
     */
    USER_STATUS,
    /**
     * userOnline 匹配信息
     */
    USER_MATCH_INFO,
    /**
     * 房间
     */
    ROOM;

    public String getKey() {
        return this.name();
    }
}