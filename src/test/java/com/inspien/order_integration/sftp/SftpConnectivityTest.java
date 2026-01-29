package com.inspien.order_integration.sftp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@ActiveProfiles("prod") // 실제 인스피언 서버 정보를 사용하기 위해 prod 프로필 활성화
class SftpConnectivityTest {

    @Autowired
    private SftpRemoteFileTemplate sftpTemplate;

    @Value("${sftp.path}") // application-prod.yml에서 정의한 /recruit/2026
    private String remotePath;

    @Test
    @DisplayName("인스피언 SFTP 서버 접속 테스트")
    void testSftpConnection() {
        assertThatCode(() -> {
            // 이제 템플릿이 경로를 알고 있으므로 파일명만 주면 돼!
            sftpTemplate.send(MessageBuilder.withPayload(
                            new ByteArrayInputStream("Test".getBytes()))
                    .setHeader(FileHeaders.FILENAME, "CONN_TEST.txt")
                    .build());
        }).doesNotThrowAnyException();
    }
}