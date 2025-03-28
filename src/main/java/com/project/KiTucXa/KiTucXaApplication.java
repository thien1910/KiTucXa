package com.project.KiTucXa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling // Cho phép sử dụng @Scheduled

@SpringBootApplication
public class KiTucXaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiTucXaApplication.class, args);
	}

}
