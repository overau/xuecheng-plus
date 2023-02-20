package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.enums.DictionaryType;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
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
        // 2.分页查询
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> items = courseBasePage.getRecords();
        // 3.封装返回数据
        PageResult<CourseBase> pageResult = new PageResult<>(items, page.getTotal(),
                pageParams.getPageNo(), pageParams.getPageSize());
        return pageResult;
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
        // 1.参数合法性校验
        if (DictionaryType.CHARGE.getCode().equals(addCourseDto.getCharge())){
            if (addCourseDto.getPrice() == null || addCourseDto.getPrice() <= 0){
                throw new RuntimeException("课程为收费但是价格为空!");
            }
        }
        // 2.对数据进行封装
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);
        courseBase.setCompanyId(companyId);
        courseBase.setCreateDate(LocalDateTime.now());
        courseBase.setAuditStatus(DictionaryType.AUDIT_NOT_COMMIT.getCode());
        courseBase.setStatus(DictionaryType.COURSE_NOT_PUBLISH.getCode());
        // 3.1保存课程基本信息
        int insert1 = courseBaseMapper.insert(courseBase);
        if (insert1 <= 0){
            throw new RuntimeException("添加课程失败!");
        }
        // 3.2 保存课程营销信息
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        Long courseBaseId = courseBase.getId();
        courseMarket.setId(courseBaseId);
        int insert2 = courseMarketMapper.insert(courseMarket);
        if (insert2 <= 0){
            throw new RuntimeException("添加课程失败!");
        }
        // 4.组装返回结果
        return getCourseBaseInfo(courseBaseId);
    }

    /**
     * 根据课程id查询课程基本信息和营销信息
     * @param courseId 课程id
     * @return 课程信息
     */
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        // 拷贝课程属性
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
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

}
