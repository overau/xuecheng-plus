package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;

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
    List<TeachplanDto> findTeachplayTree(Long courseId);

}