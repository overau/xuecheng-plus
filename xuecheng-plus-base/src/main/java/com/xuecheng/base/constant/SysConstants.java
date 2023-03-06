package com.xuecheng.base.constant;

/**
 * 系统常量
 * @author HeJin
 * @version 1.0
 * @since 2023/02/24 21:25
 */
public class SysConstants {

    /**
     * 文件分隔符
     */
    public static final String FILE_SEPARATOR = "/";

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 分块临时文件前缀
     */
    public static final String TEMP_CHUNK_PREDIX = "chunk";

    /**
     * 合并分块临时文件前缀
     */
    public static final String TEMP_MERGE_PREDIX = "merge";

    /**
     * 图片mimeType
     */
    public static final String IMAGE_TYPE = "image";

    /**
     * mp4视频mimeType
     */
    public static final String MP4_TYPE = "mp4";

    /**
     * avi视频mimeType
     */
    public static final String AVI_TYPE = "video/x-msvideo";

    /**
     * 视频任务未处理: 1
     */
    public static final String VIDEO_UN_FINISH = "1";

    /**
     * 视频任务处理完成: 2
     */
    public static final String VIDEO_FINISH = "2";

    /**
     * 视频任务处理失败
     */
    public static final String VIDEO_FAILURE = "3";

    /**
     * 根计划id
     */
    public static final Long ROOT_PLAN_ID = 0L;

    /**
     * 教学计划等级：2
     */
    public static final int TEACHPLAN_GRADE_TWO = 2;

    /**
     * 审核已提交
     */
    public static final String AUDIT_COMMIT = "202003";

    /**
     * 审核通过
     */
    public static final String AUDIT_FINISH = "202004";

    /**
     * 已发布
     */
    public static final String PUBLISH_FINISH = "203002";
}
