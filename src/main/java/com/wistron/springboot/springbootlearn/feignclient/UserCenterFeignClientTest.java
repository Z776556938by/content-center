package com.wistron.springboot.springbootlearn.feignclient;

import com.wistron.springboot.springbootlearn.domain.dto.user.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

//@FeignClient(name = "user-center", configuration = UserCenterFeignConfiguration.class) //java 配置feign
@FeignClient(name = "user-center") //请求的微服务名称
public interface UserCenterFeignClientTest {

    @GetMapping("/users/q")
    UserDTO query(@SpringQueryMap UserDTO userDTO);
}
