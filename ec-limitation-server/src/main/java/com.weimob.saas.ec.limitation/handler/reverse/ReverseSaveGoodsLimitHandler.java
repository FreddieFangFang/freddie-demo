package com.weimob.saas.ec.limitation.handler.reverse;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lujialin
 * @description 保存商品handler
 * @date 2018/7/27 14:00
 */
@Service(value = "reverseSaveGoodsLimitHandler")
public class ReverseSaveGoodsLimitHandler extends BaseHandler<UpdateUserLimitVo> {

    @Autowired
    private LimitationServiceImpl limitationService;

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {
        LimitInfoEntity entity = new LimitInfoEntity();
        entity.setPid(logList.get(0).getPid());
        entity.setLimitId(logList.get(0).getLimitId());
        List<Long> goodsList = new ArrayList<>();
        for (LimitOrderChangeLogEntity limitOrderChangeLogEntity : logList) {
            goodsList.add(limitOrderChangeLogEntity.getGoodsId());
        }
        limitationService.reverseSaveGoodsLimit(entity, goodsList);
    }


}
