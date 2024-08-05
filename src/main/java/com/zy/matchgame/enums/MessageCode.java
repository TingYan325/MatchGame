package com.zy.matchgame.enums;


import lombok.Getter;

@Getter
public enum MessageCode {
    /**
     * 响应码
     */
    SUCCESS(2000, "连接成功"),
    USER_IS_ONLINE(2001, "用户已存在"),
    CURRENT_USER_IS_INGAME(2002, "当前用户已在游戏中"),
    MESSAGE_ERROR(2003, "消息错误"),
    CANCEL_MATCH_ERROR(2004, "用户取消了匹配");

    private final Integer code;
    private final String desc;

    MessageCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
