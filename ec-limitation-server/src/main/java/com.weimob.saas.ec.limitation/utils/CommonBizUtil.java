package com.weimob.saas.ec.limitation.utils;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;

import java.util.Objects;

public class CommonBizUtil {


    public static boolean isValidCombination(Integer bizType) {
        return Objects.equals(ActivityTypeEnum.COMBINATION_BUY.getType(), bizType);
    }

    public static boolean isValidCommunityGroupon(Integer bizType) {
        return Objects.equals(ActivityTypeEnum.COMMUNITY_GROUPON.getType(), bizType);
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

    public static boolean isValidDiscount(Integer bizType) {
        return Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), bizType);
    }

    public static boolean isValidNynj(Integer bizType) {
        return Objects.equals(ActivityTypeEnum.NYNJ.getType(), bizType);
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
     * @description 需要校验活动限购的活动
     * @author qi.he
     * @date 2018/11/19 16:26
     **/
    public static boolean isValidActivityLimit(Integer bizType) {
        return isValidDiscount(bizType)
                || isValidPrivilegePrice(bizType)
                || isValidCombination(bizType)
                || isValidNynj(bizType);
    }

    /**
     * @description 需要校验商品限购的活动
     * @author qi.he
     * @date 2018/11/19 16:26
     **/
    public static boolean isValidGoodsLimit(Integer bizType) {
        return isValidDiscount(bizType)
                || isValidPoint(bizType)
                || isValidPrivilegePrice(bizType);
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
                || isValidPrivilegePrice(bizType)
                || isValidCommunityGroupon(bizType);
    }

    public static boolean isValidUserActivityLimit(Integer bizType,Integer activityStockType) {
        return isValidDiscountStock(bizType, activityStockType)
                || isValidCombination(bizType)
                || isValidDiscountSku(bizType, activityStockType)
                || isValidPrivilegePrice(bizType);
    }

    public static boolean isValidGoodsSkuLimit(Integer bizType,Integer activityStockType) {
        return isValidDiscountSku(bizType, activityStockType)
                || isValidPoint(bizType)
                || isValidPrivilegePrice(bizType);
    }
}
