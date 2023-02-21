package com.xuecheng.base.exception;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/02/21 10:36
 */
public enum CommonError {

    /**
     * 通用异常
     */
    UNKNOWN_ERROR("执行过程异常，请重试。"),
    PARAMS_ERROR("非法参数"),
    OBJECT_NULL("对象为空"),
    QUERY_NULL("查询结果为空"),
    REQUEST_NULL("请求参数为空");

    /**
     * 异常信息
     */
    private final String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    CommonError( String errMessage) {
        this.errMessage = errMessage;
    }

}
