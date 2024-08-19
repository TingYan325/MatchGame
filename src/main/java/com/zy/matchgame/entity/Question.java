package com.zy.matchgame.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Question)实体类
 *
 * @author makejava
 * @since 2024-08-05 13:17:33
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Question implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long questionId;
/**
     * 题目内容
     */
    private String content;
/**
     * 选项A
     */
    private String answer1;
/**
     * 选项B
     */
    private String answer2;
/**
     * 选项C
     */
    private String answer3;
/**
     * 选项D
     */
    private String answer4;

    private String correct;
/**
     * 逻辑删除标志
     */
    private Integer delFlag;
}

