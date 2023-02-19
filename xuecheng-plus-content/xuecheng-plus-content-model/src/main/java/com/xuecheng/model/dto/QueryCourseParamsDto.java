package com.xuecheng.model.dto;

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
    private String auditStatus;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 发布状态
     */
    private String publishStatus;

}
