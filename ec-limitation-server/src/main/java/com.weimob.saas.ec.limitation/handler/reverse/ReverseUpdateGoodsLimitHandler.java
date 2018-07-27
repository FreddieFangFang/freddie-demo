package com.weimob.saas.ec.limitation.handler.reverse;

import com.weimob.saas.ec.limitation.common.LimitTypeEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lujialin
 * @description 更新商品限购的回滚接口
 * @date 2018/7/25 18:40
 */
@Service(value = "reverseUpdateGoodsLimitHandler")
public class ReverseUpdateGoodsLimitHandler extends BaseHandler<UpdateUserLimitVo> {

    @Autowired
    private LimitationServiceImpl limitationService;

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {
        List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = new ArrayList<>();
        List<GoodsLimitInfoEntity> newGoodsLimitInfoEntityList = new ArrayList<>();
        List<SkuLimitInfoEntity> skuLimitInfoEntityList = new ArrayList<>();
        List<SkuLimitInfoEntity> newSkuLimitInfoEntityList = new ArrayList<>();
        Map<Long, SkuLimitInfoEntity> skuMap = new HashMap<>();
        for (LimitOrderChangeLogEntity limitOrderChangeLogEntity : logList) {
            if (limitOrderChangeLogEntity.getSkuId() != null) {
                //构建sku的回滚参数
                SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
                skuLimitInfoEntity.setLimitId(limitOrderChangeLogEntity.getLimitId());
                skuLimitInfoEntity.setPid(limitOrderChangeLogEntity.getPid());
                skuLimitInfoEntity.setGoodsId(limitOrderChangeLogEntity.getGoodsId());
                skuLimitInfoEntity.setSkuId(limitOrderChangeLogEntity.getSkuId());
                skuLimitInfoEntity.setLimitNum(limitOrderChangeLogEntity.getBuyNum());
                skuLimitInfoEntity.setLimitType(LimitTypeEnum.LIMIT_TYPE_ALL.getType());
                if (Objects.equals(LimitConstant.DATA_TYPE_INIT, limitOrderChangeLogEntity.getIsOriginal())) {
                    skuLimitInfoEntityList.add(skuLimitInfoEntity);
                } else {
                    newSkuLimitInfoEntityList.add(skuLimitInfoEntity);
                    skuMap.put(skuLimitInfoEntity.getSkuId(), skuLimitInfoEntity);
                }
            } else {
                //构建goods的回滚参数
                GoodsLimitInfoEntity infoEntity = new GoodsLimitInfoEntity();
                infoEntity.setLimitId(limitOrderChangeLogEntity.getLimitId());
                infoEntity.setPid(limitOrderChangeLogEntity.getPid());
                infoEntity.setGoodsId(limitOrderChangeLogEntity.getGoodsId());
                infoEntity.setLimitLevel(limitOrderChangeLogEntity.getLimitLevel());
                infoEntity.setLimitNum(limitOrderChangeLogEntity.getBuyNum());
                infoEntity.setLimitType(LimitTypeEnum.LIMIT_TYPE_ONE.getType());
                if (Objects.equals(LimitConstant.DATA_TYPE_INIT, limitOrderChangeLogEntity.getIsOriginal())) {
                    goodsLimitInfoEntityList.add(infoEntity);
                } else {
                    newGoodsLimitInfoEntityList.add(infoEntity);
                }
            }
        }
        //商品可以更新，sku要先比较再操作
        List<SkuLimitInfoEntity> updateSkuList = new ArrayList<>();
        List<SkuLimitInfoEntity> insertSkuList = new ArrayList<>();
        List<SkuLimitInfoEntity> deleteSkuList = new ArrayList<>();
        for (SkuLimitInfoEntity skuLimitInfoEntity : skuLimitInfoEntityList) {
            if (skuMap.get(skuLimitInfoEntity.getSkuId()) != null) {
                //更新操作，删除元素
                skuLimitInfoEntity.setLimitNum((skuMap.get(skuLimitInfoEntity.getSkuId()).getLimitNum()) * -1);
                updateSkuList.add(skuLimitInfoEntity);
                skuMap.remove(skuLimitInfoEntity.getSkuId());
            } else {
                //删除操作，进行恢复
                insertSkuList.add(skuLimitInfoEntity);
            }
        }
        //skuMap还剩下的就是新增的，进行删除
        deleteSkuList.addAll(skuMap.values());
        limitationService.reverseUpdateGoodsLimit(goodsLimitInfoEntityList, updateSkuList, insertSkuList, deleteSkuList);
    }
}
