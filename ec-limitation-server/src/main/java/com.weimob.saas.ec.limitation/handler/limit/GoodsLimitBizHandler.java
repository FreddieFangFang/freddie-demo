package com.weimob.saas.ec.limitation.handler.limit;

import com.weimob.saas.ec.limitation.dao.GoodsLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.handler.LimitBizHandler;
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
        Map<String, Integer> localOrderBuyNumMap = new HashMap();
        /** 1 处理入参数据 **/
        groupingOrderGoodsRequestVoList(LimitContext.getLimitBo().getGlobalOrderBuyNumMap(), orderGoodsQueryMap, vos, localOrderBuyNumMap);
        /** 2 查询商品限购信息 **/
        List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitInfoDao.listOrderGoodsLimit(orderGoodsQueryMap.get(LIMIT_PREFIX_GOODS));
        if (CollectionUtils.isEmpty(goodsLimitInfoEntityList)) {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_GOODS_IS_NULL);
        }
        /** 3 查询用户购买记录 **/
        List<UserGoodsLimitEntity> userGoodsLimitRecodeList = userGoodsLimitDao.listOrderUserGoodsLimit(vos);
        /** 4 校验商品是否超出限购 **/
        validLimitation(null, goodsLimitInfoEntityList, null,
                userGoodsLimitRecodeList, null, localOrderBuyNumMap);
        /** 5 封装更新数据库入参 **/
        updateUserLimitRecord(localOrderBuyNumMap);

    }


}
