package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

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

    @Resource
    private CoursePublishService coursePublishService;

    @Resource
    private MqMessageService mqMessageService;

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
        // 课程信息静态化, 并将静态页面上传到minio
        this.generateCourseHtml(mqMessage, mqMessage.getBusinessKey1());
        // 课程信息保存到Redis

        // 课程信息保存到ES

        return true;
    }

    private void generateCourseHtml(MqMessage mqMessage, String courseId) {
        // 判断任务是否完成
        int stageOne = this.getMqMessageService().getStageOne(mqMessage.getId());
        if (stageOne > 0){
            log.debug("当前阶段是静态化课程信息任务已经完成不再处理,任务信息:{}",mqMessage);
            return ;
        }
        // 生成静态文件
        File file = coursePublishService.generateCourseHtml(Long.valueOf(courseId));
        if (null == file){
            throw new XueChengPlusException("课程静态化异常!");
        }
        // 上传静态文件
        coursePublishService.uploadCourseHtml(Long.valueOf(courseId), file);
        // 完成阶段1任务
        mqMessageService.completedStageOne(mqMessage.getId());
    }
}
