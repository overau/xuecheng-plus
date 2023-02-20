package com.xuecheng.content.api;

import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 课程分类controller
 * @author HeJin
 * @version 1.0
 * @since 2023/02/20 14:48
 */
@Api(value = "课程分类接口", tags = "课程分类接口")
@RestController
public class CourseCategoryController {

    @ApiOperation("课程分类查询接口")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryTreeDto> queryTreeNodes(){
        return null;
    }

}
