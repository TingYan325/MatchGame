package com.zy.matchgame;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.zy.matchgame.mapper")
@SpringBootApplication
public class MatchGameApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchGameApplication.class, args);
    }

}
