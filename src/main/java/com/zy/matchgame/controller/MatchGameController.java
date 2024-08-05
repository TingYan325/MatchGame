package com.zy.matchgame.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/matching")
public class MatchGameController {

    @GetMapping("/getUsername")
    public String getUsername(HttpSession httpSession) {
        return (String) httpSession.getAttribute("username");
    }


}
