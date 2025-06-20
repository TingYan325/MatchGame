package com.zy.matchgame.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zy.matchgame.mapper.QuestionMapper;
import com.zy.matchgame.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;

import static com.zy.matchgame.utils.constant.CommonField.QUESTION_RANDOM_COUNT;

@Repository
public class QuestionDao {

    @Autowired
    private QuestionMapper mapper;

    /**
     * 从数据库获取所有 question
     */
    public List<Question> getAllQuestion() {
        long start = mapper.selectCount(null) - QUESTION_RANDOM_COUNT;

        Random random = new Random();
        start = random.nextInt((int) start);
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Question::getQuestionId, start, start+QUESTION_RANDOM_COUNT);
        return mapper.selectList(queryWrapper);
    }

    public String getAnswerById(Integer answerId) {
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Question::getQuestionId, answerId)
                .select(Question::getCorrect);

        return this.mapper.selectOne(queryWrapper).getCorrect();
    }
}
