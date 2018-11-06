package com.weimob.saas.ec.limitation.constant;

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
    //适用部分门店类型
    public static final int SELECT_PART_STORE = 2;
    //已删除
    public static final int DELETED = 1;
    //限购回滚队列
    public static final String KEY_LIMITATION_REVERSE_QUEUE = "limitationReverseQueue";
    //限购回滚队列
    public static final String EC_STRESS_KEY_LIMITATION_REVERSE_QUEUE = "stressLimitationReverseQueue";
    //限购回滚定时任务开关开启
    public static final int LIMITATION_REVERSE_TASK_OFF = 0;
    //压测限购回滚定时任务标志
    public static final int LIMITATION_REVERSE_IS_STRESS = 1;
    //真实限购回滚定时任务标志
    public static final int LIMITATION_REVERSE_NO_STRESS = 0;
    //默认RpcId
    public static final String DEFAULT_RPC_ID = "0.0.1";
}
