package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

/**
 * 课程分类service
 * @author HeJin
 * @version 1.0
 * @since 2023/02/20 16:37
 */
public interface CourseCategoryService {

    /**
     * 课程分类查询
     * @param id 根结点id
     * @return 根结点下面的所有子结点
     */
    List<CourseCategoryTreeDto> queryTreeNodes(String id);

}