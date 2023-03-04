package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.model.po.TeachplanMedia;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 课程计划树型结构dto
 * @author HeJin
 * @version 1.0
 * @since 2023/03/04 11:08
 */
@ApiModel(value="TeachPlanDto", description="课程计划dto")
@Data
@Accessors(chain = true)
public class TeachplanDto extends Teachplan {

    /**
     * 课程计划关联的媒资信息
     */
    @ApiModelProperty(value = "课程计划关联的媒资信息", required = true)
    TeachplanMedia teachplanMedia;

    /**
     * 子结点
     */
    @ApiModelProperty(value = "课程计划子节点", required = true)
    List<TeachplanDto> teachPlanTreeNodes;

}
