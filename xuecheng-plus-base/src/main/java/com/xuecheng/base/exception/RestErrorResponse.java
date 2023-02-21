package com.xuecheng.base.exception;

/**
 * 错误响应参数包装
 * @author HeJin
 * @version 1.0
 * @since 2023/02/21 10:49
 */
public class RestErrorResponse {

    private String errMessage;

    public RestErrorResponse(String errMessage){
        this.errMessage= errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

    public void setErrMessage(String errMessage) {
        this.errMessage = errMessage;
    }

}
