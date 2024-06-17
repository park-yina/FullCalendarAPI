package com.example.controller;

import com.example.entity.PainPost;
import com.example.repository.PainRepository;
import com.example.service.PainPostService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@RequestMapping("/post")
@RequiredArgsConstructor
@Controller
public class PostController{
    private final PainRepository painRepository;
    private final PainPostService painPostService;
    @GetMapping("/details")
    public String myDetailPost(@RequestParam("id") Long postId, HttpSession session, Model model) {
        if (session.getAttribute("username") == null) {
            return "redirect:/user/login";
        }
        Optional<PainPost> optionalPainPost = painPostService.checkPainPost(postId);
        if (optionalPainPost.isPresent()) {
            PainPost painPost = optionalPainPost.get();
            painPost.setViews(painPost.getViews() + 1);
            painRepository.save(painPost);
            model.addAttribute("painPost", painPost);
            if(painPost.getEnd().equals("종일")){
                model.addAttribute("duration","종일");
            }
            else{
                String duration = painPostService.calculateDuration(painPost);
                model.addAttribute("duration", duration);
            }
            return "disclosed_detail"; // 뷰 파일 이름
        } else {
            model.addAttribute("error","게시물이 없습니다.");
            return "error"; // 게시물이 없는 경우의 처리
        }
    }
}
