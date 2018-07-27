package com.weimob.saas.ec.limitation.constant;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;

/**
 * @author lujialin
 * @description 限购常量
 * @date 2018/6/4 15:47
 */
public class LimitConstant {
    //不限购
    public static final int UNLIMITED_NUM = 0;
    //下单日志表初始化状态
    public static final int ORDER_LOG_STATUS_INIT = 0;
    //下单日志表回滚完成状态
    public static final int ORDER_LOG_STATUS_OVER = 1;
    //限时折扣活动采用冻结库存形式
    public static final int DISCOUNT_TYPE_STOCK = 1;
    //限时折扣活动采用可用sku形式
    public static final int DISCOUNT_TYPE_SKU = 2;
    //原始数据
    public static final int DATA_TYPE_INIT = 0;
    //更新数据
    public static final int DATA_TYPE_OVER = 1;
}
