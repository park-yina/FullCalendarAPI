package com.example.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Getter
@Setter
public class UserEditDTO {
  @NotBlank(message = "닉네임은 필수 입력 값입니다.")
  private String nickname;
  private String new_nickname;
  private byte[] profile; // 프로필 사진은 일단 이렇게 바이트로 한다는데 맞나
  
  private String password;
  private String new_password;
}