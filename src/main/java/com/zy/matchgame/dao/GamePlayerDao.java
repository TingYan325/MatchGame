package com.zy.matchgame.dao;

import com.zy.matchgame.mapper.GamePlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class GamePlayerDao {
    @Autowired
    private GamePlayerMapper gamePlayerMapper;


}
