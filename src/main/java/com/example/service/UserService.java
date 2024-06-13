package com.example.service;

import com.example.dto.UserEditDTO;
import com.example.dto.UserMypageDTO;
import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity create(String username, String password, String nickname, byte[] profile) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        user.setProfile(profile);
        user.setSocial(false);
        return userRepository.save(user);
    }
    public UserEntity login(String username, String password) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }
    public UserMypageDTO getUserMypageInfo(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        UserMypageDTO userMypageDTO = new UserMypageDTO();
        userMypageDTO.setNickname(userEntity.getNickname());
        userMypageDTO.setUsername(userEntity.getUsername());
        userMypageDTO.setProfile(userEntity.getProfile());

        return userMypageDTO;
    }

    public UserEditDTO getUserName(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저 이름으로 정보를 불러오는 것이 어렵습니다. " + username));

        UserEditDTO userEditDTO = new UserEditDTO();
        userEditDTO.setNickname(userEntity.getNickname());
        userEditDTO.setProfile(userEntity.getProfile());
        return userEditDTO;
    }

    public boolean isNicknameTaken(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public void updateNickname(String username, String newNickname) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다."));

        userEntity.setNickname(newNickname);
        userRepository.save(userEntity);
    }
    public void updatePassword(String username,String check_password) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저 이름으로 정보를 불러오는 것이 어렵습니다. " + username));
        userEntity.setPassword(passwordEncoder.encode(check_password));
        userRepository.save(userEntity);
    }

    public void saveProfileImage(String username, MultipartFile file) throws IOException {
        // 사용자 엔티티 가져오기
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저 이름으로 정보를 불러오는 것이 어렵습니다. " + username));
        if (file != null && !file.isEmpty()) {
            userEntity.setProfile(file.getBytes());
        }
        userRepository.save(userEntity);
    }

}