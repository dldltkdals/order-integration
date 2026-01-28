package com.inspien.order_integration.repository;

import com.inspien.order_integration.model.entity.OrderEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Value("${spring.database.order-table}")
    private String orderTableName;
    /**
     * NamedParameterJdbcTemplate을 사용한 깔끔한 Batch Insert
     */
    @Transactional
    public void saveAll(List<OrderEntity> orders) {
        if (orders.isEmpty()) return;

        // 1. SQL 가독성 확보 (물음표 대신 파라미터명 사용)
        String sql = String.format("""
            INSERT INTO %s (
                ORDER_ID, USER_ID, ITEM_ID, APPLICANT_KEY, NAME, ADDRESS, ITEM_NAME, PRICE, STATUS
            ) VALUES (
                :orderId, :userId, :itemId, :applicantKey, :name, :address, :itemName, :price, :status
            )
            """, orderTableName);

        try {
            // 2. Entity 리스트를 배치를 위한 파라미터 배열로 자동 변환
            SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(orders);

            // 3. 실행 (익명 클래스 없이 단 한 줄!)
            namedParameterJdbcTemplate.batchUpdate(sql, batch);

            log.info("Batch insert 완료: {}건 적재됨", orders.size());
        } catch (Exception e) {
            log.error("Batch 적재 실패. 원인: {}", e.getMessage());
            throw new RuntimeException("DB 적재 오류", e);
        }
    }



    public String findMaxOrderId() {
        // 가장 최근에 생성된 ID 하나를 가져오는 쿼리 (Oracle 기준)
        String sql = String.format("SELECT ORDER_ID FROM (SELECT ORDER_ID FROM %s ORDER BY ORDER_ID DESC) WHERE ROWNUM = 1", orderTableName);
        try {
            // namedParameterJdbcTemplate 내부의 JdbcOperations(JdbcTemplate)을 직접 사용
            return namedParameterJdbcTemplate.getJdbcOperations()
                    .queryForObject(sql, String.class);
        } catch (EmptyResultDataAccessException e) {
            // 데이터가 하나도 없을 때 발생하는 표준 예외 처리
            log.info("{} 테이블이 비어있습니다. 초기값으로 시작합니다.", orderTableName);
            return null;
        } catch (Exception e) {
            log.error("최근 ORDER_ID 조회 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }
}
