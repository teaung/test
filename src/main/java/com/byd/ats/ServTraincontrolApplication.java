package com.byd.ats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
@EnableCaching
public class ServTraincontrolApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServTraincontrolApplication.class, args);
	}
}
