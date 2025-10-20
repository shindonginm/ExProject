package com.springboot.wooden.controller;

import com.springboot.wooden.dto.LoginResponseDTO;
import com.springboot.wooden.dto.PlanResponseDTO;
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

    private final LoginService loginService;

    @GetMapping
    public List<LoginResponseDTO> getAll() {
        return loginService.getAll();
    }

    @PostMapping
    public UserResponseDto signup(@Valid @RequestBody SignupRequestDto request) {
        return loginService.signup(request);
    }
}
