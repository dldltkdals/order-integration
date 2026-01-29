package com.inspien.order_integration.policy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component("shipmentIdGenerator")
public class ShipmentIdGenerator implements IdGenerator{
    private final AtomicInteger sequence = new AtomicInteger(0);
    private char prefix = 'A';

    @Override
    public synchronized void initialize(String lastId) {
        if (lastId == null || lastId.isEmpty()) {
            this.prefix = 'A';
            this.sequence.set(0);
            log.info("[ShipmentId] 초기값으로 세팅 (A000)");
            return;
        }

        try {
            // "A113" -> prefix: 'A', sequence: 113
            this.prefix = lastId.charAt(0);
            this.sequence.set(Integer.parseInt(lastId.substring(1)));
            log.info("[ShipmentId] 초기화 완료: 마지막 ID={}, 다음 시작={}{}", lastId, prefix, sequence.get() + 1);
        } catch (Exception e) {
            log.warn("[ShipmentId] 파싱 실패로 초기값 세팅. 입력값: {}", lastId);
            this.prefix = 'A';
            this.sequence.set(0);
        }
    }

    @Override
    public synchronized String generateNextId() {
        int nextVal = sequence.incrementAndGet();

        // 999를 넘어가면 다음 알파벳으로 변경 (A999 -> B001)
        if (nextVal > 999) {
            sequence.set(1); // 001부터 다시 시작
            nextVal = 1;
            prefix++;

            if (prefix > 'Z') {
                log.error("[ShipmentId] 모든 ID 범위를 소진했습니다 (Z999 초과)");
                throw new IllegalStateException("ID 생성 범위를 초과했습니다.");
            }
            log.info("[ShipmentId] 접두어 변경됨: {}", prefix);
        }

        return String.format("%c%03d", prefix, nextVal);
    }
}
