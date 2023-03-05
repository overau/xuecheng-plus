package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import com.xuecheng.content.service.TeachplanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程计划接口实现
 * @author HeJin
 * @version 1.0
 * @since 2023/03/04 12:53
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Resource
    private TeachplanMapper teachplanMapper;

    @Resource
    private TeachplanMediaMapper teachplanMediaMapper;

    @Resource
    private TeachplanService currentProxy;

    /**
     * 根据课程id查询课程计划
     *
     * @param courseId 课程id
     * @return 课程计划信息
     */
    @Override
    public List<TeachplanDto> findTeachplanTree(Long courseId) {
        // 1.查询课程计划
        if (courseId == null || courseId <= 0){
            throw new XueChengPlusException("课程id非法!");
        }
        List<TeachplanDto> teachplanDtoList = teachplanMapper.selectTreeNodes(courseId);
        // 2.封装树形结构数据
        return this.buildTeachplanTree(teachplanDtoList, SysConstants.ROOT_PLAN_ID);
    }

    /**
     * 保存课程计划: 包括新增和修改
     *
     * @param teachplanDto 课程计划信息
     */
    @Override
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        //课程计划id
        Long id = teachplanDto.getId();
        if(id != null){
            // 修改课程计划
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            //取出同父同级别的课程计划数量
            int count = this.getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count+1);
            BeanUtils.copyProperties(teachplanDto,teachplanNew);
            teachplanMapper.insert(teachplanNew);
        }
    }

    /**
     * 教学计划绑定媒资
     *
     * @param bindTeachplanMediaDto 教学计划-媒资绑定信息提交dto
     * @return 教学计划-媒资绑定信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto) {
        // 1.判断教学计划是否存在
        Teachplan teachplan = teachplanMapper.selectById(bindTeachplanMediaDto.getTeachplanId());
        if (null == teachplan){
            throw new XueChengPlusException("教学计划不存在!");
        }
        Integer grade = teachplan.getGrade();
        if(grade != SysConstants.TEACHPLAN_GRADE_TWO){
            throw new XueChengPlusException("只允许第二级教学计划绑定媒资文件");
        }
        // 2.存在，获取课程id
        Long courseId = teachplan.getCourseId();
        // 3.建立绑定关系：先删除旧的，在增加新的关系
        return currentProxy.buildAssociationMedia(bindTeachplanMediaDto, courseId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TeachplanMedia buildAssociationMedia(BindTeachplanMediaDto bindTeachplanMediaDto, Long courseId) {
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<TeachplanMedia>().eq(
                TeachplanMedia::getTeachplanId, bindTeachplanMediaDto.getTeachplanId());
        teachplanMediaMapper.delete(queryWrapper);
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachplanMedia.setTeachplanId(bindTeachplanMediaDto.getTeachplanId());
        teachplanMedia.setCourseId(courseId);
        teachplanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachplanMedia.setCreateDate(LocalDateTime.now());
        teachplanMediaMapper.insert(teachplanMedia);
        return teachplanMedia;
    }

    /**
     * 移除课程计划和媒资信息绑定
     *
     * @param teachPlanId 课程计划id
     * @param mediaId     媒体id
     * @return RestResponse
     */
    @Override
    public RestResponse<?> removeAssociationMedia(Long teachPlanId, String mediaId) {
        if (teachPlanId == null || teachPlanId <= 0){
            throw new XueChengPlusException("课程计划id不合法!");
        }
        if(StringUtils.isBlank(mediaId)){
            throw new XueChengPlusException("媒体id不合法!");
        }
        LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachplanMedia::getTeachplanId, teachPlanId);
        queryWrapper.eq(TeachplanMedia::getMediaId, mediaId);
        int isDelete = teachplanMediaMapper.delete(queryWrapper);
        if (isDelete <= 0){
            throw new XueChengPlusException("移除课程计划和媒资信息绑定失败!");
        }
        return RestResponse.success();
    }

    /**
     * 获取课程计划最新的排序号
     * @param courseId 课程id
     * @param parentId 课程父id
     * @return 最新的排序号
     */
    private int getTeachplanCount(long courseId,long parentId){
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId,courseId);
        queryWrapper.eq(Teachplan::getParentid,parentId);
        return teachplanMapper.selectCount(queryWrapper);
    }

    /**
     * 根据传入的根课程id构建课程计划树形结构
     * @param teachplanDtoList 课程计划列表
     * @param courseId 课程id
     * @return 课程计划树形结构
     */
    private List<TeachplanDto> buildTeachplanTree(List<TeachplanDto> teachplanDtoList, Long courseId) {
        return teachplanDtoList.stream()
                .filter(teachplanDto -> teachplanDto.getParentid().equals(courseId))
                .map(teachplanDto -> teachplanDto.setTeachPlanTreeNodes(
                        this.getChildren(teachplanDto, teachplanDtoList)))
                .collect(Collectors.toList());
    }

    /**
     * 获取传入课程计划在集合中的孩子: 递归
     * @param teachplan 传入课程计划
     * @param teachplanDtos 课程计划集合
     * @return 传入课程计划的孩子
     */
    private List<TeachplanDto> getChildren(TeachplanDto teachplan, List<TeachplanDto> teachplanDtos) {
        return teachplanDtos.stream()
                .filter(teachplanDto -> teachplanDto.getParentid().equals(teachplan.getId()))
                .map(teachplanDto -> teachplanDto.setTeachPlanTreeNodes(getChildren(teachplanDto, teachplanDtos)))
                .collect(Collectors.toList());
    }

}
