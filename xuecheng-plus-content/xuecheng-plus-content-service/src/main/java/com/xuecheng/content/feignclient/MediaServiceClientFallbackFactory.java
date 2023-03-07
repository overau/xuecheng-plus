package com.xuecheng.content.feignclient;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 媒资管理服务上传文件降级
 * @author HeJin
 * @version 1.0
 * @since 2023/03/07 14:41
 */
@Component
@Slf4j
public class MediaServiceClientFallbackFactory implements FallbackFactory<MediaServiceClient> {

    @Override
    public MediaServiceClient create(Throwable throwable) {
        return (file, folder, objectName) -> {
            // 降级方法
            log.debug("调用媒资管理服务上传文件发生熔断: {}", throwable.getMessage());
            return "媒资管理服务上传文件降级";
        };
    }

}
