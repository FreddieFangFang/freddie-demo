package com.weimob.saas.ec.limitation.exception;

/**
 * @author lujialin
 * @description 限购服务错误码
 * @date 2018/5/29 11:30
 */
public enum LimitationErrorCode {
    /*********************** 公共返回码 *********************/
    /**
     * 错误编码总共13位<br />
     * 12 34 5 6 78 9~13<br />
     * 10 50 x x xx xxxxx<br />
     * 第1、2位:<br />
     * 10:电商<br />
     * 第3、4位:<br />
     * 80:限购服务<br />
     * 第5位:<br />
     * 0:base;1:soa;8:web(C端);9:mgr(B端)<br />
     * 第6位:<br />
     * 0:新增;1:编辑;2:查询<br />
     * 第7、8位:<br />
     * 01:应用级错误(前端参数错误);<br />
     * 02:交互异常(正常业务逻辑、非错误、需告知用户-如库存不足);<br />
     * 03:依赖级错误(service内部调用其它接口出错);<br />
     * 04:业务级错误(service自身处理出错、自身业务处理异常、NPE异常);<br />
     * 99:未知异常<br />
     * 第9到13位:<br />
     * 00001开始，自增
     */

    SUCCESS("000000", "处理成功"), FAIL("999999", "处理失败"),
    PID_IS_NULL("1080000100001", "商户PID为空"),
    WID_IS_NULL("1080000100002", "用户身份为空"),
    STORE_IS_NULL("1080000100003", "限购门店为空"),
    REQUEST_PARAM_IS_NULL("1080000100005", "请求参数为空"),
    FAIL_LIST_PARAM_TOO_LONG("1080000100006", "请求参数太长"),
    CHANNELTYPE_IS_NULL("1080000100007", "限购渠道为空"),
    BIZID_IS_NULL("1080000100008", "限购bizid为空"),
    BIZTYPE_IS_NULL("1080000100009", "限购biztype为空"),
    LIMITLEVEL_IS_NULL("1080000100010", "限购级别为空"),
    LIMITNUM_IS_NULL("1080000100011", "活动限购数量为空"),
    LIMITTYPE_IS_NULL("1080000100012", "限购维度limittype为空"),
    SOURCE_IS_NULL("1080000100013", "限购来源为空"),
    LIMITATION_IS_NULL("1080000100014", "限购信息记录为空"),
    GOODSID_IS_NULL("1080000100015", "商品id为空");

    private String errorCode;
    private String errorMsg;
    private String returnMsg;

    private LimitationErrorCode(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    private LimitationErrorCode(String errorCode, String errorMsg, String returnMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.returnMsg = returnMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getReturnMsg() {
        return (returnMsg == null || returnMsg.isEmpty()) ? errorMsg : returnMsg;
    }

    public static LimitationErrorCode getErrorCode(String errorCode) {
        for (LimitationErrorCode limitationErrorCode : LimitationErrorCode.values()) {
            if (limitationErrorCode.getErrorCode().equals(errorCode)) {
                return limitationErrorCode;
            }
        }
        return null;
    }
}
