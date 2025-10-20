package com.springboot.wooden.service;

import com.springboot.wooden.domain.User;
import com.springboot.wooden.dto.LoginRequestDto;
import com.springboot.wooden.dto.LoginResponseDTO;
import com.springboot.wooden.dto.SignupRequestDto;
import com.springboot.wooden.dto.UserResponseDto;
import com.springboot.wooden.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 인증 서비스 구현
 * - DTO 검증(@Valid)은 컨트롤러에서, 여기서는 비즈니스 규칙 처리
 */
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입
     * 흐름: 이메일 정규화 → 중복 확인 → 비번 해시 → 저장 → 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public List<LoginResponseDTO> getAll() {
        return userRepository.findAll()
                .stream()
                .map(user -> new LoginResponseDTO(
                        true,                        // success: 단순히 존재하는 유저이므로 true
                        "회원 데이터 조회 성공",         // message
                        user.getUserName()           // name
                ))
                .collect(Collectors.toList());
    }
    @Override
    @Transactional
    public UserResponseDto signup(SignupRequestDto dto) {
        // 1) 이메일 정규화(트림 + 소문자)
        String email = normalizeEmail(dto.getLoginId());

        // 2) 중복 검사 (경합 상황은 DB UNIQUE가 최종 방어)
        if (userRepository.existsByLoginId(email)) {
            // GlobalExceptionHandler에서 409로 매핑되는 예외 사용
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        // 3) 비밀번호 해시
        String hashed = passwordEncoder.encode(dto.getPassword());

        // 4) 저장 (엔티티는 이미 해시된 값만 받음)
        User saved = userRepository.save(User.builder()
                .loginId(email)
                .password(hashed)
                .userName(dto.getUserName())
                .build());

        // 5) 응답 DTO
        return UserResponseDto.from(saved);
    }

    /**
     * 로그인
     * 흐름: 이메일 정규화 → 사용자 조회 → 비밀번호 대조 → 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDto dto) {
        String email = normalizeEmail(dto.getLoginId());

        User user = userRepository.findByLoginId(email)
                .orElseThrow(() ->
                        new AuthenticationFailedException("이메일 또는 비밀번호가 올바르지 않습니다.")
                );

        boolean ok = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        if (!ok) {
            throw new AuthenticationFailedException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // ✅ 로그인 성공 시 LoginResponseDTO로 반환
        return new LoginResponseDTO(true, "로그인 성공", user.getUserName());
    }

    // ===== helpers =====

    private String normalizeEmail(String raw) {
        if (raw == null) {
            return null;
        }
        return raw.trim().toLowerCase();
    }

    /**
     * 로그인 실패 전용 예외
     * - GlobalExceptionHandler에 401 매핑을 추가해서 사용하세요.
     *   예: @ExceptionHandler(AuthenticationFailedException.class) → HttpStatus.UNAUTHORIZED
     */
    public static class AuthenticationFailedException extends RuntimeException {
        public AuthenticationFailedException(String message) {
            super(message);
        }
    }
}
