package com.inspien.order_integration.model.entity;

import lombok.*;

@Getter
@Setter // BeanPropertyRowMapper는 Setter를 통해 값을 주입해
@AllArgsConstructor // @Builder를 위해 필요해
@NoArgsConstructor // 이 녀석이 없어서 에러가 난 거야 (필수!)
@Builder
@ToString
public class OrderEntity {
    private  String orderId;      // 우리가 생성한 A113 형식
    private  String userId;       // XML에서 추출
    private  String itemId;       // XML에서 추출
    private  String applicantKey; // 설정 파일의 고정값
    private  String name;         // XML Header에서 조인
    private  String address;      // XML Header에서 조인
    private  String itemName;     // XML Item에서 조인
    private  String price;        // XML Item에서 조인
    private  String status;       // XML Header의 STATUS (기본 N)
}
