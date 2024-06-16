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
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/edit/{painPostId}")

    public String editMypain(@PathVariable("painPostId") Long painPostId, Model model) {
        Optional<PainPost> optionalPainPost = painRepository.findById(painPostId);

        if (optionalPainPost.isPresent()) {
            PainPost painPost = optionalPainPost.get();
            model.addAttribute("painPost", painPost);
            return "editPainPost"; // 수정 폼 페이지로 이동
        } else {
            model.addAttribute("error","정보를 불러오는 데에 실패했습니다.");
            return "error"; // 게시물이 없으면 에러 페이지로 리다이렉트
        }
    }
    @GetMapping("/delete/{painPostId}")
    public String deletePainPost(@PathVariable("painPostId")Long painPostId,Model model,HttpSession session){
        Optional<PainPost> optionalPainPost = painRepository.findById(painPostId);
        if(optionalPainPost.isPresent()){
            painRepository.deleteById(painPostId);
            // Redirect to a success page or to the edited painPost details page
            String username = String.valueOf(session.getAttribute("username"));

            // 리다이렉트 URL에 동적으로 username 전달
            return "redirect:/my/?username=" + username;

        }
        else{
            model.addAttribute("error","게시물 찾기 실패");
            return "error";
        }
    }

    @PostMapping("/edit/{painPostId}")
    public String editPostMypain(@PathVariable("painPostId") Long painPostId, @ModelAttribute("painPost") PainPost updatedPainPost, Model model,HttpSession session) {
        Optional<PainPost> optionalPainPost = painRepository.findById(painPostId);

        if (optionalPainPost.isPresent()) {
            PainPost painPost = optionalPainPost.get();

            painPost.setDate(updatedPainPost.getDate());
            painPost.setStart(updatedPainPost.getStart());
            if(updatedPainPost.getEnd()==null||updatedPainPost.getEnd().isBlank()){
                updatedPainPost.setEnd("종일");
            }
            painPost.setViews(painPost.getViews());
            painPost.setEnd(updatedPainPost.getEnd());
            painPost.setContent(updatedPainPost.getContent());
            painPost.setPill(updatedPainPost.isPill());
            painPost.setPre_pill(updatedPainPost.isPre_pill());
            painPost.setDisclosure(updatedPainPost.isDisclosure());
            painPost.setPill_name(updatedPainPost.getPill_name());
            painPost.setSeverity(updatedPainPost.getSeverity());
            // Save the updated painPost to the repository
            painRepository.save(painPost);

            // Redirect to a success page or to the edited painPost details page
            String username = String.valueOf(session.getAttribute("username"));

            // 리다이렉트 URL에 동적으로 username 전달
            return "redirect:/my/?username=" + username;
        } else {
            model.addAttribute("error","정보를 불러오는 데에 실패했습니다.");
            return "error"; // 게시물이 없으면 에러 페이지로 리다이렉트
        }
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

