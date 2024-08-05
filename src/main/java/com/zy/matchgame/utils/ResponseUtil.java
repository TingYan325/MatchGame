package com.zy.matchgame.utils;

import com.zy.matchgame.entity.Response;
import com.zy.matchgame.entity.ResponseMsg;
import com.zy.matchgame.enums.MessageCode;
import com.zy.matchgame.enums.MessageTypeEnum;
import jakarta.servlet.http.HttpServletResponse;
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
        response.setCode(MessageCode.SUCCESS.getCode().toString());
        response.setDesc(MessageCode.SUCCESS.getDesc());
        responseMsg.setType(MessageTypeEnum.SYSTEM);
        responseMsg.setSender("NULL");
        Set<String> set = new HashSet<>();
        set.add(userName);
        responseMsg.setReceivers(set);
        response.setResponseMsg(responseMsg);
        return response;
    }


}
