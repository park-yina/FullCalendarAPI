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

        return "redirect:/oauth2/authorization/naver";
    }
}
