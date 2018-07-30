package com.weimob.saas.ec.limitation.handler.reverse;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lujialin
 * @description 删除限购商品的回滚handler
 * @date 2018/7/27 15:31
 */
@Service(value = "reverseDeleteGoodsLimitHandler")
public class ReverseDeleteGoodsLimitHandler extends BaseHandler<UpdateUserLimitVo> {

    @Autowired
    private LimitationServiceImpl limitationService;

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {
        Map<Long, List<Long>> limitIdGoodsIdMap = new HashMap<>();
        Long pid = null;
        Integer bizType = null;
        for (LimitOrderChangeLogEntity limitOrderChangeLogEntity : logList) {
            pid = limitOrderChangeLogEntity.getPid();
            bizType = limitOrderChangeLogEntity.getBizType();
            if (CollectionUtils.isEmpty(limitIdGoodsIdMap.get(limitOrderChangeLogEntity.getLimitId()))) {
                List<Long> goodsIdList = new ArrayList<>();
                goodsIdList.add(limitOrderChangeLogEntity.getGoodsId());
                limitIdGoodsIdMap.put(limitOrderChangeLogEntity.getLimitId(), goodsIdList);
            } else {
                limitIdGoodsIdMap.get(limitOrderChangeLogEntity.getLimitId()).add(limitOrderChangeLogEntity.getGoodsId());
            }
        }

        limitationService.reverseDeleteGoodsLimit(pid, bizType, limitIdGoodsIdMap);
    }
}
