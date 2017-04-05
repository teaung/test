package com.byd.ats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ServTraincontrolApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServTraincontrolApplication.class, args);
	}
}
