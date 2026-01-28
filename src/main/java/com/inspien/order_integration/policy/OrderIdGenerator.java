package com.inspien.order_integration.policy;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OrderIdGenerator implements IdGenerator {

    private final AtomicInteger sequence = new AtomicInteger(0);
    private char prefix = 'A';

    // 1. 서버 시작 시 DB에서 마지막 ID를 조회하여 초기화하는 로직이 필요함
    @Override
    public synchronized void initialize(String lastId) {
        if (lastId == null || lastId.isEmpty()) {
            this.prefix = 'A';
            this.sequence.set(0);
            return;
        }
        // 형식 검증 (ex: A113)
        this.prefix = lastId.charAt(0);
        this.sequence.set(Integer.parseInt(lastId.substring(1)));
    }

    @Override
    public synchronized String generateNextId() {
        int nextVal = sequence.incrementAndGet();

        // 999를 넘어가면 다음 알파벳으로 스위칭
        if (nextVal > 999) {
            sequence.set(0);
            nextVal = 0;
            prefix++;
            if (prefix > 'Z') {
                throw new IllegalStateException("모든 ID 범위를 소진했습니다.");
            }
        }

        return String.format("%c%03d", prefix, nextVal);
    }
}