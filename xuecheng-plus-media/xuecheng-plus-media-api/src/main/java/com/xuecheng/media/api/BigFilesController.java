package com.xuecheng.media.api;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * 大文件上传管理
 * @author HeJin
 * @version 1.0
 * @since 2023/02/24 17:33
 */
@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {

    @Resource
    MediaFileService mediaFileService;

    /**
     * 文件上传前检查文件
     * @param fileMd5 文件md5
     * @return RestResponse
     */
    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) {
        return null;
    }


    /**
     * 分块文件上传前的检测
     * @param fileMd5 文件md5
     * @param chunk 文件分块序号
     * @return RestResponse
     */
    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) {
        return null;
    }

    /**
     * 上传分块文件
     * @param file 文件信息
     * @param fileMd5 文件md5
     * @param chunk 文件分块序号
     * @return RestResponse
     */
    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse<Object> uploadChunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) {
        return null;
    }

    /**
     * 合并文件
     * @param fileMd5 文件md5
     * @param fileName 文件名称
     * @param chunkTotal 分块总数
     * @return RestResponse
     */
    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse<Object> mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) {
        return null;
    }

}
