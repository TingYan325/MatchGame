package com.zy.matchgame.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zy.matchgame.dao.QuestionDao;
import com.zy.matchgame.entity.Question;
import com.zy.matchgame.mapper.QuestionMapper;
import com.zy.matchgame.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    @Autowired
    private QuestionDao questionDao;

    /**
     * 获取问题
     */
    @Override
    public List<Question> getAllQuestion() {
        return questionDao.getAllQuestion();
    }

    @Override
    public String getAnswerById(Integer answerId) {
        return questionDao.getAnswerById(answerId);
    }
}
