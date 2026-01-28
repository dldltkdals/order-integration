package com.inspien.order_integration.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class OrderEntity {
    private final String orderId;      // 우리가 생성한 A113 형식
    private final String userId;       // XML에서 추출
    private final String itemId;       // XML에서 추출
    private final String applicantKey; // 설정 파일의 고정값
    private final String name;         // XML Header에서 조인
    private final String address;      // XML Header에서 조인
    private final String itemName;     // XML Item에서 조인
    private final String price;        // XML Item에서 조인
    private final String status;       // XML Header의 STATUS (기본 N)
}
