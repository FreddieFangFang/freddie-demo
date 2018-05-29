package com.weimob.saas.ec.limitation.exception;

import com.weimob.saas.ec.common.exception.BaseException;

/**
 * @author lujialin
 * @description 限购服务异常类
 * @date 2018/5/29 11:29
 */
public class LimitationBizException extends BaseException {

    private static final long serialVersionUID = -7648634747406657916L;

    private String bizInfo;

    public LimitationBizException(LimitationErrorCode errorCode) {
        super(errorCode.getErrorCode(), errorCode.getErrorMsg(), errorCode.getReturnMsg());
    }

    public LimitationBizException(LimitationErrorCode errorCode, Throwable t) {
        super(errorCode.getErrorCode(), errorCode.getErrorMsg(), errorCode.getReturnMsg(), t);
    }

    public LimitationBizException(LimitationErrorCode errorCode, String bizInfo, Throwable t) {
        super(errorCode.getErrorCode(), errorCode.getErrorMsg(), errorCode.getReturnMsg(), t);
        this.bizInfo = bizInfo;
    }

    public LimitationBizException(LimitationErrorCode errorCode, String bizInfo) {
        super(errorCode.getErrorCode(), errorCode.getErrorMsg());
        this.bizInfo = bizInfo;
    }

    @Override
    public String toString() {
        return "LimitationBizException [getErrorCode()=" + getErrorCode() + ", getErrorMsg()=" + getErrorMsg()
                + ", getReturnMsg()=" + getReturnMsg() + "]";
    }

}
