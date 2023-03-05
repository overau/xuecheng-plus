package com.xuecheng.content.service.impl;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 课程预览、发布service实现
 * @author HeJin
 * @version 1.0
 * @since 2023/03/05 10:08
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Resource
    private CourseBaseInfoService courseBaseInfoService;

    @Resource
    private TeachplanService teachplanService;

    /**
     * 根据课程id获取预览数据
     *
     * @param courseId 课程id
     * @return 课程预览数据
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        if (courseId == null || courseId <= 0){
            throw new XueChengPlusException("课程id不合法!");
        }
        // 查询课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 查询课程计划
        List<TeachplanDto> teachplanDtoList = teachplanService.findTeachplayTree(courseId);
        // 封装预览数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanDtoList);
        return coursePreviewDto;
    }

}
