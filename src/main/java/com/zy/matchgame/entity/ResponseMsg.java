package com.zy.matchgame.entity;

import com.zy.matchgame.enums.MessageTypeEnum;
import lombok.Data;

import java.awt.*;
import java.util.Set;

/**
 * @title 消息响应实体类
 */
@Data
public class ResponseMsg<T> {

	/**
	 * 消息类型
	 */
	private MessageTypeEnum type;
	/**
	 * 消息发送者
	 */
	private String sender;
	/**
	 * 消息接收者
	 */
	private Set<String> receivers;

	private T data;


}
