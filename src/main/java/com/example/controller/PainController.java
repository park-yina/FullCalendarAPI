package com.example.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pain")
public class PainController {

    @GetMapping("/board")
    public String painBoard(@RequestParam("username") String username, HttpSession session) {
        Object sessionUsername = session.getAttribute("username");
        if (sessionUsername == null || !sessionUsername.equals(username)) {
            return "redirect:/user/login";
        }
        return "calendar";
    }

    @GetMapping("/new")
    public String newPain(@RequestParam("username") String username, @RequestParam("date") String date, HttpSession session, Model model) {
        Object sessionUsername = session.getAttribute("username");
        if (sessionUsername == null || !sessionUsername.equals(username)) {
            return "redirect:/user/login";
        }
        model.addAttribute("username", username);
        model.addAttribute("date", date);
        return "Pain_form"; // 반환할 뷰 이름
    }
}

