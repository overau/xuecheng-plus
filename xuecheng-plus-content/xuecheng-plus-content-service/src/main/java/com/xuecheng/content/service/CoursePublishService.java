package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

/**
 * 课程预览、发布service
 * @author HeJin
 * @version 1.0
 * @since 2023/03/05 10:06
 */
public interface CoursePublishService {

    /**
     * 根据课程id获取预览数据
     * @param courseId 课程id
     * @return 课程预览数据
     */
    CoursePreviewDto getCoursePreviewInfo(Long courseId);

    /**
     * 提交课程审核
     * @param companyId 机构id
     * @param courseId 课程id
     */
    void commitAudit(Long companyId,Long courseId);

    /**
     * 课程发布
     * @param companyId 机构id
     * @param courseId 课程id
     */
    void publish(Long companyId,Long courseId);

}