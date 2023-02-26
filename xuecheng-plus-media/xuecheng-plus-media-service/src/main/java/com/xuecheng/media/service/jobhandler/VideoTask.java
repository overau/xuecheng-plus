package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 视频转码任务处理器
 * @author HeJin
 * @version 1.0
 * @since 2023/02/26 20:18
 */
@Component
@Slf4j
public class VideoTask {

    @Resource
    private MediaFileProcessService mediaFileProcessService;

    @Resource
    private MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    String ffmpegPath;

    /**
     * 视频分片广播任务
     * @throws Exception e
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        // 1.分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        List<MediaProcess> mediaProcessList;
        int size;
        try {
            // 2.取出2条记录，一次处理视频数量不要超过cpu核心数
            mediaProcessList = mediaFileProcessService.selectListByShardIndex(
                    shardTotal, shardIndex, 2);
            if (CollectionUtils.isEmpty(mediaProcessList)){
                throw new XueChengPlusException("待处理任务为空!");
            }
            size = mediaProcessList.size();
            log.debug("取出待处理视频任务: {}条", size);
        } catch (Exception e) {
            log.error("获取待处理任务出错: {}", e.getMessage());
            throw new XueChengPlusException("获取待处理任务出错!");
        }
        // 3.启动线程池
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(size, size, 1L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1), new BasicThreadFactory.Builder().build(),
                new ThreadPoolExecutor.AbortPolicy());
        CountDownLatch countDownLatch = new CountDownLatch(size);
        mediaProcessList.forEach(mediaProcess -> poolExecutor.execute(() -> {
            String bucket = mediaProcess.getBucket();
            String filePath = mediaProcess.getFilePath();
            String fileId = mediaProcess.getFileId();
            String filename = mediaProcess.getFilename();
            //将要处理的文件下载到服务器上
            File originalFile = null;
            //处理结束的视频文件
            File mp4File = null;
            try {
                originalFile = File.createTempFile("original", null);
                mp4File = File.createTempFile("mp4", ".mp4");
            } catch (IOException e) {
                log.error("处理视频前创建临时文件失败");
            }
            // 下载文件
            mediaFileService.downloadFileFromMinIo(originalFile, bucket, filePath);
            // 视频处理
            Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, originalFile.getAbsolutePath(), mp4File.getName(), mp4File.getAbsolutePath());
            String result = videoUtil.generateMp4();
            if(!"success".equals(result)){
                log.error("处理视频失败,视频地址:{},错误信息:{}",bucket+filePath,result);
            }
            // 将mp4上传至minio
            String objectName = this.getFilePath(fileId, ".mp4");
            mediaFileService.addMediaFilesToMinIo(mp4File.getAbsolutePath(), bucket, objectName);
            // 保存任务状态
            String url = "/" + bucket + "/" + objectName;
            mediaFileProcessService.saveProcessFinishStatus(mediaProcess.getId(),
                    SysConstants.VIDEO_FINISH, fileId, url, null);
            countDownLatch.countDown();
        }));

        // 等待多线程视频转码任务运行完成(阻塞): 最多等待30分钟
        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    /**
     * 根据文件md5和扩展名获取文件路径
     * @param fileMd5 文件md5
     * @param fileExt 文件后缀
     * @return 文件路径
     */
    private String getFilePath(String fileMd5,String fileExt){
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" +fileMd5 +fileExt;
    }


}
