package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 课程分类属性结点dto
 * @author HeJin
 * @version 1.0
 * @since 2023/02/20 14:56
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {

    List<CourseCategory> childrenTreeNodes;

}
