package com.weimob.saas.ec.limitation.handler.reverse;

import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
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
        HashSet<Long> goodsIdSet = new HashSet<>();
        List<SkuLimitInfoEntity> skuLimitInfoEntityList = new ArrayList<>();
        Long pid = null;
        Long limitId = null;
        for (LimitOrderChangeLogEntity limitOrderChangeLogEntity : logList) {
            pid = limitOrderChangeLogEntity.getPid();
            limitId = limitOrderChangeLogEntity.getLimitId();
            //为空。查的商品表记录，没有skuid
            if (limitOrderChangeLogEntity.getSkuId() == null) {

            } else {
                //查的sku表记录
                SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
                skuLimitInfoEntity.setPid(limitOrderChangeLogEntity.getPid());
                skuLimitInfoEntity.setLimitId(limitOrderChangeLogEntity.getLimitId());
                skuLimitInfoEntity.setGoodsId(limitOrderChangeLogEntity.getGoodsId());
                skuLimitInfoEntity.setSkuId(limitOrderChangeLogEntity.getSkuId());
                skuLimitInfoEntityList.add(skuLimitInfoEntity);
            }
            goodsIdSet.add(limitOrderChangeLogEntity.getGoodsId());
        }
        limitationService.reverseDeleteLimitation(pid, limitId, goodsIdSet, skuLimitInfoEntityList, logList);
    }
}
