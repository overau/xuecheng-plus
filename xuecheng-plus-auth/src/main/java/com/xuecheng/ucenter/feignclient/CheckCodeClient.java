package com.xuecheng.ucenter.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 验证码服务远程接口
 * @author HeJin
 * @version 1.0
 * @since 2023/03/08 16:52
 */
@FeignClient(value = "checkcode", fallbackFactory = CheckCodeClientFactory.class)
public interface CheckCodeClient {

    /**
     * 验证码校验服务
     * @param key key
     * @param code code
     * @return 校验结果
     */
    @PostMapping(value = "/checkcode/verify")
    Boolean verify(@RequestParam("key") String key, @RequestParam("code") String code);

}
