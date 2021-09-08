package configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.core.Balancer;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class NacosSameClusterWeightedRule extends AbstractLoadBalancerRule {
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {

    }

    @Override
    public Server choose(Object o) {
        try {
            BaseLoadBalancer loadBalancer = (BaseLoadBalancer) this.getLoadBalancer();

            //获取集群的名臣
            String clusterName = nacosDiscoveryProperties.getClusterName();

            //获取微服务的名字
            String name = loadBalancer.getName();

            //获取服务发现的API
            NamingService namingService = this.nacosDiscoveryProperties.namingServiceInstance();
            //1 通过API获取所有的健康的实例  true 表示健康
            List<Instance> instances = namingService.selectInstances(name, true);

            //2 进行过滤同一集群下的实例
            List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(instance.getClusterName(), clusterName))
                    .collect(Collectors.toList());


            List<Instance> instancesChosen = new ArrayList<>();
            //3 如果同一集群下实例为空 在用其他集群
            if (sameClusterInstances.isEmpty()) {
                instancesChosen = instances;
                log.warn("发生跨集群调用, name = {}, cluster = {}, instances = {}", name, clusterName, instances);
            } else {
                instancesChosen = sameClusterInstances;
            }
            // 4 基于复杂均衡算法 返回一个实例
            Instance instanceFinal = ExtendsBalancer.getInstanceByRandomWeight(instancesChosen);

//            log.info("选择的实例是 port = {}, instance = {}", instanceFinal.getPort(), instanceFinal);
            return new NacosServer(instanceFinal);
        } catch (NacosException e) {
            e.printStackTrace();
             log.error("发生异常", e);
            return null;
        }
    }
}

class ExtendsBalancer extends Balancer {
    public static Instance getInstanceByRandomWeight(List<Instance> hosts) {
        return getHostByRandomWeight(hosts);
    }
}