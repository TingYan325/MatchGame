package com.zy.matchgame.controller;

import com.zy.matchgame.entity.Response;
import com.zy.matchgame.enums.User;
import com.zy.matchgame.utils.ResponseUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

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
        if(user.getUsername() != null && Objects.equals(user.getPassword(), "123")) {
            String username = user.getUsername() + UUID.randomUUID();
            httpSession.setAttribute("username", username);
            return responseUtil.response_Success(username);
        } else {
            return responseUtil.response_Faild(user.getUsername());
        }
    }
}
