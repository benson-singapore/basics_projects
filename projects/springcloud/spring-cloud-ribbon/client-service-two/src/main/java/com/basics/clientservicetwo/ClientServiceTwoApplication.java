package com.basics.clientservicetwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 主方法
 *
 * @author zhangby
 * @date 24/9/19 2:08 pm
 */
@EnableDiscoveryClient
@SpringBootApplication
public class ClientServiceTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientServiceTwoApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
