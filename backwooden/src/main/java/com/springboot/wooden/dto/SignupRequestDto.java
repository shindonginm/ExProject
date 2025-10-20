package com.springboot.wooden.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * 회원가입 요청 DTO
 * - 비밀번호는 평문으로 전달되며, 서비스에서 BCrypt 해시 후 저장
 * - 사용자 이름은 화면 표시용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequestDto {

    /** 로그인 ID(이메일) - 서비스에서 trim().toLowerCase()로 정규화 예정 */
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Size(max = 50, message = "이메일은 최대 50자입니다.")
    private String loginId;

    /** 서비스에서 BCrypt.encode() 적용 */
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8~64자여야 합니다.")
    private String password;

    /** 사용자 표시 이름 */
    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 30, message = "이름은 최대 30자입니다.")
    private String userName;
}
