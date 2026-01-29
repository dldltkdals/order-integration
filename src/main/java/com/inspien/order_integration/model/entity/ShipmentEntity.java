package com.inspien.order_integration.model.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ShipmentEntity {
    private final String shipmentId;   // 직접 채번한 A113 형식
    private final String orderId;      // 원본 주문 번호
    private final String itemId;       // 원본 아이템 번호
    private final String applicantKey; // 지원자 키
    private final String address;      // 배송 주소
}