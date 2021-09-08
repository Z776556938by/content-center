package com.wistron.springboot.springbootlearn.service.content;

import com.wistron.springboot.springbootlearn.dao.content.ShareMapper;
import com.wistron.springboot.springbootlearn.domain.dto.content.ShareDTO;
import com.wistron.springboot.springbootlearn.domain.dto.user.UserDTO;
import com.wistron.springboot.springbootlearn.domain.entity.content.Share;
import com.wistron.springboot.springbootlearn.feignclient.UserCenterFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author K21064736
 * @Description TODO
 * @Date 10:45 2021/8/24
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ShareService {
    private final ShareMapper shareMapper;
    private final UserCenterFeignClient userCenterFeignClient;
//    private final RestTemplate restTemplate;   //引入 feign 进行解决Http 微服务请求
//    private final DiscoveryClient discoveryClient;  //引入Ribbon

    public ShareDTO findById(Integer id) {
        //获取分享的内容
        Share share = this.shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
//        使用discoveryClient 进行服务发现 获取URL地址
//        List<ServiceInstance> instances = this.discoveryClient.getInstances("user-center");
//        String targetUrl = instances.stream()
//                .map(instance -> instance.getUri().toString() + "/users/{id}")
//                .findFirst()
//                .orElseThrow(()-> new IllegalArgumentException("当前没有实例"));

//        int ints = ThreadLocalRandom.current().nextInt(instances.size());  //获取一个随机数 ThreadLocalRandom线程安全

       /*使用Ribbon会自动将user-center替换 user-center在nacons中的实例 并负载均衡后的URL*/
//        UserDTO userDTO = this.restTemplate.getForObject(
//                "http://user-center/users/{id}",
//                UserDTO.class,
//                userId
//        );

        /*使用Feign 替代resttemplate */
        UserDTO userDTO = this.userCenterFeignClient.findById(userId);

        ShareDTO shareDTO = ShareDTO.builder().build();
        //进行前端所需信息装配
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setWxNickname(userDTO.getWxNickname());
        return shareDTO;
    }
}
