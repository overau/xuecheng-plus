package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/02/19 16:36
 */
@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})
@SpringBootApplication(scanBasePackages = {"com.xuecheng.content", "com.xuecheng.messagesdk"})
public class ContentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentServiceApplication.class, args);
    }

}
