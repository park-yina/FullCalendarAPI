package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class SocialController {

    @GetMapping("/naver")
    public String naverLogin() {
        //이 부분 url은 변경불가
        return "redirect:/oauth2/authorization/naver";
    }
}
