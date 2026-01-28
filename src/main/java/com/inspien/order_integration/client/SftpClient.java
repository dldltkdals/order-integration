package com.inspien.order_integration.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class SftpClient {

    private final SftpRemoteFileTemplate sftpTemplate;

    @Value("${sftp.path}")
    private String remoteDirectory;

    public void send(String content, String fileName) {
        try {
            log.info("SFTP 전송 시작: {}", fileName);

            // 텍스트 내용을 바이트 스트림으로 변환하여 전송
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

            sftpTemplate.send(MessageBuilder.withPayload(new ByteArrayInputStream(bytes))
                    .setHeader("file_name", fileName)
                    .setHeader("file_remoteDirectory", remoteDirectory)
                    .build());

            log.info("SFTP 전송 완료: {}", fileName);
        } catch (Exception e) {
            log.error("SFTP 전송 중 치명적 오류 발생: {}", e.getMessage());
            throw new RuntimeException("SFTP 전송 실패", e); // 예외를 던져서 DB 롤백 유도
        }
    }
}