package com.springboot.wooden.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerRequestDto {
    private Long   cusNo;
    private String cusComp;   // 거래처 명
    private String cusName;   // 담당자 명
    private String cusEmail;  // 담당자 이메일
    private String cusPhone;  // 전화번호(하이픈 X)
    private String cusAddr;   // 주소
}
