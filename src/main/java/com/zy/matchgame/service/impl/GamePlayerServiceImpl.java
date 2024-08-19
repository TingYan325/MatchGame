package com.zy.matchgame.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.matchgame.entity.GamePlayer;
import com.zy.matchgame.mapper.GamePlayerMapper;
import com.zy.matchgame.service.GamePlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GamePlayerServiceImpl extends ServiceImpl<GamePlayerMapper, GamePlayer> implements GamePlayerService {
    @Autowired
    private GamePlayerMapper gamePlayerMapper;
}
