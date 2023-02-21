package com.xuecheng.content.service;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;

/**
 * 课程管理service
 * @author HeJin
 * @version 1.0
 * @since 2023/02/19 17:25
 */
public interface CourseBaseInfoService {

    /**
     * 课程查询
     * @param pageParams 分页参数
     * @param queryCourseParamsDto 查询条件
     * @return PageResult<CourseBase>
     */
    PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto);

    /**
     * 新增课程
     * @param companyId 培训机构id
     * @param addCourseDto 新增课程信息
     * @return 课程信息: 基本信息、营销信息
     */
    CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto);

    /**
     * 根据课程id查询课程基本信息和营销信息
     * @param courseId 课程id
     * @return 课程信息
     */
    CourseBaseInfoDto getCourseBaseInfo(Long courseId);

}