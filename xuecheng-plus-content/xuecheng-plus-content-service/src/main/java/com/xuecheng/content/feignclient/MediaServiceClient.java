package com.xuecheng.content.feignclient;

import com.xuecheng.content.config.MultipartSupportConfig;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/03/07 13:51
 */
@FeignClient(value = "media-api",configuration = MultipartSupportConfig.class)
public interface MediaServiceClient {

    /**
     * 上传文件
     * @param file MultipartFile
     * @param folder 文件目录
     * @param objectName 文件名称
     * @return 上传文件成功响应结果
     */
    @ApiOperation("上传文件")
    @RequestMapping(value = "/media/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    String upload(
            @RequestPart("filedata") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "objectName", required = false) String objectName);

}