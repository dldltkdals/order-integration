package com.inspien.order_integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OrderIntegrationApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderIntegrationApplication.class, args);
	}

}
