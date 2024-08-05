package com.zy.matchgame.utils;

import com.zy.matchgame.entity.Response;
import com.zy.matchgame.entity.ResponseMsg;
import com.zy.matchgame.enums.MessageCode;
import com.zy.matchgame.enums.MessageTypeEnum;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ResponseUtil {
    /**
     * 系统信息回复成功
     * @param userName
     * @return
     */
    public Response<String> response_Success(String userName) {
        Response response = new Response();
        ResponseMsg<String> responseMsg = new ResponseMsg<>();
        //设置respond里面的响应码和描述
        response.setCode(MessageCode.SUCCESS.getCode().toString());
        response.setDesc(MessageCode.SUCCESS.getDesc());
        //设置respondMsg里面的消息类型为系统信息
        responseMsg.setType(MessageTypeEnum.SYSTEM);
        //设置respondMsg里面的发送者为NULL
        responseMsg.setSender("NULL");
        //设置respondMsg里面的接收者为该用户
        Set<String> set = new HashSet<>();
        set.add(userName);
        responseMsg.setReceivers(set);
        //封装respond
        response.setResponseMsg(responseMsg);
        return response;
    }

    /**
     * 封装响应多个用户的信息的方法
     * @param receivers
     * @return
     */
    public Response<String> response_Success(Set<String> receivers) {
        Response response = new Response();
        ResponseMsg<String> responseMsg = new ResponseMsg<>();
        //设置respond里面的响应码和描述
        response.setCode(MessageCode.SUCCESS.getCode().toString());
        response.setDesc(MessageCode.SUCCESS.getDesc());
        //设置respondMsg里面的消息类型为系统信息
        responseMsg.setType(MessageTypeEnum.SYSTEM);
        //设置respondMsg里面的发送者为NULL
        responseMsg.setSender("NULL");
        //设置respondMsg里面的接收者为该用户列表
        responseMsg.setReceivers(receivers);
        response.setResponseMsg(responseMsg);
        return response;
    }

}
