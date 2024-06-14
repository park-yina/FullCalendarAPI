package com.example.controller;

import com.example.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPainController {
    private final UserService userService;
    @GetMapping("/")
    public String myPainPost(@RequestParam("username") String username, Model model){
        return null;
    }
}
