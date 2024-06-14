package com.example.controller;

import com.example.dto.PainPostDTO;
import com.example.dto.UserEditDTO;
import com.example.entity.PainPost;
import com.example.entity.UserEntity;
import com.example.repository.PainRepository;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pain")
public class PainController {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PainRepository painPostRepository;

    @GetMapping("/board")
    public String painBoard(@RequestParam("username") String username, HttpSession session, Model model) {
        Object sessionUsername = session.getAttribute("username");

        if (sessionUsername == null || !sessionUsername.equals(username)) {
            return "redirect:/user/login";
        }

        // 세션에서 userId 가져오기
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "error"; // 세션에 userId가 없는 경우
        }
        // 필요한 경우 사용자의 특정 데이터도 같이 전달할 수 있음
        model.addAttribute("userId", userId);
        return "calendar"; // 사용자의 캘린더 화면으로 이동
    }


    @GetMapping("/new")
    public String newPain(@RequestParam("date") String date,
                          @RequestParam("userId") Long userId,
                          HttpSession session,
                          Model model) {
        Object sessionUserId = session.getAttribute("userId");

        if (sessionUserId == null || !sessionUserId.equals(userId)) {
            return "redirect:/user/login";
        }

        // PainPostDTO 생성 및 초기화
        PainPostDTO painPostDTO = new PainPostDTO();
        painPostDTO.setDate(date);

        // 모델에 필요한 데이터 추가
        model.addAttribute("userId", userId);
        model.addAttribute("date", date);
        model.addAttribute("painPostDTO", painPostDTO);

        return "Pain_form"; // 통증 기록 폼으로 이동
    }

    @PostMapping("/save")
    public String savePain(@ModelAttribute("painPostDTO") PainPostDTO painPostDTO, HttpSession session) {
        Object sessionUsername = session.getAttribute("username");

        if (sessionUsername == null) {
            return "redirect:/user/login";
        }

        // 세션에서 userId 가져오기
        Long userId = (Long) session.getAttribute("userId");

        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return "redirect:/user/login"; // 유효하지 않은 userId인 경우
        }
        UserEntity user = userOptional.get();
        UserEditDTO author = userService.getUserName(String.valueOf(sessionUsername));
        // PainPost 엔티티 생성 및 데이터 설정
        PainPost painPost = new PainPost();
        painPost.setAuthor(author.getNickname());
        painPost.setUser(user); // UserEntity 설정
        painPost.setContent(painPostDTO.getContent());
        painPost.setDate(painPostDTO.getDate());
        painPost.setStart(painPostDTO.getStart());
        painPost.setEnd(painPostDTO.getEnd());
        painPost.setPill(painPostDTO.isPill());
        painPost.setPill_name(painPostDTO.getPill_name());
        painPost.setSeverity(painPostDTO.getSeverity());

        if (painPost.getPill_name() == null || painPost.getPill_name().isEmpty()) {
            // painPost.getPill_name()이 null이거나 비어 있는 경우 처리
            painPost.setPill_name("없음");
        }
        if(painPost.getEnd()==null||painPost.getEnd().isEmpty()){
            painPost.setEnd("종일");
        }
        // 저장 처리 로직
        painPostRepository.save(painPost);

        return "redirect:/my?username=" + sessionUsername;
    }

    @GetMapping("/events")
    @ResponseBody
    public List<PainPostDTO> getPainEventsForToday(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return Collections.emptyList(); // 세션에 사용자 ID가 없는 경우 빈 리스트 반환
        }


        List<PainPost> painPosts = painPostRepository.findByUserId(userId);
        return painPosts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    private PainPostDTO convertToDTO(PainPost painPost) {
        PainPostDTO dto = new PainPostDTO();
        dto.setContent(painPost.getContent());
        dto.setDate(painPost.getDate());
        dto.setStart(painPost.getStart());

        dto.setPill(painPost.isPill());
        dto.setPre_pill(painPost.isPre_pill());
        dto.setDisclosure(painPost.isDisclosure());
        dto.setPill_name(painPost.getPill_name());

        dto.setSeverity(painPost.getSeverity());
        dto.setAuthor(painPost.getAuthor());
        return dto;
    }
}

