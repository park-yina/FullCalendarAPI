package com.example.controller;

import com.example.dto.UserCreateDTO;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import com.example.service.NaverService;
import com.example.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Controller
@RequestMapping("/login")
public class SocialController {

    @Autowired
    private NaverService naverService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/naver")
    public String naverLogin(@RequestParam(value = "code", required = false) String code,
                             @RequestParam(value = "state", required = false) String state,
                             Model model, HttpSession session) throws Exception {
        if (code == null || state == null) {
            return "redirect:/nid.naver.com/oauth2.0/authorize";
        } else {
            String redirectURI = "http://localhost:8080/login/naver";

            // 네이버 API를 통해 Access Token 가져오기
            String accessToken = naverService.getAccessToken(code, state, redirectURI);
            session.setAttribute("accessToken",accessToken);
            // Access Token을 사용하여 네이버 사용자 정보 가져오기
            String naverProfile = naverService.getNaverProfile(accessToken);
            System.out.println("Naver Profile: " + naverProfile);

            // 가져온 프로필 정보에서 닉네임(nickname)과 이메일 추출
            String nickname = naverService.extractNicknameFromNaverProfile(naverProfile);
            String email = naverService.extractEmailFromNaverProfile(naverProfile);

            // 모델에 닉네임과 이메일 추가
            model.addAttribute("nickname", nickname);
            model.addAttribute("email", email);

            // 사용자 존재 여부 확인
            Optional<UserEntity> naverUser = userRepository.findByUsername(email);
            if (naverUser.isPresent()) {
                String username=naverUser.get().getUsername();
                session.setAttribute("username",username);
                Long userId=naverUser.get().getId();
                session.setAttribute("userId", userId);

                return "redirect:/";
            } else {
                // 사용자 정보 화면으로 이동
                return "test"; // 사용자 정보를 보여줄 뷰 페이지
            }
        }
    }

    @PostMapping("/profile/save")
    public String naverSave(@RequestParam("nickname") String nickname,
                            @RequestParam("email") String email,
                            @RequestParam("profileImage") MultipartFile profileImage,
                            Model model,HttpSession session) throws IOException {
        UserCreateDTO userCreateDTO = new UserCreateDTO();
        byte[] profileBytes = profileImage.isEmpty() ? null : profileImage.getBytes();
        userCreateDTO.setSocial(true);
        userCreateDTO.setUsername(email);
        userCreateDTO.setNickname(nickname);
        userCreateDTO.setProfile(profileBytes);
        session.setAttribute("username",email);

        UserEntity newUseruser=userService.create(userCreateDTO.getUsername(), userCreateDTO.getPassword(), userCreateDTO.getNickname(), profileBytes);
        Long userId=newUseruser.getId();
        session.setAttribute("userId", userId);

        return "redirect:/";
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        try {
            String clientId = naverService.clientId;
            String clientSecret = naverService.clientSecret;
            String accessToken = String.valueOf(session.getAttribute("accessToken"));

            String apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=delete"
                    + "&client_id=" + clientId
                    + "&client_secret=" + clientSecret
                    + "&access_token=" + accessToken
                    + "&service_provider=NAVER";

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("DELETE");

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            System.out.println("네이버 로그아웃 응답: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/";
    }
}

