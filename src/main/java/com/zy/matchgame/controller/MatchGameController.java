package com.zy.matchgame.controller;

import com.zy.matchgame.entity.Response;
import com.zy.matchgame.utils.ResponseUtil;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matching")
public class MatchGameController {

    @Autowired
    ResponseUtil responseUtil;

    @GetMapping("/getUsername")
    public String getUsername(HttpSession httpSession) {
        return (String) httpSession.getAttribute("username");
    }

    @PostMapping("/login")
    public Response<?> login(@RequestBody User user, HttpSession httpSession) {
        if(user.getUsername() != null && user.getPassword() == "123") {
            httpSession.setAttribute("username", user.getUsername());
            return responseUtil.response_Success(user.getUsername());
        } else {
            return responseUtil.response_Faild(user.getUsername());
        }
    }
}
