package com.byd.ats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@EnableTransactionManagement  // 启注解事务管理，等同于xml配置方式的 <tx:annotation-driven />
@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
@EnableCaching
public class ServTraincontrolApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServTraincontrolApplication.class, args);
	}
	
    @LoadBalanced
    @Bean
    public RestTemplate restTemplate() {  
        return new RestTemplate();
    }
}
