package com.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileDTO {
    private byte[] profileImage;
    private String profileImageName;
}