package com.xuecheng.media.service.impl;

import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.media.mapper.MediaFilesMapper;
import com.xuecheng.media.mapper.MediaProcessHistoryMapper;
import com.xuecheng.media.mapper.MediaProcessMapper;
import com.xuecheng.media.model.po.MediaFiles;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.model.po.MediaProcessHistory;
import com.xuecheng.media.service.MediaFileProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/02/26 19:35
 */
@Service
@Slf4j
public class MediaFileProcessServiceImpl implements MediaFileProcessService {

    @Resource
    private MediaFilesMapper mediaFilesMapper;

    @Resource
    private MediaProcessMapper mediaProcessMapper;

    @Resource
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    /**
     * 根据分片参数获取待处理任务
     *
     * @param shardTotal 总数
     * @param shardIndex 分片索引
     * @param count      任务数
     * @return 待处理任务集合
     */
    @Override
    public List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count) {
        return mediaFilesMapper.selectListByShardIndex(shardTotal, shardIndex, count);
    }

    /**
     * 保存任务结果
     *
     * @param taskId   任务id
     * @param status   任务状态
     * @param fileId   文件id
     * @param url      文件uel
     * @param errorMsg 错误信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg) {
        // 1.查出任务，如果不存在则直接返回
        MediaProcess mediaProcess = mediaProcessMapper.selectById(taskId);
        if (null == mediaProcess){
            log.error("任务不存在: {}", taskId);
            throw new XueChengPlusException("任务不存在!");
        }
        // 2.处理失败，更新任务处理结果
        if (SysConstants.VIDEO_FAILURE.equals(status)){
            mediaProcess.setStatus(SysConstants.VIDEO_FAILURE);
            mediaProcess.setErrormsg(errorMsg);
            mediaProcessMapper.updateById(mediaProcess);
            throw new XueChengPlusException("任务处理失败!");
        }
        // 3.处理成功，更新url和状态
        // 3.1更新媒资文件表，添加url
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileId);
        if (mediaFiles != null){
            mediaFiles.setUrl(url);
            mediaFilesMapper.updateById(mediaFiles);
        }
        // 3.2更新媒资任务表，防止任务重复执行
        mediaProcess.setUrl(url);
        mediaProcess.setStatus(SysConstants.VIDEO_FINISH);
        mediaProcess.setFinishDate(LocalDateTime.now());
        mediaProcessMapper.updateById(mediaProcess);
        // 4.添加到历史记录
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistoryMapper.insert(mediaProcessHistory);
        // 5.删除媒资任务表中已完成的任务
        mediaProcessMapper.deleteById(mediaProcess.getId());
    }

}
