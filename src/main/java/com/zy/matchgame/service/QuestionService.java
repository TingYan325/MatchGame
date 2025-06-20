package com.zy.matchgame.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zy.matchgame.entity.Question;

import java.util.List;

public interface QuestionService extends IService<Question> {
    List<Question> getAllQuestion();

    String getAnswerById(Integer answerId);
}
