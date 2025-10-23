package com.springboot.wooden.controller;

import com.springboot.wooden.dto.LoginRequestDto;
import com.springboot.wooden.dto.LoginResponseDTO;
import com.springboot.wooden.service.LoginService;
import com.springboot.wooden.service.LoginServiceImpl.AuthenticationFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000") // React 3000 포트 CORS 허용
@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {
    // 비즈니스 로직은 전부 Service로 위임
    private final LoginService loginService;

    /**
     * POST /api/login
     * - 요청 바디: { "id": "...", "pw": "..." }
     * - 성공: { success: true,  message: "로그인 성공", name: "사용자명" }
     * - 실패: { success: false, message: "이메일 또는 비밀번호가 올바르지 않습니다.", name: null }
     * - 오류: { success: false, message: "서버 오류가 발생했습니다.", name: null }
     */
    @PostMapping
    public LoginResponseDTO login(@RequestBody LoginRequestDto request) {
        try {
            // 서비스에서 아이디/비밀번호 검증. 성공 시 사용자 정보/메시지 반환
            LoginResponseDTO response = loginService.login(request);
            // 성공 케이스는 그대로 포맷 맞춰 반환
            return new LoginResponseDTO(
                    true,
                    response.getMessage(), // "로그인 성공"
                    response.getName()
            );
        } catch (AuthenticationFailedException e) {
            // 이메일/비밀번호 불일치 시
            return new LoginResponseDTO(false, "이메일 또는 비밀번호가 올바르지 않습니다.", null);
        } catch (Exception e) {
            // 그 외 예외 (서버 오류 등)
            return new LoginResponseDTO(false, "서버 오류가 발생했습니다.", null);
        }
    }
}

// api/login 으로 들어온 로그인 요청을 LoginService에 위임해 검증하고,
// 성공/실패 케이스를 LoginResponseDTO로 반환. CORS는 프런트 3000만 허용.
