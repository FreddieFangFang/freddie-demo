package com.weimob.saas.ec.limitation.utils;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;

import java.util.Objects;

public class CommonBizUtil {


    public static boolean isValidCombination(Integer bizType) {
        return Objects.equals(ActivityTypeEnum.COMBINATION_BUY.getType(), bizType);
    }


    public static boolean isValidPrivilegePrice(Integer bizType) {
        return Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), bizType);
    }


    public static boolean isValidDiscountStock(Integer bizType, Integer activityStockType) {
        return Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), bizType)
                && Objects.equals(activityStockType, LimitConstant.DISCOUNT_TYPE_STOCK);
    }


    public static boolean isValidDiscountSku(Integer bizType, Integer activityStockType) {
        return Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), bizType)
                && Objects.equals(activityStockType, LimitConstant.DISCOUNT_TYPE_SKU);
    }


    /**
     * @description 积分商城
     * @author haojie.jin
     * @date 4:02 PM 2018/11/8
     **/

    public static boolean isValidPoint(Integer bizType) {
        return Objects.equals(bizType, LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel());
    }



    /**
     * @description 需要校验sku限购的活动
     * @author haojie.jin
     * @date 5:00 PM 2018/11/8
     **/

    public static boolean isValidSkuLimit(Integer bizType,Integer activityStockType) {
        return isValidCombination(bizType)
                || isValidDiscountSku(bizType, activityStockType)
                || isValidPoint(bizType)
                || isValidPrivilegePrice(bizType);

    }
    public static boolean isValidUserActivityLimit(Integer bizType,Integer activityStockType) {
        return isValidDiscountStock(bizType, activityStockType)
                || isValidCombination(bizType)
                || isValidDiscountSku(bizType, activityStockType)
                || isValidPrivilegePrice(bizType);

    }


}