package com.example.controller;

import com.example.service.MypageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dto.UserMypageDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserInfoController {
	private final MypageService mypageService;
	  @GetMapping("/mypage")
	    public String getMypage(HttpSession session, Model model) {
	        Object username = session.getAttribute("username");

	        if (username != null) {
	            UserMypageDTO userMypageDTO = mypageService.getUserMypageInfo(String.valueOf(username));
	            model.addAttribute("userMypageDTO", userMypageDTO);
	            return "mypage";
	        } else {
	            return "redirect:/user/login";
	        }
	    }

}
