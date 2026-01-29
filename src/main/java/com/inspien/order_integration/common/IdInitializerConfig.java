package com.inspien.order_integration.common;

import com.inspien.order_integration.policy.IdGenerator;
import com.inspien.order_integration.repository.OrderRepository;
import com.inspien.order_integration.repository.ShipmentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class IdInitializerConfig {
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;

    private final IdGenerator orderIdGenerator;
    private final IdGenerator shipmentIdGenerator;
    // 생성자를 직접 작성하고 파라미터 앞에 @Qualifier를 붙여줘
    public IdInitializerConfig(
            OrderRepository orderRepository,
            ShipmentRepository shipmentRepository,
            @Qualifier("orderIdGenerator") IdGenerator orderIdGenerator,
            @Qualifier("shipmentIdGenerator") IdGenerator shipmentIdGenerator) {
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
        this.orderIdGenerator = orderIdGenerator;
        this.shipmentIdGenerator = shipmentIdGenerator;
    }
    @PostConstruct
    public void init() {
        // 1. 주문 ID 초기화
        String lastOrderId = orderRepository.findMaxOrderId();
        orderIdGenerator.initialize(lastOrderId);

        // 2. 운송 ID 초기화
        String lastShipmentId = shipmentRepository.findMaxShipmentId();
        shipmentIdGenerator.initialize(lastShipmentId);
    }
}
