package com.wistron.springboot.springbootlearn.configuration;


import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserCenterConfiguration implements ReactorServiceInstanceLoadBalancer {

    // 服务列表
    @Autowired
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;


    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider.getIfAvailable();
        return supplier.get().next().map(this::getInstanceResponse);
    }

    /**
     * 使用随机数获取服务
     *
     * @param instances
     * @return
     */
    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances) {
        if (instances.isEmpty()) {
            return new EmptyResponse();
        }

        // 随机算法
        ServiceInstance instance = instances.get(ThreadLocalRandom.current().nextInt(instances.size()));

        return new DefaultResponse(instance);

    }
}
