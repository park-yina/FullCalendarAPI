package com.example.service;

import com.example.entity.UserEntity;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.UserMypageDTO;


@Service
public class MypageService {

    @Autowired
    private UserRepository userRepository;
    
    public UserMypageDTO getUserMypageInfo(String username) {

        // UserEntity를 UserMypageDTO로 변환하여 반환
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        UserMypageDTO userMypageDTO = new UserMypageDTO();
        userMypageDTO.setNickname(userEntity.getNickname());
        userMypageDTO.setUsername(userEntity.getUsername());
        userMypageDTO.setProfile(userEntity.getProfile());

        return userMypageDTO;
    }

    public void updateUserMypageInfo(String username, UserMypageDTO userMypageDTO) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
        userEntity.setNickname(userEntity.getNickname());
        userEntity.setProfile(userMypageDTO.getProfile());
        userEntity.setPassword(userEntity.getPassword());
        userEntity.setUsername(username);

        userRepository.save(userEntity);
    }
}
