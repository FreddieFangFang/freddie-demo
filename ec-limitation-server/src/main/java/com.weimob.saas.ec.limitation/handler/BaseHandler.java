package com.weimob.saas.ec.limitation.handler;

import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.entity.*;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.UserLimitBaseBo;
import com.weimob.saas.ec.limitation.model.convertor.LimitConvertor;
import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author lujialin
 * @description base
 * @date 2018/6/6 13:49
 */
public abstract class BaseHandler<T extends Comparable<T>> implements Handeler<T> {

    @Autowired
    protected LimitInfoDao limitInfoDao;

    protected final String LIMIT_PREFIX_ACTIVITY = "LIMIT_ACTIVITY_";
    protected final String LIMIT_PREFIX_GOODS = "LIMIT_GOODS_";
    protected final String LIMIT_PREFIX_SKU = "LIMIT_SKU_";

    @Override
    public String doHandler(List<T> vos) {
        String ticket = LimitContext.getTicket();
        try {
            checkParams(vos);

            //2.排序
            sortList(vos);

            //3.批量更新
            doBatchBizLogic(vos);

            //4.同步批量保存日志
            saveOrderChangeLog(vos);
        } finally {
            LimitContext.clearAll();
        }
        return ticket;
    }

    protected void saveOrderChangeLog(List<T> vos) {
    }

    protected void doBatchBizLogic(List<T> vos) {
    }

    protected void sortList(List<T> vos) {
        Collections.sort(vos);
    }

