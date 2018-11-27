package com.weimob.saas.ec.limitation.handler.reverse;

import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.utils.CommonBizUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lujialin
 * @description 回滚删除活动限购接口
 * @date 2018/7/30 11:14
 */
@Service(value = "reverseDeleteLimitationHandler")
public class ReverseDeleteLimitationHandler extends BaseHandler<UpdateUserLimitVo> {

    @Autowired
    private LimitationServiceImpl limitationService;

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {
        HashSet<Long> goodsIdSet = null;
        List<SkuLimitInfoEntity> skuLimitInfoEntityList = null;
        Long pid = null;
        Long limitId = null;
        Integer bizType = logList.get(0).getBizType();

        //N元N件 没记录商品与sku
        if (CommonBizUtil.isValidNynj(bizType)) {
            pid = logList.get(0).getPid();
            limitId = logList.get(0).getLimitId();
        } else {
            goodsIdSet = new HashSet<>();
            skuLimitInfoEntityList = new ArrayList<>();
            for (LimitOrderChangeLogEntity limitOrderChangeLogEntity : logList) {
                pid = limitOrderChangeLogEntity.getPid();
                limitId = limitOrderChangeLogEntity.getLimitId();
                //查的sku表记录
                if (limitOrderChangeLogEntity.getSkuId() != null) {
                    SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
                    skuLimitInfoEntity.setPid(limitOrderChangeLogEntity.getPid());
                    skuLimitInfoEntity.setLimitId(limitOrderChangeLogEntity.getLimitId());
                    skuLimitInfoEntity.setGoodsId(limitOrderChangeLogEntity.getGoodsId());
                    skuLimitInfoEntity.setSkuId(limitOrderChangeLogEntity.getSkuId());
                    skuLimitInfoEntityList.add(skuLimitInfoEntity);
                }
                goodsIdSet.add(limitOrderChangeLogEntity.getGoodsId());
            }
        }
        limitationService.reverseDeleteLimitation(pid, limitId, goodsIdSet, skuLimitInfoEntityList, logList);
    }
}
