package com.zy.matchgame.entity;

import lombok.Data;

import java.util.List;

/**
 * 比赛信息实体类
 */
@Data
public class GameMatchInfo {

    private UserMatchInfo selfInfo;
    private UserMatchInfo opponentInfo;
    private List<Question> questions;
}