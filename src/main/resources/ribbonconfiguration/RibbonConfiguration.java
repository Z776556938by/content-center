package ribbonconfiguration;

import com.netflix.loadbalancer.IRule;
import com.wistron.springboot.springbootlearn.configuration.NacosSameClusterWeightedRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RibbonConfiguration {
    @Bean
    public IRule ribbonRule() {
        return new NacosSameClusterWeightedRule();
    }

    //自定义各项配置 同上
//    @Bean
//    public IPing ping() {
//        return new PingUrl();
//    }
}
