package com.springboot.wooden.service;

import com.springboot.wooden.dto.*;

import java.util.List;

public interface LoginService {
    List<LoginResponseDTO> getAll();
    UserResponseDto signup(SignupRequestDto dto);

    // ✅ 로그인 결과 타입을 LoginResponseDTO로 변경
    LoginResponseDTO login(LoginRequestDto dto);
}
