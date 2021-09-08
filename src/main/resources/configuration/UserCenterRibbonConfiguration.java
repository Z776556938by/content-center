package configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;
import ribbonconfiguration.RibbonConfiguration;

//细粒度配置
//@Configuration
//@RibbonClient(name = "user-center", configuration = RibbonConfiguration.class)

/**
 * @author K21064736
 * 全局项配置
 */

@Configuration
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class UserCenterRibbonConfiguration {
}
