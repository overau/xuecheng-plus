package com.xuecheng.media.api;

import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 媒资文件管理接口
 *
 * @author Mr.M
 * @version 1.0
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    @Resource
    MediaFileService mediaFileService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1232141425L;
        return mediaFileService.queryMediaFiles(companyId, pageParams, queryMediaParamsDto);
    }

    /**
     * 上传文件
     * @param file MultipartFile
     * @param folder 文件目录
     * @param objectName 文件名称
     * @return 上传文件成功响应结果
     */
    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public UploadFileResultDto upload(
            @RequestPart("filedata") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "objectName", required = false) String objectName) {
        Long companyId = 1L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        uploadFileParamsDto.setFilename(file.getOriginalFilename());
        String contentType = file.getContentType();
        uploadFileParamsDto.setContentType(contentType);
        assert contentType != null;
        if (contentType.contains(SysConstants.IMAGE_TYPE)) {
            uploadFileParamsDto.setFileType("001001");
        } else {
            uploadFileParamsDto.setFileType("001003");
        }
        uploadFileParamsDto.setFileSize(file.getSize());

        UploadFileResultDto uploadFileResultDto;
        try {
            uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, file.getBytes(),
                    folder, objectName);
        } catch (IOException e) {
            throw new XueChengPlusException("上传文件过程中出错!");
        }

        return uploadFileResultDto;
    }

    @ApiOperation("预览文件")
    @GetMapping("/preview/{mediaId}")
    public RestResponse<String> getPlayUrlByMediaId(@PathVariable("mediaId") String mediaId){
        MediaFiles mediaFiles = mediaFileService.getPlayUrlByMediaId(mediaId);
        return RestResponse.success(mediaFiles.getUrl());
    }

}
