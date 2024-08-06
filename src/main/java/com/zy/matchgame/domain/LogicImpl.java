package com.zy.matchgame.domain;

import com.alibaba.fastjson.JSONObject;
import com.zy.matchgame.entity.Response;
import com.zy.matchgame.enums.StatusEnum;
import com.zy.matchgame.utils.MatchUtil;
import com.zy.matchgame.utils.ResponseUtil;
import com.zy.matchgame.websocket.MatchEndPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogicImpl {
    @Autowired
    MatchEndPoint matchEndPoint;

    @Autowired
    ResponseUtil responseUtil;

    private final MatchUtil matchUtil;

    public LogicImpl(MatchUtil matchUtil) {
        this.matchUtil = matchUtil;
    }

    public void addUser(JSONObject jsonObject) {
        log.info("ChatWebsocket addUser 用户加入游戏开始 message: {}", jsonObject.toJSONString());

        String username = jsonObject.getString("username");
        StatusEnum statusEnum = matchUtil.getOnlineStatus(username);
        Response<?> response = responseUtil.response_AddUser(username);

        if(statusEnum != null) {
            if(statusEnum.compareTo(StatusEnum.GAME_OVER) == 0) {
                matchUtil.setOnlineStatus_IDLE(username);
            }
        } else {
            matchUtil.setOnlineStatus_IDLE(username);
        }

        matchEndPoint.sendToUser(response);

        log.info("ChatWebsocket addUser 用户加入游戏结束 message: {}", jsonObject.toJSONString());
    }

    public void playGame(JSONObject jsonObject) {
    }

    public void matchUser(JSONObject jsonObject) {

    }

    public void gameOver(JSONObject jsonObject) {
    }
}
