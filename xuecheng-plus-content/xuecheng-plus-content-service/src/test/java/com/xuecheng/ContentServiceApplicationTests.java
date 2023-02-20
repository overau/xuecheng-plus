package com.xuecheng;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 测试mybatis-plus
 * @author HeJin
 * @version 1.0
 * @since 2023/02/19 16:30
 */
@SpringBootTest
@Slf4j
public class ContentServiceApplicationTests {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseBaseInfoService courseBaseInfoService;

    @Resource
    private CourseCategoryService courseCategoryService;

    @Test
    public void testCourseBaseMapper(){
        CourseBase courseBase = courseBaseMapper.selectById(22);
        Assertions.assertNotNull(courseBase);
    }

    @Test
    public void testCourseBaseInfoService(){
        PageParams pageParams = new PageParams();
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        PageResult<CourseBase> courseBaseList = courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        log.debug("courseBaseList: {}", courseBaseList);
    }

    @Test
    public void testCourseCategoryService(){
        List<CourseCategoryTreeDto> courseCategoryTreeDtoList = courseCategoryService.queryTreeNodes("1");
        log.debug("courseCategoryTreeDtoList: {}", courseCategoryTreeDtoList);
    }

}
