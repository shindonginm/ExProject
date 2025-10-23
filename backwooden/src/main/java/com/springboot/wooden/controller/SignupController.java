package com.springboot.wooden.controller;

import com.springboot.wooden.dto.LoginResponseDTO;
import com.springboot.wooden.dto.SignupRequestDto;
import com.springboot.wooden.dto.UserResponseDto;
import com.springboot.wooden.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class SignupController {
    // 실제 로직은 서비스로 위임
    private final LoginService loginService;

    // User 목록 조회
    @GetMapping
    public List<LoginResponseDTO> getAll() {
        return loginService.getAll();
    }
    // User 회원 가입
    @PostMapping
    public UserResponseDto signup(@Valid @RequestBody SignupRequestDto request) {
        return loginService.signup(request);
    }
}

// 회원 가입 전용 REST 컨트롤러. 프론트 3000 포트에서 오는 CORS 허용, 전체 사용자 조회와 신규 가입을 LoginService로 위임