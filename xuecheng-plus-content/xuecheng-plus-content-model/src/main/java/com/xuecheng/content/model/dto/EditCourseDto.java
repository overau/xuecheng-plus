package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 添加课程dto
 * @author HeJin
 * @version 1.0
 * @since 2023/02/20 20:23
 */
@Data
@ApiModel(value="EditCourseDto", description="修改课程基本信息")
public class EditCourseDto extends AddCourseDto{

    /**
     * 课程id
     */
    private Long id;

}
