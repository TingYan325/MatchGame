package com.zy.matchgame.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.matchgame.mapper.QuestionMapper;
import com.zy.matchgame.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class QuestionDao {

    @Autowired
    private QuestionMapper mapper;

    /**
     * 从数据库获取所有 question
     */
    public List<Question> getAllQuestion() {
        return mapper.selectList(null);
    }

    public String getAnswerById(Integer answerId) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getQuestionId, answerId)
                .select(Question::getCorrect);

        return this.mapper.selectOne(queryWrapper).getCorrect();
    }
}
