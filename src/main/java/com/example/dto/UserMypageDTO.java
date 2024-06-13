package com.example.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMypageDTO {
	private String nickname;
	private String password;
    private byte[] profile; // 프로필 사진은 일단 이렇게 바이트로 한다는데 맞나
    private String username;
    private String profileBase64; // Base64 문자열로 변환된 프로필 이미지

}
