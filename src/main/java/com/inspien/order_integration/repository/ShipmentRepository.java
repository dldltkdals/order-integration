package com.inspien.order_integration.repository;

import com.inspien.order_integration.model.entity.OrderEntity;
import com.inspien.order_integration.model.entity.ShipmentEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ShipmentRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Value("${spring.database.order-table}")
    private String orderTable;

    @Value("${spring.database.shipment-table}")
    private String shipmentTable;

    /**
     * 1. 미전송 주문 조회 (STATUS='N' & APPLICANT_KEY)
     */
    public List<OrderEntity> findPendingOrders(String applicantKey) {
        String sql = String.format("""
            SELECT ORDER_ID, ITEM_ID, ADDRESS, APPLICANT_KEY 
            FROM %s 
            WHERE STATUS = 'N' AND APPLICANT_KEY = :applicantKey
            """, orderTable);

        MapSqlParameterSource params = new MapSqlParameterSource("applicantKey", applicantKey);

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(OrderEntity.class));
    }

    /**
     * 2. 운송 테이블 대량 적재 (Batch Insert)
     */
    public void saveAllShipments(List<ShipmentEntity> shipments) {
        if (shipments.isEmpty()) return;

        String sql = String.format("""
            INSERT INTO %s (SHIPMENT_ID, ORDER_ID, ITEM_ID, APPLICANT_KEY, ADDRESS)
            VALUES (:shipmentId, :orderId, :itemId, :applicantKey, :address)
            """, shipmentTable);

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(shipments);
        jdbcTemplate.batchUpdate(sql, batch);
        log.info("Shipment 적재 완료: {}건", shipments.size());
    }

    /**
     * 3. 주문 테이블 상태 업데이트 (Batch Update N -> Y)
     */
    public void updateOrdersStatusToY(List<String> orderIds, String applicantKey) {
        if (orderIds.isEmpty()) return;

//        String sql = String.format("UPDATE %s SET STATUS = 'Y' WHERE ORDER_ID = :orderId", orderTable);
// ShipmentRepository.java 내 수정
        String sql = """
        UPDATE RECRUIT.ORDER_TB 
        SET STATUS = 'Y' 
        WHERE ORDER_ID = :orderId 
          AND APPLICANT_KEY = :applicantKey 
          AND STATUS = 'N'
        """;
        // ID 리스트를 Batch용 파라미터 맵 배열로 변환
        // 2. 파라미터 맵에 applicantKey도 포함되도록 수정
        SqlParameterSource[] batch = orderIds.stream()
                .map(id -> new MapSqlParameterSource()
                        .addValue("orderId", id)
                        .addValue("applicantKey", applicantKey))
                .toArray(SqlParameterSource[]::new);

        try {
            jdbcTemplate.batchUpdate(sql, batch);
            log.info("시나리오 2: Order 상태 업데이트 성공 ({}건)", orderIds.size());
        } catch (Exception e) {
            // [중요] 권한 부족(ORA-01031) 발생 시 로그만 남기고 배치는 종료함
            log.error("시나리오 2: 상태 업데이트 중 권한 부족 발생(ORA-01031). " +
                    "SHIPMENT_TB 적재는 완료되었으나 STATUS 변경은 제한됨. 에러: {}", e.getMessage());
        }
    }

    /**
     * 초기화용: 마지막 SHIPMENT_ID 조회
     */
    public String findMaxShipmentId() {
        String sql = String.format("SELECT SHIPMENT_ID FROM (SELECT SHIPMENT_ID FROM %s ORDER BY SHIPMENT_ID DESC) WHERE ROWNUM = 1", shipmentTable);
        try {
            return jdbcTemplate.getJdbcOperations().queryForObject(sql, String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
