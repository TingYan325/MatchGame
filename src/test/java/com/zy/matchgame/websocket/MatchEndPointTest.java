package com.zy.matchgame.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zy.matchgame.entity.Answer;
import com.zy.matchgame.entity.Response;
import com.zy.matchgame.entity.ResponseMsg;
import io.swagger.v3.core.util.Json;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchEndPointTest {
    @Autowired
    MatchEndPoint matchEndPoint;

    @Test
    public void test() {
        Answer answer = new Answer();
        answer.setAnswerId(1);
        answer.setAnswer("3");
        Response<Answer> message = new Response<>();
        ResponseMsg<Answer> msg = new ResponseMsg<>();
        msg.setData(answer);
        message.setResponseMsg(msg);
        String message1 = JSON.toJSONString(message);
        JSONObject jsonObject = JSON.parseObject(message1);

        matchEndPoint.playGame(jsonObject);
    }
}
