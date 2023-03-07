package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CoursePreviewDto;

import java.io.File;

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

    /**
     * 课程静态化
     * @param courseId 课程id
     * @return 静态化文件
     */
    File generateCourseHtml(Long courseId);

    /**
     * 上传课程静态化页面
     * @param courseId 课程id
     * @param file 静态化页面
     */
    void uploadCourseHtml(Long courseId,File file);

}