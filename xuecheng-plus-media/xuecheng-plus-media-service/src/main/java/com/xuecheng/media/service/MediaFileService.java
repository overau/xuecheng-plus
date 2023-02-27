package com.xuecheng.media.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService {

    /**
     * 媒资文件查询方法
     *
     * @param companyId           机构id
     * @param pageParams          分页条件
     * @param queryMediaParamsDto 查询条件
     * @return PageResult<MediaFiles>
     */
    PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    /**
     * 上传文件的通用接口
     * @param companyId 机构id
     * @param uploadFileParamsDto 文件信息
     * @param bytes 文件字节数组
     * @param folder 桶下边的子目录
     * @param objectName 对象名称
     * @return UploadFileResultDto
     */
    UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, byte[] bytes,
                                   String folder, String objectName);

    /**
     * 文件信息保存到数据库
     * @param companyId 机构id
     * @param uploadFileParamsDto 文件信息
     * @param bucket 桶名称
     * @param objectName 对象名称
     * @param fileId 文件id
     * @return 媒资信息
     */
    MediaFiles addMediaFilesToDb(Long companyId, UploadFileParamsDto uploadFileParamsDto,
                                        String bucket, String objectName, String fileId);

    /**
     * 文件上传前检查文件
     * @param fileMd5 文件md5
     * @return RestResponse<Boolean>
     */
    RestResponse<Boolean> checkFile(String fileMd5);

    /**
     * 分块文件上传前的检测
     * @param fileMd5 文件md5
     * @param chunkIndex 文件分块序号
     * @return RestResponse<Boolean>
     */
    RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    /**
     * 上传分块文件
     * @param bytes 文件字节数据
     * @param fileMd5 文件md5
     * @param chunkIndex 文件分块序号
     * @return RestResponse<Object>
     */
    RestResponse<?> uploadChunk(byte[] bytes, String fileMd5, int chunkIndex);

    /**
     * 合并文件
     * @param companyId 机构id
     * @param fileMd5 文件md5
     * @param chunkTotal 文件分块总数
     * @param uploadFileParamsDto 文件信息
     * @return RestResponse
     */
    RestResponse<?> mergeChunks(Long companyId, String fileMd5, int chunkTotal,
                                     UploadFileParamsDto uploadFileParamsDto);

    /**
     * 根据id查询媒资信息
     * @param id 文件id
     * @return 媒资信息
     */
    MediaFiles getPlayUrlByMediaId(String id);

    /**
     * 从minio下载文件到传入的文件对象
     *
     * @param file       需要保存到的文件
     * @param bucket     桶的名称
     * @param objectName 对象名称
     */
    void downloadFileFromMinIo(File file, String bucket, String objectName);

    /**
     * 将文件上传到分布式文件系统(minio的uploadObject上传)
     * @param filePath 文件路径
     * @param bucket 桶的名称
     * @param objectName 对象名称
     */
    void addMediaFilesToMinIo(String filePath, String bucket, String objectName);

    /**
     * 移除媒资文件
     * @param fileMd5 文件md5
     * @return 受影响的行数
     */
    int removeMediaFiles(String fileMd5);
}
