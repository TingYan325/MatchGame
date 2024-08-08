package com.zy.matchgame.utils;

import com.zy.matchgame.entity.*;
import com.zy.matchgame.enums.MessageCode;
import com.zy.matchgame.enums.MessageTypeEnum;
import com.zy.matchgame.enums.StatusEnum;
import com.zy.matchgame.service.impl.QuestionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.zy.matchgame.enums.MessageTypeEnum.*;

@Component
public class ResponseUtil {

    @Autowired
    MatchUtil matchUtil;

    @Autowired
    QuestionServiceImpl questionService;

    /**
     * 系统信息回复成功
     * @param userName
     * @return
     */
    public Response<Object> response_Success(String userName) {
        ResponseMsg<Object> responseMsg = setResponseMsg(userName, null, SYSTEM);
        return setResponse(MessageCode.SUCCESS, responseMsg);
    }

    /**
     * 封装响应多个用户的信息的方法
     * @param receivers
     * @return
     */
    public Response<Object> response_Success(Set<String> receivers) {
        ResponseMsg<Object> responseMsg = setResponseMsg(receivers, null, SYSTEM);
        return setResponse(MessageCode.SUCCESS, responseMsg);
    }

    /**
     * 生成登陆失败的消息响应实体类
     * @param userName
     * @return
     */
    public Response<Object> response_Faild(String userName) {
        ResponseMsg<Object> responseMsg = setResponseMsg(userName, null, SYSTEM);
        return setResponse(MessageCode.PASSWORD_ERROR, responseMsg);
    }


    /**
     * 生成添加添加用户用户的响应消息实体类
     * @param userName
     * @return
     */
    public Response<Object> response_AddUser(String userName) {
        StatusEnum status = matchUtil.getOnlineStatus(userName);

        ResponseMsg<Object> responseMsg = setResponseMsg(userName, null, ADD_USER);
        Response<Object> response = new Response<>();
        /*
          根据用户状态不同返回不同类型的消息码和描述
         */
        if(status != null) {
            if(status.compareTo(StatusEnum.GAME_OVER) == 0){
                response = setResponse(MessageCode.SUCCESS, responseMsg);
            } else {
                setResponse(MessageCode.USER_IS_ONLINE, responseMsg);
            }
            setResponse(MessageCode.SUCCESS, responseMsg);
        }

        return response;
    }

    /**
     * 匹配用户成功的返回消息
     *
     * @param senderInfo
     * @param receiverInfo
     * @return
     */
    public Response<Object> response_matchUser(UserMatchInfo senderInfo, UserMatchInfo receiverInfo) {
        /*
           封装比赛基本信息，本人信息，对手信息，题目
         */
        GameMatchInfo gameMatchInfo = new GameMatchInfo();
        gameMatchInfo.setSelfInfo(senderInfo);
        gameMatchInfo.setOpponentInfo(receiverInfo);
        List<Question> questions = questionService.getAllQuestion();
        gameMatchInfo.setQuestions(questions);

        ResponseMsg<Object> responseMsg = setResponseMsg(receiverInfo.getUserId(), gameMatchInfo, MATCH_USER);

        return setResponse(MessageCode.SUCCESS, responseMsg);
    }


    /**
     * 返回用户取消匹配的消息实体类
     * @param userName
     * @return
     */
    public Response<Object> response_matchUserFail(String userName) {
        ResponseMsg<Object> responseMsg = setResponseMsg(userName, null, CANCEL_MATCH);
        return setResponse(MessageCode.CANCEL_MATCH_ERROR, responseMsg);
    }

    /**
     * 答题正确后返回的消息的生成方法，分数加1
     * @param username
     * @param Score
     * @return
     */
    public Response<Object> response_InGameRight(String username, Integer Score) {
        UserMatchInfo userMatchInfo = new UserMatchInfo();
        userMatchInfo.setUserId(username);
        userMatchInfo.setScore(Score + 1);

        String receiver = matchUtil.getUserFromRoom(username);

        Set<String> set = new HashSet<>();
        set.add(receiver);
        set.add(username);

        ResponseMsg<Object> responseMsg = setResponseMsg(set, userMatchInfo, PLAY_GAME);

        return setResponse(MessageCode.SUCCESS, responseMsg);
    }

    /**
     * 返回游戏进行中选择错误答案的返回消息，分数无变化
     * @param username
     * @param Score
     * @return
     */
    public Response<Object> response_InGameFail(String username, Integer Score) {
        UserMatchInfo userMatchInfo = new UserMatchInfo();
        userMatchInfo.setUserId(username);
        userMatchInfo.setScore(Score);

        String receiver = matchUtil.getUserFromRoom(username);

        Set<String> set = new HashSet<>();
        set.add(receiver);
        set.add(username);

        ResponseMsg<Object> responseMsg = setResponseMsg(set, userMatchInfo, PLAY_GAME);

        return setResponse(MessageCode.SUCCESS, responseMsg);
    }


    /**
     * 使用函数进行消息封装，提高代码复用
     * @param receiver
     * @param data
     * @param messageTypeEnum
     * @return
     * @param <V>
     */
    private <V> ResponseMsg<Object> setResponseMsg(String receiver, V data, MessageTypeEnum messageTypeEnum) {
        ResponseMsg<Object> responseMsg = new ResponseMsg<>();

        responseMsg.setType(messageTypeEnum);
        responseMsg.setData(data);
        Set<String> set = new HashSet<>();
        set.add(receiver);
        responseMsg.setReceivers(set);

        return responseMsg;
    }

    private <V> ResponseMsg<Object> setResponseMsg(Set<String> receiver, V data, MessageTypeEnum messageTypeEnum) {
        ResponseMsg<Object> responseMsg = new ResponseMsg<>();

        responseMsg.setType(messageTypeEnum);
        responseMsg.setData(data);
        responseMsg.setReceivers(receiver);

        return responseMsg;
    }

    public <V> Response<V> setResponse(MessageCode messageCode, ResponseMsg<V> responseMsg) {

        Response<V> response = new Response<>();
        response.setCode(messageCode.getCode());
        response.setDesc(messageCode.getDesc());
        response.setResponseMsg(responseMsg);

        return response;
    }
}
