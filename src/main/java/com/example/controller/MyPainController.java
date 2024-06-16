package com.example.controller;

import com.example.dto.PainPostDTO;
import com.example.entity.PainPost;
import com.example.repository.PainRepository;
import com.example.service.PainPostService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyPainController {
    private final UserService userService;
    private final PainRepository painRepository;
    private final PainPostService painPostService;

    @GetMapping("/")
    public String myPainPost(@RequestParam("username") String username, Model model, HttpSession session){
        Object sessionUsername = session.getAttribute("username");
        if(sessionUsername == null || !sessionUsername.equals(username)){
            return "redirect:/user/login";
        }

        Long userId = (Long) session.getAttribute("userId");
        List<PainPost> painPosts = painRepository.findByUserIdOrderByDateAsc(userId);
        painRepository.findAllOrderByDateAsc();
        model.addAttribute("userId", userId);
        model.addAttribute("posts", painPosts);
        return "MyPain"; // 이 부분은 뷰 파일의 이름입니다.
    }
        @GetMapping("/post/details")
        public String myDetailPost(@RequestParam("id") Long postId, @RequestParam("userId") Long userId, Model model, HttpSession session) {
            if (!session.getAttribute("userId").equals(userId)) {
                return "redirect:/"; // 본인이 쓴 게시물이 아닌 경우 메인 화면으로 이동
            }

            Optional<PainPost> optionalPainPost = painPostService.checkPainPost(postId);
            if (optionalPainPost.isPresent()) {
                PainPost painPost = optionalPainPost.get();
                model.addAttribute("painPost", painPost);
                if(painPost.getEnd().equals("종일")){
                    model.addAttribute("duration","종일");
                }
                else{
                    String duration = painPostService.calculateDuration(painPost);
                    model.addAttribute("duration", duration);
                }

                return "PainPostDetails"; // 뷰 파일 이름
            } else {
                model.addAttribute("error","게시물이 없습니다.");
                return "error"; // 게시물이 없는 경우의 처리
            }
        }
}

