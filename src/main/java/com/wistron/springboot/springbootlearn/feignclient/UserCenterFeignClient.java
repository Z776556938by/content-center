package com.wistron.springboot.springbootlearn.feignclient;

import com.wistron.springboot.springbootlearn.configuration.UserCenterFeignConfiguration;
import com.wistron.springboot.springbootlearn.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class) //java 配置feign
@FeignClient(name = "user-center") //请求的微服务名称
public interface UserCenterFeignClient {

    @GetMapping("/users/{id}")
    UserDTO findById(@PathVariable Integer id);
}
