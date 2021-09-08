package com.wistron.springboot.springbootlearn.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/*Feign日志配置类 还需要在配置中添加 这个类的日志级别
*
* 该类不需要添加@Configuration 否则会产生父子上下文问题 导致成为Feign全局的配置
* */

public class UserCenterFeignConfiguration {
    @Bean
    public Logger.Level level() {
        //让Feign打印所有的日志
        return Logger.Level.FULL;
    }
}
