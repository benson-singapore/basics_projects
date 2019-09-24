package com.basics.clientservicetwo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ClientController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public Object test() {
        return restTemplate.getForObject("http://CLIENT-SERVICE-ONE/test",String.class);
    }
}
