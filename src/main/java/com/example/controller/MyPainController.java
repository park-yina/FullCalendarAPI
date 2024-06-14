package com.example.controller;

import com.example.entity.PainPost;
import com.example.repository.PainRepository;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPainController {
    private final UserService userService;
    private final PainRepository painRepository;

    @GetMapping("/")
    public String myPainPost(@RequestParam("username") String username, Model model, HttpSession session){
        Object sessionUsername = session.getAttribute("username");
        if(sessionUsername == null || !sessionUsername.equals(username)){
            return "redirect:/user/login";
        }

        Long userId = (Long) session.getAttribute("userId");
        System.out.print(userId);
        List<PainPost> painPosts = painRepository.findByUserId(userId);

        model.addAttribute("posts", painPosts);
        return "MyPain"; // 이 부분은 뷰 파일의 이름입니다.
    }
}

