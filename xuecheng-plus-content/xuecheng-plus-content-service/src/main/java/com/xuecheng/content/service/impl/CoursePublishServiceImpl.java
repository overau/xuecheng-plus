package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程预览、发布service实现
 * @author HeJin
 * @version 1.0
 * @since 2023/03/05 10:08
 */
@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Resource
    private CourseBaseInfoService courseBaseInfoService;

    @Resource
    private TeachplanService teachplanService;

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseMarketMapper courseMarketMapper;

    @Resource
    private CoursePublishPreMapper coursePublishPreMapper;

    /**
     * 根据课程id获取预览数据
     *
     * @param courseId 课程id
     * @return 课程预览数据
     */
    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        if (courseId == null || courseId <= 0){
            throw new XueChengPlusException("课程id不合法!");
        }
        // 查询课程基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 查询课程计划
        List<TeachplanDto> teachplanDtoList = teachplanService.findTeachplanTree(courseId);
        // 封装预览数据
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanDtoList);
        return coursePreviewDto;
    }

    /**
     * 提交课程审核
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    @Override
    public void commitAudit(Long companyId, Long courseId) {
        // 约束校验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        String auditStatus = courseBase.getAuditStatus();
        if (SysConstants.AUDIT_COMMIT.equals(auditStatus)) {
            throw new XueChengPlusException("当前为等待审核状态，审核完成可以再次提交。");
        }
        if (!courseBase.getCompanyId().equals(companyId)) {
            throw new XueChengPlusException("不允许提交其它机构的课程。");
        }
        if (StringUtils.isEmpty(courseBase.getPic())) {
            throw new XueChengPlusException("提交失败，请上传课程图片");
        }
        // 查询课程计划信息
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (CollectionUtils.isEmpty(teachplanTree)) {
            throw new XueChengPlusException("提交失败，还没有添加课程计划");
        }
        //添加课程预发布记录
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo,coursePublishPre);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        String courseMarketJson = JSON.toJSONString(courseMarket);
        coursePublishPre.setMarket(courseMarketJson);
        //设置预发布记录状态,已提交
        coursePublishPre.setStatus(SysConstants.AUDIT_COMMIT);
        coursePublishPre.setCompanyId(companyId);
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPreUpdate == null){
            //添加课程预发布记录
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            // 更新课程预发布记录
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本表的审核状态
        courseBase.setAuditStatus(SysConstants.AUDIT_COMMIT);
        courseBaseMapper.updateById(courseBase);
    }

}
