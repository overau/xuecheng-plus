package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.po.CourseCategory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程分类 Mapper 接口
 * </p>
 *
 * @author HeJin
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategory> {

    /**
     * 课程分类查询
     * @param id 根结点id
     * @return 根结点下面的所有子结点
     */
    List<CourseCategoryTreeDto> selectTreeNodes(@Param("id") String id);

}
