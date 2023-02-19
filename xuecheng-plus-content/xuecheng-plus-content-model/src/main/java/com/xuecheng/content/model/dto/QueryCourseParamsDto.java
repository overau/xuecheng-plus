package com.xuecheng.content.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author HeJin
 * @date 2023/02/19 13:55
 */
@Data
public class QueryCourseParamsDto {

    /**
     * 审核状态
     */
    @ApiModelProperty("审核状态")
    private String auditStatus;

    /**
     * 课程名称
     */
    @ApiModelProperty("状态名称")
    private String courseName;

    /**
     * 发布状态
     */
    @ApiModelProperty("发布状态")
    private String publishStatus;

}
