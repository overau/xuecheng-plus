package com.xuecheng.media.service;

import com.xuecheng.media.model.po.MediaProcess;

import java.util.List;

/**
 * 媒资文件处理业务方法
 * @author HeJin
 * @version 1.0
 * @since 2023/02/26 19:34
 */
public interface MediaFileProcessService {

    /**
     * 根据分片参数获取待处理任务
     * @param shardTotal 总数
     * @param shardIndex 分片索引
     * @param count 任务数
     * @return 待处理任务集合
     */
    List<MediaProcess> selectListByShardIndex(int shardTotal, int shardIndex, int count);

    /**
     * 保存任务结果
     * @param taskId 任务id
     * @param status 任务状态
     * @param fileId 文件id
     * @param url 文件uel
     * @param errorMsg 错误信息
     */
    void saveProcessFinishStatus(Long taskId, String status, String fileId, String url, String errorMsg);
}