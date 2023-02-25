package com.xuecheng.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.model.dto.QueryMediaParamsDto;
import com.xuecheng.media.model.dto.UploadFileParamsDto;
import com.xuecheng.media.model.dto.UploadFileResultDto;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.service.MediaFileService;
import io.minio.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */
@Service
@Slf4j
public class MediaFileServiceImpl implements MediaFileService {

    @Resource
    MediaFilesMapper mediaFilesMapper;

    @Resource
    MinioClient minioClient;

    @Resource
    private MediaFileService currentProxy;

    @Value("${minio.bucket.files}")
    private String bucketFiles;

    @Value("${minio.bucket.videofiles}")
    private String bucketVideoFiles;

    @Override
    public PageResult<MediaFiles> queryMediaFiles(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());
    }

    /**
     * 上传文件的通用接口
     *
     * @param companyId           机构id
     * @param uploadFileParamsDto 文件信息
     * @param bytes               文件字节数组
     * @param folder              桶下边的子目录
     * @param objectName          对象名称
     * @return UploadFileResultDto
     */
    @Override
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto,
                                          byte[] bytes, String folder, String objectName) {
        String fileMd5 = DigestUtils.md5Hex(bytes);
        if (StringUtils.isBlank(folder)){
            // 自动生成目录的路径: 年/月/日
            folder = this.getFileFolder(new Date(), true, true, true);
        } else if (!folder.contains(SysConstants.FILE_SEPARATOR)){
            folder = folder + SysConstants.FILE_SEPARATOR;
        }

        String filename = uploadFileParamsDto.getFilename();
        if (StringUtils.isBlank(objectName)){
            // objectName为空，使用文件的md5值为objectName
            objectName = fileMd5 + filename.substring(filename.lastIndexOf('.'));
        }
        objectName = folder + objectName;

        // 1.上传文件到minio
        this.addMediaFilesToMinIo(bytes, bucketFiles, objectName);
        // 2.保存到数据库: 获取代理对象(事务)
        MediaFiles mediaFiles = currentProxy.addMediaFilesToDb(companyId, uploadFileParamsDto, bucketFiles, objectName, fileMd5);
        // 3.准备返回数据
        UploadFileResultDto uploadFileResultDto = new UploadFileResultDto();
        BeanUtils.copyProperties(mediaFiles, uploadFileResultDto);
        return uploadFileResultDto;
    }

    /**
     * 文件信息保存到数据库
     * @param companyId 机构id
     * @param uploadFileParamsDto 文件信息
     * @param bucket 桶名称
     * @param objectName 对象名称
     * @param fileId 文件id
     * @return 媒资信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MediaFiles addMediaFilesToDb(Long companyId, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName, String fileId) {
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (null == mediaFiles){
            mediaFiles = new MediaFiles();
            BeanUtils.copyProperties(uploadFileParamsDto, mediaFiles);
            mediaFiles.setId(fileId);
            mediaFiles.setFileId(fileId);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setStatus("1");
            mediaFiles.setAuditStatus("002003");
            // 插入文件表
            mediaFilesMapper.insert(mediaFiles);
        }
        return mediaFiles;
    }

    /**
     * 文件上传前检查文件
     *
     * @param fileMd5 文件md5
     * @return RestResponse<Boolean>
     */
    @Override
    public RestResponse<Boolean> checkFile(String fileMd5) {
        // 1.检查数据库表是否存在上传记录
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (null == mediaFiles){
            return RestResponse.success(false);
        }
        // 2.检查minio文件系统是否存在文件
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(mediaFiles.getBucket())
                .object(mediaFiles.getFilePath())
                .build();
        try {
            GetObjectResponse response = minioClient.getObject(objectArgs);
            if (null == response){
                return RestResponse.success(false);
            }
        } catch (Exception e) {
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    /**
     * 分块文件上传前的检测
     *
     * @param fileMd5    文件md5
     * @param chunkIndex 文件分块序号
     * @return RestResponse<Boolean>
     */
    @Override
    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex) {
        String chunkName = this.getChunkFileFolderPath(fileMd5) + chunkIndex;
        GetObjectArgs objectArgs = GetObjectArgs.builder()
                .bucket(bucketVideoFiles)
                .object(chunkName)
                .build();
        try {
            GetObjectResponse response = minioClient.getObject(objectArgs);
            if (response == null){
                return RestResponse.success(false);
            }
        } catch (Exception e){
            return RestResponse.success(false);
        }
        return RestResponse.success(true);
    }

    /**
     * 上传分块文件
     *
     * @param bytes      文件字节数据
     * @param fileMd5    文件md5
     * @param chunkIndex 文件分块序号
     * @return RestResponse<Object>
     */
    @Override
    public RestResponse<?> uploadChunk(byte[] bytes, String fileMd5, int chunkIndex) {
        String chunkName = this.getChunkFileFolderPath(fileMd5) + chunkIndex;
        try {
            this.addMediaFilesToMinIo(bytes, bucketVideoFiles, chunkName);
            return RestResponse.success(true);
        } catch (Exception e) {
            log.error("上传分块文件: {}, 失败: {}", chunkName, e.getMessage());
        }
        return RestResponse.validFail(false, "上传分块失败!");
    }

    /**
     * 合并文件
     *
     * @param companyId           机构id
     * @param fileMd5             文件md5
     * @param chunkTotal          文件分块总数
     * @param uploadFileParamsDto 文件信息
     * @return RestResponse
     */
    @Override
    public RestResponse<?> mergeChunks(Long companyId, String fileMd5, int chunkTotal,
                                       UploadFileParamsDto uploadFileParamsDto) {
        // 1.下载分块文件
        File[] chunkFiles = this.downloadChunk(fileMd5, chunkTotal);
        // 2.合并分块文件
        this.mergeChunkFile(companyId, fileMd5, uploadFileParamsDto, chunkFiles);
        return RestResponse.success(true);
    }

    /**
     * 合并分块文件
     * @param companyId 结构id
     * @param fileMd5 文件md5
     * @param uploadFileParamsDto 文件信息
     * @param files 分块文件数组: 分块文件名称为数字，升序排列
     */
    private void mergeChunkFile(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, File[] files) {
        String filename = uploadFileParamsDto.getFilename();
        String extension = filename.substring(filename.lastIndexOf(SysConstants.DOT));
        // 临时合并文件
        File mergeFile = null;
        try {
            try {
                mergeFile = File.createTempFile(SysConstants.TEMP_MERGE_PREDIX, extension);
            } catch (IOException e) {
                log.error("创建临时合并文件出错: {}", e.getMessage());
                throw new XueChengPlusException("创建临时合并文件出错！");
            }
            try (RandomAccessFile rafWrite = new RandomAccessFile(mergeFile, "rw")) {
                byte[] bytes = new byte[1024];
                for (File file : files) {
                    RandomAccessFile rafRead = new RandomAccessFile(file, "r");
                    int len;
                    while ((len = rafRead.read(bytes)) != -1){
                        rafWrite.write(bytes, 0, len);
                    }
                    rafRead.close();
                }
            } catch (IOException e) {
                log.error("文件合并过程出错: {}", e.getMessage());
                throw new XueChengPlusException("文件合并过程出错!");
            }
            // 校验文件
            try (FileInputStream fileInputStream = new FileInputStream(mergeFile)) {

                String mergeFileMd5 = DigestUtils.md5Hex(fileInputStream);
                if (!fileMd5.equals(mergeFileMd5)){
                    log.error("文件校验不通过, 文件路径: {}, 原始文件md5: {}", mergeFile.getAbsolutePath(), fileMd5);
                    throw new XueChengPlusException("文件校验不通过!");
                }
            } catch (IOException e) {
                log.error("文件校验失败, 文件路径: {}, 原始文件md5: {}", mergeFile.getAbsolutePath(), fileMd5);
                throw new XueChengPlusException("文件校验失败!");
            }
            // 文件写入minio
            String mergeFilePath = this.getFilePathByMd5(fileMd5, extension);
            this.addMediaFilesToMinIo(mergeFile.getAbsolutePath(), bucketVideoFiles, mergeFilePath);
            // 文件入库
            uploadFileParamsDto.setFileSize(mergeFile.length());
            currentProxy.addMediaFilesToDb(companyId, uploadFileParamsDto, bucketVideoFiles, mergeFilePath, fileMd5);
        } finally {
            // 删除临时分块文件和合并临时文件
            for (File file : files) {
                this.deleteTempFile(file);
            }
            this.deleteTempFile(mergeFile);
        }
    }

    /**
     * 删除文件
     * @param file 要删除的文件
     */
    private void deleteTempFile(File file) {
        if (file != null && file.exists()){
            boolean isDelete = file.delete();
            if (!isDelete){
                log.error("删除文件失败, 文件路径: {}", file.getAbsolutePath());
                throw new XueChengPlusException("删除文件失败!");
            }
        }
    }

    /**
     * 通过文件md5和后缀获得完整路径
     * @param fileMd5 文件md5
     * @param fileExt 文件后缀
     * @return 文件完整路径
     */
    private String getFilePathByMd5(String fileMd5, String fileExt){
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }


    /**
     * 下载分块
     * @param fileMd5 文件md5
     * @param chunkTotal 文件分块总数
     * @return 分块文件数组
     */
    private File[] downloadChunk(String fileMd5, int chunkTotal){
        String chunkFolderPath = this.getChunkFileFolderPath(fileMd5);
        File[] files = new File[chunkTotal];
        for (int i = 0; i < chunkTotal; i++) {
            String chunkName = chunkFolderPath + i;
            // 1.创建临时文件
            File chunkTemp;
            try {
                chunkTemp = File.createTempFile(SysConstants.TEMP_CHUNK_PREDIX, null);
            } catch (IOException e) {
                log.error("创建分块临时文件出错: {}", e.getMessage());
                throw new XueChengPlusException("创建分块临时文件出错!");
            }
            // 2.下载分块文件
            this.downloadFileFromMinIo(chunkTemp, bucketVideoFiles, chunkName);
            // 分块文件加入数组
            files[i] = chunkTemp;
        }
        return files;
    }

    /**
     * 从minio下载文件到传入的文件对象
     *
     * @param file       需要保存到的文件
     * @param bucket     桶的名称
     * @param objectName 对象名称
     */
    private void downloadFileFromMinIo(File file, String bucket, String objectName) {
        GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build();
        try (InputStream inputStream = minioClient.getObject(getObjectArgs);
             FileOutputStream outputStream = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e){
            log.error("分块文件下载错误: {}", e.getMessage());
            throw new XueChengPlusException("分块文件下载错误!");
        }
    }

    /**
     * 得到分块文件的目录
     * @param fileMd5 文件md5
     * @return 分块文件的目录
     */
    private String getChunkFileFolderPath(String fileMd5) {
        return fileMd5.charAt(0) + SysConstants.FILE_SEPARATOR + fileMd5.charAt(1) +
                SysConstants.FILE_SEPARATOR + fileMd5 + SysConstants.FILE_SEPARATOR + "chunk"
                + SysConstants.FILE_SEPARATOR;
    }

    /**
     * 将文件上传到分布式文件系统
     * @param bytes 文件字节数组
     * @param bucket 桶名称
     * @param objectName 对象名称
     */
    private void addMediaFilesToMinIo(byte[] bytes, String bucket, String objectName) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            if (objectName.contains(SysConstants.DOT)){
                String extension = objectName.substring(objectName.lastIndexOf('.'));
                ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
                if (extensionMatch != null){
                    contentType = extensionMatch.getMimeType();
                }
            }
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .stream(byteArrayInputStream, byteArrayInputStream.available(), -1)
                    .contentType(contentType)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("上传文件到文件系统出错: {}", e.getMessage());
            throw new XueChengPlusException("上传文件出错!");
        }
    }

    /**
     * 将文件上传到分布式文件系统(minio的uploadObject上传)
     * @param filePath 文件路径
     * @param bucket 桶的名称
     * @param objectName 对象名称
     */
    private void addMediaFilesToMinIo(String filePath, String bucket, String objectName){
        String extension = null;
        if(objectName.contains(SysConstants.DOT)){
            extension = objectName.substring(objectName.lastIndexOf("."));
        }
        String contentType = this.getMimeTypeByExtension(extension);
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectName)
                    .filename(filePath)
                    .contentType(contentType)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("文件上传成功: {}", filePath);
        } catch (Exception e) {
            log.error("文件上传到文件系统失败: {}", filePath);
            throw new XueChengPlusException("文件上传到文件系统失败");
        }
    }

    /**
     * 根据扩展名获取MimeType
     * @param extension 文件扩展名
     * @return MimeType
     */
    private String getMimeTypeByExtension(String extension){
        String contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if(StringUtils.isNotEmpty(extension)){
            ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
            if(extensionMatch!=null){
                contentType = extensionMatch.getMimeType();
            }
        }
        return contentType;
    }



    /**
     * 根据日期拼接目录
     * @param date 日期
     * @param year 是否包含年
     * @param month 是否包含月
     * @param day 是否包含日
     * @return 拼接后目录
     */
    private String getFileFolder(Date date, boolean year, boolean month, boolean day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期字符串
        String dateString = sdf.format(date);
        //取出年、月、日
        String[] dateStringArray = dateString.split("-");
        StringBuilder folderString = new StringBuilder();
        if(year){
            folderString.append(dateStringArray[0]);
            folderString.append("/");
        }
        if(month){
            folderString.append(dateStringArray[1]);
            folderString.append("/");
        }
        if(day){
            folderString.append(dateStringArray[2]);
            folderString.append("/");
        }
        return folderString.toString();
    }

}
