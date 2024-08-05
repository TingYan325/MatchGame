package com.zy.matchgame.entity;

import lombok.Data;

/**
 - @title 响应实体父类
 */
@Data
public class Response<T> {
	
	private String code;

	private String desc;

	private ResponseMsg<T> responseMsg;
}
