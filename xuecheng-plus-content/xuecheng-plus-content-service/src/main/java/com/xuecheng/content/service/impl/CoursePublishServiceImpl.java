package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Resource
    private CoursePublishMapper coursePublishMapper;

    @Resource
    private MqMessageService mqMessageService;

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

    /**
     * 课程发布
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(Long companyId, Long courseId) {
        //约束校验
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            throw new XueChengPlusException("请先提交课程审核，审核通过才可以发布");
        }
        if(!coursePublishPre.getCompanyId().equals(companyId)){
            throw new XueChengPlusException("不允许提交其它机构的课程。");
        }
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if(!SysConstants.AUDIT_FINISH.equals(auditStatus)){
            throw new XueChengPlusException("操作失败，课程审核通过方可发布。");
        }

        //保存课程发布信息
        this.saveCoursePublish(courseId);
        //保存消息表
        this.saveCoursePublishMessage(courseId);
        //删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    /**
     * 保存课程发布信息
     * @param courseId 课程id
     */
    private void saveCoursePublish(Long courseId) {
        //整合课程发布信息
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            throw new XueChengPlusException("课程预发布数据为空");
        }
        // 修改课程发布表
        CoursePublish coursePublish = new CoursePublish();
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        coursePublish.setStatus(SysConstants.PUBLISH_FINISH);
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if(coursePublishUpdate == null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus(SysConstants.PUBLISH_FINISH);
        courseBaseMapper.updateById(courseBase);
    }

    /**
     * 保存消息表
     * @param courseId 课程id
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish",
                String.valueOf(courseId), null, null);
        if (null == mqMessage){
            throw new XueChengPlusException("添加消息记录失败!");
        }
    }

}
