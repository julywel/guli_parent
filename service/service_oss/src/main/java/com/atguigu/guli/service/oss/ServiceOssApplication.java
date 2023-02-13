package com.atguigu.guli.service.oss;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

//如果有模块中或者父模块中含有mysql驱动的依赖，那么就会去检查你是否配置了相关属性，没有就会出现异常，如果你不进行使用，就使用exclude
@EnableDiscoveryClient //开启nacos客户端
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) //取消数据源自动配置
@ComponentScan("com.atguigu.guli")
public class ServiceOssApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceOssApplication.class,args);
    }
}
