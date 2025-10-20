package com.springboot.wooden.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private boolean success;  // success : 로그인 성공 여부
    private String message;   // message : 안내 문구
    private String name;      // name : 사용자 이름
}