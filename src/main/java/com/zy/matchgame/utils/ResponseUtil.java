package com.zy.matchgame.utils;

import com.zy.matchgame.entity.Response;
import com.zy.matchgame.entity.ResponseMsg;
import com.zy.matchgame.enums.MessageCode;
import com.zy.matchgame.enums.MessageTypeEnum;
import com.zy.matchgame.enums.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ResponseUtil {

    @Autowired
    MatchUtil matchUtil;

    /**
     * 系统信息回复成功
     * @param userName
     * @return
     */
    public Response<String> response_Success(String userName) {
        Response response = new Response();
        ResponseMsg<String> responseMsg = new ResponseMsg<>();
        //设置respond里面的响应码和描述
        response.setCode(MessageCode.SUCCESS.getCode());
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
        response.setCode(MessageCode.SUCCESS.getCode());
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

    /**
     * 生成登陆失败的消息响应实体类
     * @param userName
     * @return
     */
    public Response<String> response_Faild(String userName) {
        Response response = new Response();
        ResponseMsg<String> responseMsg = new ResponseMsg<>();
        //设置respond里面的响应码和描述
        response.setCode(MessageCode.PASSWORD_ERROR.getCode());
        response.setDesc(MessageCode.PASSWORD_ERROR.getDesc());
        //设置respondMsg里面的消息类型为系统信息
        responseMsg.setType(MessageTypeEnum.SYSTEM);
        //设置respondMsg里面的发送者为NULL
        responseMsg.setSender("NULL");
        //设置respondMsg里面的接收者为该用户
        Set<String> set = new HashSet<>();
        set.add(userName);
        responseMsg.setReceivers(set);
        response.setResponseMsg(responseMsg);
        return response;
    }


    /**
     * 生成添加添加用户用户的响应消息实体类
     * @param userName
     * @return
     */
    public Response<Object> response_AddUser(String userName) {
        StatusEnum status = matchUtil.getOnlineStatus(userName);

        Response<Object> response = new Response<>();
        ResponseMsg<Object> responseMsg = new ResponseMsg<>();
        //设置接收人，消息类型和发送人为系统
        responseMsg.setType(MessageTypeEnum.ADD_USER);
        responseMsg.setSender("NULL");
        Set<String> set = new HashSet<>();
        set.add(userName);
        responseMsg.setReceivers(set);

        /**
         * 根据用户状态不同返回不同类型的消息码和描述
         */
        if(status != null) {
            if(status.compareTo(StatusEnum.GAME_OVER) == 0){
                response.setCode(MessageCode.SUCCESS.getCode());
                response.setDesc(MessageCode.SUCCESS.getDesc());
            } else {
                response.setCode(MessageCode.USER_IS_ONLINE.getCode());
                response.setDesc(MessageCode.USER_IS_ONLINE.getDesc());
            }
            response.setCode(MessageCode.SUCCESS.getCode());
            response.setDesc(MessageCode.SUCCESS.getDesc());
        }
        response.setResponseMsg(responseMsg);

        return response;
    }
}
