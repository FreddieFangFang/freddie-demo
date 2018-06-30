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

    /*****************************应用级异常01**********************************/
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
    GOODSID_IS_NULL("1080000100015", "商品id为空"),
    GOODSNUM_IS_NULL("1080000100016", "商品购买数量为空"),
    GOODSLIMITNUM_IS_NULL("1080000100017", "商品限购数量为空"),
    SKUINFO_IS_NULL("1080000100018", "限购sku信息为空"),
    ORDERNO_IS_NULL("1080000100019", "订单号为空"),
    GOODSNUM_IS_ILLEGAL("1080000100020", "商品购买数量不合法"),
    REPEAT_ORDER_DEDUCT_LIMIT("1080000100021", "重复取消限购"),
    SKUID_IS_NULL("1080000100022", "skuId为空"),


    /*****************************交互异常02**********************************/
    BEYOND_GOODS_LIMIT_NUM("1080000200001", "超出商品限购"),
    LIMIT_GOODS_IS_NULL("1080000200002", "商品限购记录不存在"),
    BEYOND_ACTIVITY_LIMIT_NUM("1080000200003", "超出活动限购"),
    BEYOND_SKU_LIMIT_NUM("1080000200004", "超出sku限购"),
    LIMIT_ACTIVITY_IS_NULL("1080000200005", "活动限购记录为空"),
    LIMIT_SKU_IS_NULL("1080000200006", "SKU限购记录为空"),
    INVALID_REVERSE_TICKET("1080000200006", "非法的回滚ticket"),


    /*****************************业务异常04**********************************/
    SQL_UPDATE_USER_GOODS_LIMIT_ERROR("1080000400001", "数据库更新异常"),
    SQL_UPDATE_USER_LIMIT_ERROR("1080000400002", "数据库更新异常"),
    SQL_UPDATE_SKU_LIMIT_ERROR("1080000400003", "数据库更新异常"),
    SQL_INSERT_ORDER_LOG_ERROR("1080000400004", "数据库插入异常"),
    SQL_QUERY_ORDER_CHANGE_LOG_ERROR("1080000400005", "查询下单记录异常"),
    SQL_UPDATE_ORDER_CHANGE_LOG_ERROR("1080000400006", "更新下单的日志状态异常"),
    ;

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
