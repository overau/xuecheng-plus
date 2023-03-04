package com.xuecheng.content.service.impl;

import com.xuecheng.base.constant.SysConstants;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    /**
     * 根据课程id查询课程计划
     *
     * @param courseId 课程id
     * @return 课程计划信息
     */
    @Override
    public List<TeachplanDto> findTeachplayTree(Long courseId) {
        // 1.查询课程计划
        if (courseId == null || courseId <= 0){
            throw new XueChengPlusException("课程id非法!");
        }
        List<TeachplanDto> teachplanDtoList = teachplanMapper.selectTreeNodes(courseId);
        // 2.封装树形结构数据
        return this.buildTeachplanTree(teachplanDtoList, SysConstants.ROOT_PLAN_ID);
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
