package com.sam.InsuranceManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InsuranceManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(InsuranceManagementApplication.class, args);
	}
}
