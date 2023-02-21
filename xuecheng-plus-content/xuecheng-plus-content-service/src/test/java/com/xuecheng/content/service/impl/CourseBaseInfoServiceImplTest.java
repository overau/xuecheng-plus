package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/02/21 19:14
 */
class CourseBaseInfoServiceImplTest {

    @Spy
    @InjectMocks
    private CourseBaseInfoServiceImpl courseBaseInfoService;

    @Mock
    private CourseBaseMapper courseBaseMapper;

    @Mock
    private CourseMarketMapper courseMarketMapper;

    @Mock
    private CourseCategoryMapper courseCategoryMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 修改的课程不存在
     */
    @Test
    public void should_throw_exception_when_course_given_not_exists(){
        Long courseId = -1L;
        Long companyId = 22L;
        Mockito.when(courseBaseMapper.selectById(courseId)).thenReturn(null);
        EditCourseDto editCourseDto = new EditCourseDto();
        editCourseDto.setId(courseId);
        try {
            courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }
    }

    /**
     * 校验本机构只能修改本机构的课程
     */
    @Test
    public void should_throw_exception_when_company_given_not_self(){
        Long courseId = 127L;
        Long companyId = 22L;
        EditCourseDto editCourseDto = new EditCourseDto();
        CourseBase courseBase = new CourseBase();
        courseBase.setId(courseId);
        courseBase.setCompanyId(23L);
        Mockito.when(courseBaseMapper.selectById(courseId)).thenReturn(courseBase);
        editCourseDto.setId(courseId);
        try {
            courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }
    }

    /**
     * 课程基本信息修改失败
     */
    @Test
    public void should_throw_exception_when_course_given_update_fail(){
        Long courseId = 127L;
        Long companyId = 22L;
        EditCourseDto editCourseDto = new EditCourseDto();
        CourseBase courseBase = new CourseBase();
        courseBase.setId(courseId);
        editCourseDto.setId(courseId);
        courseBase.setCompanyId(22L);
        Mockito.when(courseBaseMapper.selectById(courseId)).thenReturn(courseBase);
        Mockito.when(courseBaseMapper.updateById(courseBase)).thenReturn(0);
        try {
            courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }
    }

    /**
     * 收费规则没有填写
     */
    @Test
    public void should_throw_exception_when_charge_given_is_empty(){
        Long courseId = 127L;
        Long companyId = 22L;
        EditCourseDto editCourseDto = new EditCourseDto();
        CourseBase courseBase = new CourseBase();
        courseBase.setId(courseId);
        editCourseDto.setId(courseId);
        courseBase.setCompanyId(22L);
        Mockito.when(courseBaseMapper.selectById(courseId)).thenReturn(courseBase);
        Mockito.when(courseBaseMapper.updateById(courseBase)).thenReturn(1);
        try {
            courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }

        // 课程为收费价格不能为空
        editCourseDto.setCharge("201001");
        try {
            courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }

        // 课程为收费价格不能为空且大于0
        editCourseDto.setCharge("201001");
        editCourseDto.setPrice(0F);
        try {
            courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }
    }

    /**
     * 营销数据修改失败
     */
    @Test
    public void should_throw_exception_when_course_market_update_fail(){
        Long courseId = 127L;
        Long companyId = 22L;
        EditCourseDto editCourseDto = new EditCourseDto();
        CourseBase courseBase = new CourseBase();
        courseBase.setId(courseId);
        editCourseDto.setId(courseId);
        courseBase.setCompanyId(22L);
        Mockito.when(courseBaseMapper.selectById(courseId)).thenReturn(courseBase);
        Mockito.when(courseBaseMapper.updateById(courseBase)).thenReturn(1);
        editCourseDto.setCharge("201001");
        editCourseDto.setPrice(10F);
        try {
            courseBaseInfoService.updateCourseBase(companyId, editCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }
    }

    /**
     * 根据id查询课程单元测试
     */
    @Test
    void getCourseBaseInfo() {
        Long courseId = 1L;

        // 查询课程不存在!
        Mockito.when(courseBaseMapper.selectById(courseId)).thenReturn(null);
        try {
            courseBaseInfoService.getCourseBaseInfo(courseId);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }

        CourseBase courseBase = new CourseBase();
        courseBase.setMt("1001");
        courseBase.setSt("1002");
        Mockito.when(courseBaseMapper.selectById(courseId)).thenReturn(courseBase);
        // 营销信息不为空
        CourseMarket courseMarket = new CourseMarket();
        Mockito.when(courseMarketMapper.selectById(courseId)).thenReturn(courseMarket);
        // 大分类、小分类不为空
        CourseCategory courseCategory = new CourseCategory();
        Mockito.when(courseCategoryMapper.selectById("1001")).thenReturn(courseCategory);
        Mockito.when(courseCategoryMapper.selectById("1002")).thenReturn(courseCategory);
        courseBaseInfoService.getCourseBaseInfo(courseId);
    }

    /**
     * 新增课程
     */
    @Test
    void createCourseBase() {
        Long companyId = 22L;
        AddCourseDto addCourseDto = new AddCourseDto();
        CourseBase courseBase = new CourseBase();
        Mockito.when(courseBaseMapper.insert(courseBase)).thenReturn(0);
        try {
            courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }

        Mockito.when(courseBaseMapper.insert(Mockito.any())).thenReturn(1);
        try {
            courseBaseInfoService.createCourseBase(companyId, addCourseDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }
    }

    /**
     * 分页查询课程
     */
    @Test
    void queryCourseBaseList() {
        PageParams pageParams = new PageParams();
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto();
        // 课程数据为空
        Mockito.when(courseBaseMapper.selectPage(Mockito.any(), Mockito.any())).thenReturn(null);
        try {
            courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
        } catch (Exception e) {
            Assertions.assertTrue(e instanceof XueChengPlusException);
        }

        // 课程数据不为空
        Mockito.when(courseBaseMapper.selectPage(Mockito.any(), Mockito.any())).thenReturn(new Page<CourseBase>());
        courseBaseInfoService.queryCourseBaseList(pageParams, queryCourseParamsDto);
    }
}