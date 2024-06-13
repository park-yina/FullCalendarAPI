package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {
    private String username;
    private String password;
    private String nickname;
    private String email;
    private byte[] profile;
    private Boolean social;
}
