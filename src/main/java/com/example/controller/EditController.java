package com.example.controller;

import com.example.service.UserService;
import lombok.RequiredArgsConstructor;

import java.io.IOException;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.example.dto.EditPasswordDTO;
import com.example.dto.UserEditDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@Controller
@RequestMapping("/edit")
@RequiredArgsConstructor
public class EditController {
    private final UserService userService;

    @GetMapping("/nickname")
    public String edit(Model model, HttpSession session) {
        Object username = session.getAttribute("username");
        if (username != null) {
           UserEditDTO userEditDTO = userService.getUserName(String.valueOf(username));
            model.addAttribute("userEditDTO", userEditDTO);
            return "nickname_edit";
        } else {
            return "redirect:/user/login";
        }
    }

    @PostMapping("/nickname")
    public String editNickname(@Valid @ModelAttribute("userEditDTO") UserEditDTO userEditDTO, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "nickname_edit";
        }

        String newNickname = userEditDTO.getNickname();

        if (newNickname == null || newNickname.isEmpty()) {
            model.addAttribute("errorMessage", "닉네임을 입력해주세요.");
            return "nickname_edit";
        }

        if (userService.isNicknameTaken(newNickname)) {
            model.addAttribute("errorMessage", "사용할 수 없는 닉네임입니다.");
            return "nickname_edit";
        }

        Object username = session.getAttribute("username");
        if (username != null) {
            userService.updateNickname(String.valueOf(username), newNickname);
        }
        
        return "redirect:/user/mypage";
    }
    @GetMapping("/password")
    public String editPassword(Model model) {
        model.addAttribute("editPasswordDTO", new EditPasswordDTO());
        return "password_edit";
    }
    @PostMapping("/password")
    public String postPassword(@Valid @ModelAttribute("editPasswordDTO") EditPasswordDTO editPasswordDTO, BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "password_edit";
        }
        if (!editPasswordDTO.getPassword().equals(editPasswordDTO.getCheck_password())) {
        	 model.addAttribute("errorMessage", "비밀번호와 비밀번호 확인 문자가 일치하지 않습니다.");
             return "password_edit";
        }
        Object username = session.getAttribute("username");
        if (username != null) {
            userService.updatePassword(String.valueOf(username),editPasswordDTO.getPassword());
        }
        return "redirect:/";
    }
    @GetMapping("/profile")
    public String profile() {
        return "profile_edit";
    }
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("profileImage") MultipartFile file, Model model,HttpSession session) {
        try {
            Object username = session.getAttribute("username");
            userService.saveProfileImage(String.valueOf(username),file);
        } catch (IOException e) {
            model.addAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다.");
            return "profile_edit";
        }
        return "redirect:/";
    }
    
}
