package com.example.controller;

import com.example.entity.PostEntity;
import com.example.repository.PostRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class NoticeController {
    private final PostRepository postRepository;
    @GetMapping("")
    public String board(HttpSession session) {
        Object username=session.getAttribute("username");
        if(username!=null) {
            return "board";
        }else {
            return "redirect:/user/login";
        }

    }
    @GetMapping("/{boardType}")
    public String getBoardPosts(@PathVariable("boardType") String boardType, Model model) {
        model.addAttribute("boardType", boardType);

        List<PostEntity> posts = postRepository.findByBoardType(boardType);
        model.addAttribute("posts", posts);
        return "detail_board";
    }


}
