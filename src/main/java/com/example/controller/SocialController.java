package com.example.controller;

import com.example.service.NaverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Controller
@RequestMapping("/login")
public class SocialController {
    @Autowired
    private NaverService naverService;

    @GetMapping("/naver")
    public String naverLogin(@RequestParam(value = "code", required = false) String code,
                             @RequestParam(value = "state", required = false) String state,
                             Model model) throws Exception {
        if (code == null || state == null) {
            return "redirect:/nid.naver.com/oauth2.0/authorize";
        } else {
            String redirectURI = "http://localhost:8080/login/naver";

            // 네이버 API를 통해 Access Token 가져오기
            String accessToken = naverService.getAccessToken(code, state, redirectURI);

            // Access Token을 사용하여 네이버 사용자 정보 가져오기
            String naverProfile = naverService.getNaverProfile(accessToken);
            System.out.println("Naver Profile: " + naverProfile);

            // 가져온 프로필 정보에서 닉네임(nickname) 추출
            String nickname = naverService.extractNicknameFromNaverProfile(naverProfile);

            // 모델에 닉네임 추가
            model.addAttribute("nickname", nickname);

            // 사용자 정보 화면으로 이동
            return "test"; // 사용자 정보를 보여줄 뷰 페이지
        }
    }
}
