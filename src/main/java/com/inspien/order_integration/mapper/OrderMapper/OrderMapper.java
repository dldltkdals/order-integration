package com.inspien.order_integration.mapper.OrderMapper;

import com.inspien.order_integration.model.dto.xml.OrderRequestXml;
import com.inspien.order_integration.model.entity.OrderEntity;
import com.inspien.order_integration.policy.IdGenerator;
import com.inspien.order_integration.policy.OrderIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderIdGenerator orderIdGenerator;
    /**
     * XML 데이터를 DB 저장용 Flat Entity 리스트로 변환
     */
    public List<OrderEntity> toEntities(OrderRequestXml xml, String applicantKey) {
        if (xml == null || xml.getItems() == null) {
            return Collections.emptyList();
        }

        // 1. Header 리스트를 Map으로 변환 (Key: USER_ID) - Hash Join 준비
        Map<String, OrderRequestXml.HeaderXml> headerMap = xml.getHeaders().stream()
                .collect(Collectors.toMap(
                        OrderRequestXml.HeaderXml::getUserId,
                        header -> header,
                        (existing, replacement) -> existing // 중복 USER_ID 발생 시 기존 것 유지 (Idempotency)
                ));

        // 2. Item을 기준으로 Flat 구조 생성
        return xml.getItems().stream()
                .map(item -> {
                    OrderRequestXml.HeaderXml header = headerMap.get(item.getUserId());

                    if (header == null) {
                        log.warn("매칭되는 Header가 없는 Item 발견: USER_ID={}", item.getUserId());
                        return null; // 또는 예외 던지기
                    }

                    // 3. Entity 빌드 (평탄화 작업)
                    return OrderEntity.builder()
                            .orderId(orderIdGenerator.generateNextId()) // 정책에 따른 A113 형식 생성
                            .userId(item.getUserId())
                            .itemId(item.getItemId())
                            .applicantKey(applicantKey)
                            .name(header.getName())
                            .address(header.getAddress())
                            .itemName(item.getItemName())
                            .price(item.getPrice())
                            .status(header.getStatus() != null ? header.getStatus().trim() : "N")
                            .build();
                })
                .filter(java.util.Objects::nonNull) // Header가 없어 null 반환된 건 제외
                .collect(Collectors.toList());
    }
}

