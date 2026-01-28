package com.inspien.order_integration.common;

import com.inspien.order_integration.policy.IdGenerator;
import com.inspien.order_integration.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class IdInitializerConfig {
    private final OrderRepository orderRepository;
    private final IdGenerator orderIdGenerator;
    @Bean
    public CommandLineRunner initId() {
        return args -> {
            log.info("시퀀스 초기화를 위해 DB 조회를 시작합니다...");
            String lastId = orderRepository.findMaxOrderId();
            orderIdGenerator.initialize(lastId);
            log.info("ID 생성기 초기화 완료. 마지막 ID: {}, 다음 예상 ID: {}",
                    lastId == null ? "없음" : lastId,
                    "미리보기 불가(호출 시 생성)");
        };
    }
}
