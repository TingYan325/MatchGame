package com.zy.matchgame.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.io.Serializable;

/**
 * (GamePlayer)实体类
 *
 * @author makejava
 * @since 2024-08-19 09:48:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayer implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long userId;

    private String userName;

    private Integer numberOfMatches;

    private Integer winOfMatches;

    private Integer gameScore;

    private Date creatBy;

    private Integer delFlag;
}

