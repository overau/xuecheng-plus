package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 课程预览dto
 * @author HeJin
 * @version 1.0
 * @since 2023/03/05 10:05
 */
@ApiModel(value="CoursePreviewDto", description="课程预览dto")
@Data
public class CoursePreviewDto {

    /**
     * 课程基本信息,课程营销信息
     */
    CourseBaseInfoDto courseBase;

    /**
     * 课程计划信息
     */
    List<TeachplanDto> teachplans;

}
