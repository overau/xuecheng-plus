package com.xuecheng.base.enums;

/**
 * 数据字典枚举
 * @author HeJin
 * @version 1.0
 * @since 2023/02/20 22:07
 */
public enum DictionaryType {

    /**
     * 课程收费
     */
    CHARGE("201001", "收费"),

    /**
     * 审核状态
     */
    AUDIT_NOT_COMMIT("202002", "未提交"),

    /**
     * 课程发布状态
     */
    COURSE_NOT_PUBLISH("203001", "未发布");

    /**
     * 编码
     */
    private final String code;

    /**
     * 描述
     */
    private final String desc;


    DictionaryType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}
