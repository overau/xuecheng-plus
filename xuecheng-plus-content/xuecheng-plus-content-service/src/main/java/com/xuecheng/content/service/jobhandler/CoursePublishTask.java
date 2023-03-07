package com.xuecheng.content.service.jobhandler;

import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 课程发布任务
 *
 * @author HeJin
 * @version 1.0
 * @since 2023/03/07 9:46
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    /**
     * 课程发布消息类型
     */
    public static final String MESSAGE_TYPE = "course_publish";

    /**
     * 课程发布任务任务调度入口
     */
    @XxlJob("coursePublishJobHandler")
    public void coursePublishJobHandler() {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex=" + shardIndex + ",shardTotal=" + shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex, shardTotal, MESSAGE_TYPE, 5, 60);
    }

    /**
     * 执行任务
     *
     * @param mqMessage 执行任务内容
     * @return boolean true:处理成功，false处理失败
     * @description 任务处理
     * @author Mr.M
     * @date 2022/9/21 19:47
     */
    @Override
    public boolean execute(MqMessage mqMessage) {
        log.debug("开始执行课程发布任务, 课程id: {}", mqMessage.getStageState1());

        // 课程信息静态化

        // 课程信息保存到Redis

        // 课程信息保存到ES

        // 静态页面上传到minio
        return true;
    }
}
