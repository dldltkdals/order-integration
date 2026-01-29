package com.inspien.order_integration.service;

import com.inspien.order_integration.model.entity.OrderEntity;
import com.inspien.order_integration.model.entity.ShipmentEntity;
import com.inspien.order_integration.policy.IdGenerator;
import com.inspien.order_integration.policy.ShipmentIdGenerator;
import com.inspien.order_integration.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShipmentBatchService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentIdGenerator shipmentIdGenerator; // ShipmentIdGenerator 주입

    @Value("${app.applicant-key}")
    private String applicantKey;

    @Scheduled(fixedDelayString = "${app.batch.interval:300000}")
    @Transactional
    public void runShipmentBatch() {
        log.info("시나리오 2: 운송 적재 배치 시작...");

        // 1. 미전송 주문 조회 (STATUS='N')
        List<OrderEntity> pendingOrders = shipmentRepository.findPendingOrders(applicantKey);

        if (pendingOrders.isEmpty()) {
            log.info("처리할 미전송 주문이 없습니다.");
            return;
        }

        // 2. Shipment 정보로 변환 및 ID 생성
        List<ShipmentEntity> shipments = pendingOrders.stream()
                .map(order -> ShipmentEntity.builder()
                        .shipmentId(shipmentIdGenerator.generateNextId()) // 여기서 A001 등 생성
                        .orderId(order.getOrderId())
                        .itemId(order.getItemId())
                        .applicantKey(order.getApplicantKey())
                        .address(order.getAddress())
                        .build())
                .collect(Collectors.toList());

        // 3. SHIPMENT_TB 적재 (Batch Insert)
        shipmentRepository.saveAllShipments(shipments);

        // 4. ORDER_TB 상태 업데이트 (N -> Y)
        List<String> orderIds = pendingOrders.stream()
                .map(OrderEntity::getOrderId)
                .collect(Collectors.toList());
        shipmentRepository.updateOrdersStatusToY(orderIds,applicantKey);

        log.info("시나리오 2 완료: {}건의 주문이 운송사로 전달되었습니다.", shipments.size());
    }

}
