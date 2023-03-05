package com.xuecheng.content.service;

import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.BindTeachplanMediaDto;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.TeachplanMedia;

import java.util.List;

/**
 * 课程计划接口
 * @author HeJin
 * @version 1.0
 * @since 2023/03/04 12:51
 */
public interface TeachplanService {

    /**
     * 根据课程id查询课程计划
     * @param courseId 课程id
     * @return 课程计划信息
     */
    List<TeachplanDto> findTeachplanTree(Long courseId);

    /**
     * 保存课程计划: 包括新增和修改
     * @param teachplanDto 课程计划信息
     */
    void saveTeachplan(SaveTeachplanDto teachplanDto);

    /**
     * 教学计划绑定媒资
     * @param bindTeachplanMediaDto 教学计划-媒资绑定信息提交dto
     * @return 教学计划-媒资绑定信息
     */
    TeachplanMedia associationMedia(BindTeachplanMediaDto bindTeachplanMediaDto);

    /**
     * 课程计划绑定媒体
     * @param bindTeachplanMediaDto 教学计划-媒资绑定提交数据
     * @param courseId 课程id
     * @return TeachplanMedia
     */
    TeachplanMedia buildAssociationMedia(BindTeachplanMediaDto bindTeachplanMediaDto, Long courseId);

    /**
     * 移除课程计划和媒资信息绑定
     * @param teachPlanId 课程计划id
     * @param mediaId 媒体id
     * @return RestResponse
     */
    RestResponse<?> removeAssociationMedia(Long teachPlanId, String mediaId);
}