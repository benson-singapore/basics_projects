package com.basics.clientserviceone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 主方法
 *
 * @author zhangby
 * @date 24/9/19 2:02 pm
 */

@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class ClientServiceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientServiceOneApplication.class, args);
    }

    @GetMapping("/test")
    public Object test() {
        return "this is client-service-one";
    }

}
