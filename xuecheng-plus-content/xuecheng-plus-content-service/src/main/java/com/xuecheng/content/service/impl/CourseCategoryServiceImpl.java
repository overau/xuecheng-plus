package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/02/20 16:40
 */
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    /**
     * 课程分类查询
     *
     * @param id 根结点id
     * @return 根结点下面的所有子结点
     */
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        // 1.根结点下面的所有子结点
        List<CourseCategoryTreeDto> categoryTreeDtoList = courseCategoryMapper.selectTreeNodes(id);
        // 2.封装返回数据
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = categoryTreeDtoList.stream()
                .filter(item -> item.getParentid().equals(id))
                .map(item -> item.setChildrenTreeNodes(getChildren(categoryTreeDtoList, item)))
                .collect(Collectors.toList());
        // 3.返回的list只包括了根节点的直接下属结点
        return courseCategoryTreeDtoList;
    }

    /**
     * 查询指定分类结点下的所有子分类树
     * @param categoryTreeDtos 分类数据集合
     * @param node 指定分类结点
     * @return 子分类树
     */
    private List<CourseCategory> getChildren(List<CourseCategoryTreeDto> categoryTreeDtos, CourseCategoryTreeDto node) {
        List<CourseCategory> children = categoryTreeDtos.stream()
                .filter(c -> c.getParentid().equals(node.getId()))
                .map(c -> c.setChildrenTreeNodes(getChildren(categoryTreeDtos, c)))
                .collect(Collectors.toList());
        return children;
    }

}
