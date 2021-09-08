package com.wistron.springboot.springbootlearn;

import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class ConcentCenterApplicationTest {
    public static void main(String[] args) throws InterruptedException {
        RestTemplate restTemplate = new RestTemplate();
        for (int i = 0; i < 10000; i++) {
            String forObject = restTemplate.getForObject("http://localhost:8081/actuator/sentinel", String.class);

            Thread.sleep(100);
        }
    }
}