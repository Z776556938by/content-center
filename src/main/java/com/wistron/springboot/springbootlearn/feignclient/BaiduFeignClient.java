package com.wistron.springboot.springbootlearn.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "baidu",url = "http://www.baidu.com")
public interface BaiduFeignClient {

    @GetMapping("")
    String index();
}