    protected void checkParams(List<T> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            throw new LimitationBizException(LimitationErrorCode.REQUEST_PARAM_IS_NULL);
        }
    }


    protected void groupingOrderGoodsRequestVoList(Map<String, Integer> orderGoodsLimitMap, Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap, List<UpdateUserLimitVo> vos) {
        for (UpdateUserLimitVo requestVo : vos) {

            String goodsKey = generateGoodsKey(requestVo);
            updateOrderGoodsMap(orderGoodsLimitMap, orderGoodsQueryMap, requestVo, goodsKey);
        }

    }

    private void updateOrderGoodsMap(Map<String, Integer> orderGoodsLimitMap,
                                     Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap,
                                     UpdateUserLimitVo requestVo,
                                     String mapKey) {
        //限购map的封装
        if (orderGoodsLimitMap.containsKey(mapKey)) {
            orderGoodsLimitMap.put(mapKey, orderGoodsLimitMap.get(mapKey) + requestVo.getGoodsNum());
        } else {
            orderGoodsLimitMap.put(mapKey, requestVo.getGoodsNum());
            //将活动id、goodsId、skuId对应的关系存入本地线程变量
            UserLimitBaseBo limitBaseBo = new UserLimitBaseBo();
            limitBaseBo.setPid(requestVo.getPid());
            limitBaseBo.setStoreId(requestVo.getStoreId());
            limitBaseBo.setWid(requestVo.getWid());
            limitBaseBo.setBizId(requestVo.getBizId());
            limitBaseBo.setBizType(requestVo.getBizType());


            //判断原先的map里是否有响应的值
            String swithCondition = mapKey.substring(0, mapKey.indexOf("_", mapKey.indexOf("_") + 1) + 1);
            switch (swithCondition) {
                case LIMIT_PREFIX_ACTIVITY:
                    buildOrderQueryMap(orderGoodsQueryMap, requestVo, LIMIT_PREFIX_ACTIVITY);
                    LimitContext.getLimitBo().getActivityIdLimitMap().put(requestVo.getBizId(), limitBaseBo);
                    break;
                case LIMIT_PREFIX_GOODS:
                    buildOrderQueryMap(orderGoodsQueryMap, requestVo, LIMIT_PREFIX_GOODS);
                    LimitContext.getLimitBo().getGoodsIdLimitMap().put(requestVo.getGoodsId(), limitBaseBo);
                    break;
                case LIMIT_PREFIX_SKU:
                    buildOrderQueryMap(orderGoodsQueryMap, requestVo, LIMIT_PREFIX_SKU);
                    LimitContext.getLimitBo().getSkuIdLimitMap().put(requestVo.getSkuId(), limitBaseBo);
                    break;
                default:
                    break;
            }
        }
    }

    private void buildOrderQueryMap(Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap,
                                    UpdateUserLimitVo requestVo,
                                    String mapKey) {
        if (!orderGoodsQueryMap.containsKey(mapKey)) {
            List<UpdateUserLimitVo> voList = new ArrayList<>();
            voList.add(requestVo);
            orderGoodsQueryMap.put(mapKey, voList);
        } else {
            orderGoodsQueryMap.get(mapKey).add(requestVo);
        }
    }

    private String generateActivityKey(UpdateUserLimitVo requestVo) {
        StringBuilder key = new StringBuilder(LIMIT_PREFIX_ACTIVITY);
        key.append(requestVo.getPid()).append("_");
        key.append(requestVo.getStoreId()).append("_");
        key.append(requestVo.getBizId());
        return key.toString();
    }

    private String generateGoodsKey(UpdateUserLimitVo requestVo) {
        StringBuilder key = new StringBuilder(LIMIT_PREFIX_GOODS);
        key.append(requestVo.getPid()).append("_");
        key.append(requestVo.getStoreId()).append("_");
        key.append(requestVo.getGoodsId());
        return key.toString();
    }

    private String generateSKUKey(UpdateUserLimitVo requestVo) {
        StringBuilder key = new StringBuilder(LIMIT_PREFIX_SKU);
        key.append(requestVo.getPid()).append("_");
        key.append(requestVo.getStoreId()).append("_");
        key.append(requestVo.getSkuId());
        return key.toString();
    }

    protected Boolean validLimitation(List<LimitInfoEntity> limitInfoEntityList,
                                      List<GoodsLimitInfoEntity> goodsLimitInfoEntityList,
                                      List<UserLimitEntity> activityLimitList,
                                      List<UserGoodsLimitEntity> userGoodsLimitList,
                                      List<SkuLimitInfoEntity> skuLimitInfoEntityList,
                                      Map<String, Integer> orderGoodsLimitMap) {


        Boolean isUpdate = true;
        //1. 判断用户是否有下单记录
        if (org.springframework.util.CollectionUtils.isEmpty(activityLimitList) || org.springframework.util.CollectionUtils.isEmpty(userGoodsLimitList)) {
            //用户第一次下单
            isUpdate = false;
        }

        //2. 将活动以及商品的后台设置限购信息分组，活动Id对应的限购数，goodsId对应的限购数
        Map<Long, LimitInfoEntity> activityMap = MapUtils.EMPTY_MAP;
        if (CollectionUtils.isNotEmpty(limitInfoEntityList)) {
            activityMap = groupingActivityEntityMap(limitInfoEntityList);
        }
        Map<Long, GoodsLimitInfoEntity> activityGoodsMap = groupingActivityGoodsEntityMap(goodsLimitInfoEntityList);

        //3. 校验某个活动的限购、活动商品的限购 (如果超过限购，文案提示需要显示较大的限购数量)

        for (Map.Entry<String, Integer> entry : orderGoodsLimitMap.entrySet()) {
            String entryKey = entry.getKey();
            int lastIndex = entryKey.lastIndexOf("_") + 1;
            //3.1 校验商品限购, 将用户该商品的购买记录与购买的数量相加 > 商品的限购

            String entryKeyPrefix = entryKey.substring(0, entryKey.indexOf("_", entryKey.indexOf("_") + 1) + 1);

            switch (entryKeyPrefix) {
                case LIMIT_PREFIX_GOODS:
                    long goodsId = Long.parseLong(entryKey.substring(lastIndex));
                    // 当用户没有购买记录的时候, 需要查看购买的记录, 当购买的商品超过商品限购记录则抛出异常
                    if (activityGoodsMap.get(goodsId).getLimitNum() == LimitConstant.UNLIMITED_NUM) {
                        break;
                    }


                    boolean includeCurrentGoods = false;
                    if (!org.springframework.util.CollectionUtils.isEmpty(userGoodsLimitList)) {
                        //用户有购买记录
                        for (UserGoodsLimitEntity goodsLimitEntity : userGoodsLimitList) {
                            if (goodsLimitEntity.getGoodsId() == goodsId) {
                                includeCurrentGoods = true;
                                int finalGoodsNum = entry.getValue() + goodsLimitEntity.getBuyNum();
                                //判断是否超出商品设置的限购数量
                                GoodsLimitInfoEntity GoodsEntity = activityGoodsMap.get(goodsId);
                                //等于0表示不限购
                                if (GoodsEntity.getLimitNum() != LimitConstant.UNLIMITED_NUM) {
                                    if (finalGoodsNum > activityGoodsMap.get(goodsId).getLimitNum()) {
                                        throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
                                    }
                                }
                            }
                        }
                    }
                    if (!includeCurrentGoods) {
                        if (entry.getValue() > activityGoodsMap.get(goodsId).getLimitNum()) {
                            throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
                        }
                    }
                    break;
                case LIMIT_PREFIX_ACTIVITY:
                    long activityId = Long.parseLong(entryKey.substring(lastIndex));
                    // 当用户没有购买记录的时候, 需要查看购买的记录, 当购买的商品超过商品限购记录则抛出异常
                    if (activityMap.get(activityId).getLimitNum() == LimitConstant.UNLIMITED_NUM) {
                        break;
                    }
                    boolean included = false;
                    if (!org.springframework.util.CollectionUtils.isEmpty(activityLimitList)) {
                        for (UserLimitEntity activityLimitEntity : activityLimitList) {
                            if (activityLimitEntity.getBizId() == activityId) {
                                included = true;
                                int finalGoodsNum = entry.getValue() + activityLimitEntity.getBuyNum();
                                //判断是否超出活动设置的限购数量
                                LimitInfoEntity activityEntity = activityMap.get(activityId);
                                //等于0表示不限购
                                if (activityEntity.getLimitNum() != LimitConstant.UNLIMITED_NUM) {
                                    if (finalGoodsNum > activityMap.get(activityId).getLimitNum()) {
                                        throw new LimitationBizException(LimitationErrorCode.BEYOND_ACTIVITY_LIMIT_NUM);
                                    }
                                }
                            }
                        }
                    }

                    if (!included) {
                        if (entry.getValue() > activityMap.get(activityId).getLimitNum()) {
                            throw new LimitationBizException(LimitationErrorCode.BEYOND_ACTIVITY_LIMIT_NUM);
                        }
                    }
                    break;
                //3.3 校验sku的可售数量, 将sku的已售数+购买数量 > 设置的阙值
                case LIMIT_PREFIX_SKU:
                    long skuId = Long.parseLong(entry.getKey().substring(lastIndex));
                    for (SkuLimitInfoEntity soldEntity : skuLimitInfoEntityList) {
                        if (skuId == soldEntity.getSkuId()) {
                            if (entry.getValue() + soldEntity.getSoldNum() > soldEntity.getLimitNum()) {
                                throw new LimitationBizException(LimitationErrorCode.BEYOND_SKU_LIMIT_NUM);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        return isUpdate;

    }

    protected void updateUserLimitRecord(Map<String, Integer> orderGoodsLimitMap,
                                         List<UserLimitEntity> activityLimitList,
                                         List<UserGoodsLimitEntity> userGoodsLimitRecodeList,
                                         Boolean isUpdate) {
        List<UserGoodsLimitEntity> goodsLimitEntityList = new ArrayList<>();
        List<UserLimitEntity> activityLimitEntityList = new ArrayList<>();
        List<SkuLimitInfoEntity> activityGoodsSoldEntityList = new ArrayList<>();
        UserLimitBaseBo baseBo = null;
        LimitInfoEntity limitInfoEntity =null;
        for (Map.Entry<String, Integer> entry : orderGoodsLimitMap.entrySet()) {
            int lastIndex = entry.getKey().lastIndexOf("_") + 1;
            String entryKey = entry.getKey();
            String entryKeyPrefix = entryKey.substring(0, entryKey.indexOf("_", entryKey.indexOf("_") + 1) + 1);

            switch (entryKeyPrefix) {
                //保存商品限购记录
                case LIMIT_PREFIX_GOODS:
                    long goodsId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getGoodsIdLimitMap().get(goodsId);
                    limitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    goodsLimitEntityList.add(LimitConvertor.convertGoodsLimit(baseBo, goodsId, entry.getValue(), limitInfoEntity));
                    break;
                //保存活动限购记录,多门店的时候是否会出现问题？
                case LIMIT_PREFIX_ACTIVITY:
                    long activityId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getActivityIdLimitMap().get(activityId);
                    limitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    activityLimitEntityList.add(LimitConvertor.convertActivityLimit(baseBo, activityId, entry.getValue(),limitInfoEntity));
                    break;
                //更新sku的售卖数量
                case LIMIT_PREFIX_SKU:
                    long skuId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getSkuIdLimitMap().get(skuId);
                    limitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    activityGoodsSoldEntityList.add(LimitConvertor.convertActivitySoldEntity(baseBo, skuId, entry.getValue(),limitInfoEntity));
                    break;
                default:
                    break;
            }
        }
        LimitContext.getLimitBo().setGoodsLimitEntityList(goodsLimitEntityList);
        LimitContext.getLimitBo().setActivityLimitEntityList(activityLimitEntityList);
        LimitContext.getLimitBo().setActivityGoodsSoldEntityList(activityGoodsSoldEntityList);
        LimitContext.getLimitBo().setActivityLimitRecodeList(activityLimitList);
        LimitContext.getLimitBo().setUserGoodsLimitRecodeList(userGoodsLimitRecodeList);
        LimitContext.getLimitBo().setUpdate(isUpdate);

    }

    private Map<Long, LimitInfoEntity> groupingActivityEntityMap(List<LimitInfoEntity> limitInfoEntityList) {
        Map<Long, LimitInfoEntity> resultMap = new HashMap<>();
        for (LimitInfoEntity entity : limitInfoEntityList) {
            resultMap.put(entity.getBizId(), entity);
        }
        return resultMap;
    }

    private Map<Long, GoodsLimitInfoEntity> groupingActivityGoodsEntityMap(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList) {
        Map<Long, GoodsLimitInfoEntity> resultMap = new HashMap<>();
        for (GoodsLimitInfoEntity entity : goodsLimitInfoEntityList) {
            resultMap.put(entity.getGoodsId(), entity);
        }
        return resultMap;
    }

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {

    }
}