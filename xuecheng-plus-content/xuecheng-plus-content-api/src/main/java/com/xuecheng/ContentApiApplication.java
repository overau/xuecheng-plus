package com.xuecheng;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author HeJin
 * @date 2023/02/19 14:24
 */
@EnableSwagger2Doc
@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})
@SpringBootApplication
public class ContentApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContentApiApplication.class, args);
    }

}
