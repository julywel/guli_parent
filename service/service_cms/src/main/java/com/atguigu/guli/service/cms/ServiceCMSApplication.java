package com.atguigu.guli.service.cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"com.atguigu.guli"})
@EnableFeignClients
public class ServiceCMSApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCMSApplication.class,args);
    }
}
