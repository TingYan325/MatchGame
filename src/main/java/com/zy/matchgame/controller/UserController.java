package com.zy.matchgame.controller;

import com.zy.matchgame.service.GamePlayerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private GamePlayerService gamePlayerService;

    @GetMapping("/getUsername")
    public String getUsername(HttpSession httpSession) {
        return (String) httpSession.getAttribute("username");
    }
}

