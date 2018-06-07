package com.weimob.saas.ec.limitation.handler.limit;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.dao.GoodsLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.handler.LimitBizHandler;
import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lujialin
 * @description 商品限购校验handler
 * @date 2018/6/6 15:33
 */
@Service(value = "goodsLimitBizHandler")
public class GoodsLimitBizHandler extends BaseHandler implements LimitBizHandler {

    @Autowired
    private GoodsLimitInfoDao goodsLimitInfoDao;
    @Autowired
    private UserGoodsLimitDao userGoodsLimitDao;

    @Override
    public void doLimitHandler(List<UpdateUserLimitVo> vos) {
        Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap = new HashMap<>();
        groupingOrderGoodsRequestVoList(LimitContext.getLimitBo().getOrderGoodsLimitMap(), orderGoodsQueryMap, vos);

        List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitInfoDao.queryOrderGoodsLimitInfoList(orderGoodsQueryMap.get(LIMIT_PREFIX_GOODS));
        if (CollectionUtils.isEmpty(goodsLimitInfoEntityList)) {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_GOODS_IS_NULL);
        }

        List<UserGoodsLimitEntity> userGoodsLimitRecodeList = userGoodsLimitDao.queryUserOrderGoodsLimitList(vos);

        validLimitation(null, goodsLimitInfoEntityList, null,
                userGoodsLimitRecodeList, null, LimitContext.getLimitBo().getOrderGoodsLimitMap());

        updateUserLimitRecord(LimitContext.getLimitBo().getOrderGoodsLimitMap());

    }


}
