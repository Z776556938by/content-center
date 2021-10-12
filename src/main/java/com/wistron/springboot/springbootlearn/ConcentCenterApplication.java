package com.wistron.springboot.springbootlearn;

import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * @author K21064736
 */
@MapperScan("com.wistron")
@EnableFeignClients
@SpringBootApplication
//@EnableBinding(Source.class)
/*
@LoadBalancerClient(name = "user-center", configuration = UserCenterConfiguration.class)
*/

public class ConcentCenterApplication {

	private final WebClient.Builder loadBalancedWebClientBuilder;
	private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

	public ConcentCenterApplication(WebClient.Builder loadBalancedWebClientBuilder, ReactorLoadBalancerExchangeFilterFunction lbFunction) {
		this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
		this.lbFunction = lbFunction;
	}

	public static void main(String[] args) {
		SpringApplication.run(ConcentCenterApplication.class, args);
	}

	@Bean
	@LoadBalanced
	@SentinelRestTemplate //restTemplate 进行整合 Sentinel
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
}
