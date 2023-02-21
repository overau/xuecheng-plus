package com.xuecheng.base.exception;

/**
 * @author HeJin
 * @version 1.0
 * @since 2023/02/21 10:33
 */
public class XueChengPlusException extends RuntimeException{

    private String errMessage;

    public XueChengPlusException() {
    }

    public XueChengPlusException(String message) {
        this.errMessage = message;
    }

    public XueChengPlusException(CommonError commonError) {
        this.errMessage = commonError.getErrMessage();
    }

    public String getErrMessage() {
        return errMessage;
    }
}
