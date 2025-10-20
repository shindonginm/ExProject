package com.springboot.wooden.dto;

import com.springboot.wooden.domain.User;
import lombok.*;

/**
 * 사용자 응답 DTO
 * - 비밀번호 제외
 * - 회원가입/로그인/조회 응답에 공통 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long userNo;
    private String loginId;   // 이메일
    private String userName;

    public static UserResponseDto from(User u) {
        return UserResponseDto.builder()
                .userNo(u.getUserNo())
                .loginId(u.getLoginId())
                .userName(u.getUserName())
                .build();
    }
}
