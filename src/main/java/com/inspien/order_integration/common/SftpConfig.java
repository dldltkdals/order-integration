package com.inspien.order_integration.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;

@Configuration
public class SftpConfig {

    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.user}")
    private String user;
    @Value("${sftp.password}")
    private String password;

    @Bean
    public DefaultSftpSessionFactory sftpSessionFactory() {
        DefaultSftpSessionFactory factory = new DefaultSftpSessionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUser(user);
        factory.setPassword(password);
        factory.setAllowUnknownKeys(true); // 과제 환경에서는 호스트 키 검증을 건너뛰도록 설정
        return factory;
    }

    @Bean
    public SftpRemoteFileTemplate sftpRemoteFileTemplate(DefaultSftpSessionFactory factory) {
        return new SftpRemoteFileTemplate(factory);
    }
}