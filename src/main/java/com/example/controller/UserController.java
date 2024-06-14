package com.example.controller;

import com.example.dto.UserCreateDTO;
import com.example.dto.UserLoginDTO;
import com.example.dto.UserMypageDTO;
import com.example.repository.UserRepository;
import com.example.service.MypageService;
import com.example.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.example.util.ImgUtil;
import java.io.IOException;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final MypageService mypageService;
    private final UserRepository userRepository;
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("userCreateDTO", new UserCreateDTO());
        return "sign";
    }
    @PostMapping("/signup")
    public String postSignup(@Valid UserCreateDTO userCreateDTO, BindingResult bindingResult, @RequestParam("profileImage") MultipartFile file) throws IOException {
        if (bindingResult.hasErrors()) {
            return "sign";
        }

        byte[] profileBytes = file.isEmpty() ? null : file.getBytes();
        userService.create(userCreateDTO.getUsername(), userCreateDTO.getPassword(), userCreateDTO.getNickname(), profileBytes);
        return "redirect:/";
    }
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("userLoginDTO", new UserLoginDTO());
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@Valid UserLoginDTO userLoginDTO, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "login";
        }

        try {
            userService.login(userLoginDTO.getUsername(), userLoginDTO.getPassword());
            session.setAttribute("username", userLoginDTO.getUsername());
            return "redirect:/"; // 로그인 성공 시 홈으로 리다이렉트
        } catch (Exception e) {
            bindingResult.reject("loginFail", "Invalid username or password");
            return "login"; // 로그인 실패 시 로그인 페이지로 이동
        }
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 로그아웃 시 세션 무효화
        return "redirect:/user/login?logout=true";
    }

    @GetMapping("/info")
    public String getInfo(HttpSession session, Model model) throws IOException {
        Object username = session.getAttribute("username");
        if (username != null) {
            UserMypageDTO userMypageDTO = mypageService.getUserMypageInfo(String.valueOf(username));

            // BLOB 데이터를 가져와서 이미지 크기 조정 및 Base64 변환
            byte[] profileBlob = userMypageDTO.getProfile();
            if (profileBlob != null) {
                String profileBase64 = ImgUtil.resizeImage(profileBlob, 300, 300); // 원하는 크기로 조정
                userMypageDTO.setProfileBase64(profileBase64);
            }

            model.addAttribute("user", userMypageDTO);
            return "info";
        } else {
            return "redirect:/user/login";
        }
    }
}
