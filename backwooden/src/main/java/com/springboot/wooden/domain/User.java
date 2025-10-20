package com.springboot.wooden.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "user_tbl")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_no")
    private Long userNo;

    @Column(name = "login_id", nullable = false, unique=true, length = 50)
    private String loginId; // 이메일(소문자 저장 권장)

    @Column(name = "password", nullable = false, length = 60)
    private String password; // BCrypt 해시("$2a$..." 형태)

    @Column(name = "user_name", nullable = false, length = 30)
    private String userName;

    /** 표시 이름 변경 */
    public void changeUserName(String newUserName) {
        requireHasText(newUserName, "userName must not be blank");
        this.userName = newUserName;
    }

    /**
     * 비밀번호 변경
     * - 반드시 서비스에서 BCrypt.encode(raw) 수행 후 해시값을 인자로 전달
     */
    public void changePassword(String newEncodedPassword) {
        requireHasText(newEncodedPassword, "encodedPassword must not be blank");
        this.password = newEncodedPassword;
    }

    private static void requireHasText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    // 식별자 기반 동등성
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return userNo != null && Objects.equals(userNo, other.userNo);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // 비밀번호는 출력 금지
    @Override
    public String toString() {
        return "User{" +
                "userNo=" + userNo +
                ", loginId='" + loginId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}