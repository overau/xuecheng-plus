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
    public static final CharSequence IMAGE_TYPE = "image";

    /**
     * mp4视频mimeType
     */
    public static final CharSequence MP4_TYPE = "mp4";

    /**
     * avi视频mimeType
     */
    public static final CharSequence AVI_TYPE = "video/x-msvideo";

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
}
