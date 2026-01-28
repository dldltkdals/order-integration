package com.inspien.order_integration.contoller;

import com.inspien.order_integration.model.dto.xml.OrderRequestXml;
import com.inspien.order_integration.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(consumes = "application/xml", produces = "application/json")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestXml requestXml) {
        // 시나리오 1: 주문 적재 및 처리
        orderService.processOrder(requestXml);

        return ResponseEntity.ok(Map.of(
                "result", "SUCCESS",
                "message", "주문이 성공적으로 적재되었습니다."
        ));
    }
}