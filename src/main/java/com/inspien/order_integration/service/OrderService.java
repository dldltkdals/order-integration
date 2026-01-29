package com.inspien.order_integration.service;

import com.inspien.order_integration.client.SftpClient;
import com.inspien.order_integration.mapper.OrderMapper.OrderMapper;
import com.inspien.order_integration.model.dto.xml.OrderRequestXml;
import com.inspien.order_integration.model.entity.OrderEntity;
import com.inspien.order_integration.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final SftpClient sftpClient;
    @Value("${app.applicant-key}")
    private String applicantKey;
    @Value("${app.user-name}")
    private String userName;
    @Transactional
    public void processOrder(OrderRequestXml xml) {
        // 1. 매핑 (XML -> Flat Entity List)
        List<OrderEntity> entities = orderMapper.toEntities(xml, applicantKey);

        // 2. DB 적재
        orderRepository.saveAll(entities);

        log.info("시나리오 1: DB 적재 완료. 건수: {}", entities.size());

        // 3. SFTP 전송 내용 생성 (영수증 포맷팅)
        String fileContent = generateReceiptContent(entities);
        // 파일명 생성 시 활용
        String fileName = String.format("INSPIEN_%s_%s.txt",
                userName,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

        // 4. SFTP 전송
        // 만약 여기서 예외가 터지면 @Transactional 덕분에 2번 과정이 롤백됨
        sftpClient.send(fileContent, fileName);
    }
    private String generateReceiptContent(List<OrderEntity> entities) {
        StringBuilder sb = new StringBuilder();
        for (OrderEntity order : entities) {
            sb.append(order.getOrderId()).append("^")
                    .append(order.getUserId()).append("^")
                    .append(order.getItemId()).append("^")
                    .append(order.getApplicantKey()).append("^")
                    .append(order.getName()).append("^")
                    .append(order.getAddress()).append("^")
                    .append(order.getItemName()).append("^")
                    .append(order.getPrice()).append("\n");
        }
        return sb.toString();
    }
}