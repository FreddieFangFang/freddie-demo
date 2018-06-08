package com.weimob.saas.ec.limitation.handler.limit;

import com.weimob.saas.ec.limitation.dao.SkuLimitInfoDao;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.handler.LimitBizHandler;
import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lujialin
 * @description sku限购校验handler
 * @date 2018/6/6 15:53
 */
@Service(value = "skuLimitBizHandler")
public class SkuLimitBizHandler extends BaseHandler implements LimitBizHandler {

    @Autowired
    private SkuLimitInfoDao skuLimitInfoDao;

    @Override
    public void doLimitHandler(List<UpdateUserLimitVo> vos) {
        Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap = new HashMap<>();
        Map<String, Integer> orderSkuValidMap = new HashMap();
        groupingOrderSkuRequestVoList(LimitContext.getLimitBo().getOrderGoodsLimitMap(), orderGoodsQueryMap, vos, orderSkuValidMap);

        List<SkuLimitInfo> skuLimitInfoList = skuLimitInfoDao.queryOrderSkuLimitInfoList(vos);
        if (CollectionUtils.isEmpty(skuLimitInfoList)) {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_SKU_IS_NULL);
        }

        validLimitation(null, null,
                null, null, skuLimitInfoList, orderSkuValidMap);

        updateUserLimitRecord(LimitContext.getLimitBo().getOrderGoodsLimitMap());

    }


}
