package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.enums.DictionaryType;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程管理serviceImpl
 * @author HeJin
 * @version 1.0
 * @since 2023/02/19 17:31
 */
@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseMarketMapper courseMarketMapper;

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Resource
    private CourseMarketServiceImpl courseMarketService;

    /**
     * 课程查询
     *
     * @param pageParams           分页参数
     * @param queryCourseParamsDto 查询条件
     * @return PageResult<CourseBase>
     */
    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 1.封装课程查询条件
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 课程名称模糊查询
        queryWrapper.like(StringUtils.isNotBlank(queryCourseParamsDto.getCourseName()),
                CourseBase::getName, queryCourseParamsDto.getCourseName());
        // 审核状态
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getAuditStatus()),
                CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        // 课程发布状态
        queryWrapper.eq(StringUtils.isNotBlank(queryCourseParamsDto.getPublishStatus()),
                CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        // 根据课程创建时间排序
        queryWrapper.orderByDesc(CourseBase::getCreateDate);
        // 2.分页查询
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        if (courseBasePage == null){
            throw new XueChengPlusException("课程数据为空!");
        }
        List<CourseBase> items = courseBasePage.getRecords();
        // 3.封装返回数据
        return new PageResult<>(items, page.getTotal(),
                pageParams.getPageNo(), pageParams.getPageSize());
    }

    /**
     * 新增课程
     *
     * @param companyId    培训机构id
     * @param addCourseDto 新增课程信息
     * @return 课程信息: 基本信息、营销信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto addCourseDto) {
        // 1.对数据进行封装
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus(DictionaryType.AUDIT_NOT_COMMIT.getCode());
        courseBase.setStatus(DictionaryType.COURSE_NOT_PUBLISH.getCode());
        // 2.保存课程基本信息
        int insert1 = courseBaseMapper.insert(courseBase);
        if (insert1 <= 0){
            throw new XueChengPlusException("添加课程失败!");
        }
        // 3.保存课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        courseMarket.setId(courseBase.getId());
        boolean isSuccess = this.saveCourseMarket(courseMarket);
        if (!isSuccess){
            throw new XueChengPlusException("添加课程失败!");
        }
        // 4.组装返回结果
        return getCourseBaseInfo(courseBase.getId());
    }

    /**
     * 根据课程id查询课程基本信息和营销信息
     * @param courseId 课程id
     * @return 课程信息
     */
    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null){
            throw new XueChengPlusException("查询课程不存在!");
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        // 拷贝课程属性
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        // 拷贝营销信息
        if (courseMarket != null){
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        // 根据课程分类id查询课程分类名称
        CourseCategory mtCategory = courseCategoryMapper.selectById(courseBase.getMt());
        CourseCategory stCategory = courseCategoryMapper.selectById(courseBase.getSt());
        if (mtCategory != null){
            courseBaseInfoDto.setMtName(mtCategory.getName());
        }
        if (stCategory != null){
            courseBaseInfoDto.setStName(stCategory.getName());
        }

        return courseBaseInfoDto;
    }

    /**
     * 修改课程信息
     *
     * @param companyId     培训机构id: 校验本机构只能修改本机构的课程
     * @param editCourseDto 修改课程信息
     * @return 课程信息: 基本信息、营销信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        CourseBase courseBase = courseBaseMapper.selectById(editCourseDto.getId());
        if (null == courseBase){
            throw new XueChengPlusException("课程不存在!");
        }
        // 校验本机构只能修改本机构的课程
        if (!courseBase.getCompanyId().equals(companyId)){
            throw new XueChengPlusException("只能修改本机构的课程!");
        }
        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        int affectRows = courseBaseMapper.updateById(courseBase);
        if (affectRows <= 0){
            throw new XueChengPlusException("课程基本数据修改失败!");
        }

        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);
        boolean isSuccess = this.saveCourseMarket(courseMarket);
        if (!isSuccess){
            throw new XueChengPlusException("课程营销数据修改失败!");
        }
        return this.getCourseBaseInfo(editCourseDto.getId());
    }

    /**
     * 保存课程营销信息
     * @param courseMarket 课程营销信息
     * @return 保存是否成功
     */
    private boolean saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)){
            throw new XueChengPlusException("收费规则没有填写!");
        }
        if (DictionaryType.CHARGE.getCode().equals(charge)){
            if (courseMarket.getPrice() == null || courseMarket.getPrice() <= 0){
                throw new XueChengPlusException("课程为收费价格不能为空且大于0!");
            }
        }
        return courseMarketService.saveOrUpdate(courseMarket);
    }

}
