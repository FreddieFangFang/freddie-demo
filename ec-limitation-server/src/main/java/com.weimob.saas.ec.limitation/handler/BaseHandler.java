package com.weimob.saas.ec.limitation.handler;

import com.alibaba.dubbo.rpc.RpcContext;
import com.weimob.saas.ec.limitation.common.LimitServiceNameEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitOrderChangeLogDao;
import com.weimob.saas.ec.limitation.entity.*;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.UserLimitBaseBo;
import com.weimob.saas.ec.limitation.model.convertor.LimitConvertor;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.thread.SaveLimitChangeLogThread;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.saas.ec.limitation.utils.MapKeyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.*;

/**
 * @author lujialin
 * @description base
 * @date 2018/6/6 13:49
 */
public abstract class BaseHandler<T extends Comparable<T>> implements Handler<T> {

    @Autowired
    protected LimitInfoDao limitInfoDao;
    @Autowired
    protected LimitOrderChangeLogDao limitOrderChangeLogDao;
    @Autowired
    private ThreadPoolTaskExecutor threadExecutor;

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
        //调用dao保存日志
        //异步写日志 SaveLimitChangeLogThread
        List<LimitOrderChangeLogEntity> logEntityList = new ArrayList<>();
        LimitOrderChangeLogEntity logEntity = null;
        // 保存日志表需要从limit_info表查询数据 需要切换数据源
        RpcContext rpcContext = RpcContext.getContext();
        String globalTicket = rpcContext.getGlobalTicket();
        if (globalTicket != null && globalTicket.startsWith("EC_STRESS-")) {
            rpcContext.setGlobalTicket(null);
        }
        for (T inputVo : vos) {
            logEntity = createOrderChangeLog(inputVo);
            logEntityList.add(logEntity);
        }
        // 异步线程调用需要传globalTicket
        threadExecutor.execute(new SaveLimitChangeLogThread(limitOrderChangeLogDao, logEntityList, RpcContext.getContext()));
    }

    protected Map<Integer, List<UpdateUserLimitVo>> buildActivityMap(List<UpdateUserLimitVo> vos) {
        Map<Integer, List<UpdateUserLimitVo>> activityMap = new HashMap<>();
        for (UpdateUserLimitVo limitVo : vos) {
            if (CollectionUtils.isEmpty(activityMap.get(limitVo.getBizType()))) {
                List<UpdateUserLimitVo> updateUserLimitVoList = new ArrayList<>();
                updateUserLimitVoList.add(limitVo);
                activityMap.put(limitVo.getBizType(), updateUserLimitVoList);
            } else {
                activityMap.get(limitVo.getBizType()).add(limitVo);
            }
        }
        return activityMap;
    }

    protected LimitOrderChangeLogEntity createOrderChangeLog(T vo) {
        return null;
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


    protected void groupingOrderGoodsRequestVoList(Map<String, Integer> orderGoodsLimitMap, Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap, List<UpdateUserLimitVo> vos, Map<String, Integer> orderGoodsValidMap) {
        for (UpdateUserLimitVo requestVo : vos) {

            String goodsKey = generateGoodsKey(requestVo);
            updateOrderGoodsMap(orderGoodsLimitMap, orderGoodsQueryMap, requestVo, goodsKey);
            updateOrderGoodsMap(orderGoodsValidMap, orderGoodsQueryMap, requestVo, goodsKey);
        }

    }

    protected void groupingOrderActivityRequestVoList(Map<String, Integer> orderGoodsLimitMap, Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap, List<UpdateUserLimitVo> vos, Map<String, Integer> orderActivityValidMap) {
        for (UpdateUserLimitVo requestVo : vos) {

            String goodsKey = generateActivityKey(requestVo);
            updateOrderGoodsMap(orderGoodsLimitMap, orderGoodsQueryMap, requestVo, goodsKey);
            updateOrderGoodsMap(orderActivityValidMap, orderGoodsQueryMap, requestVo, goodsKey);
        }

    }

    protected void groupingOrderSkuRequestVoList(Map<String, Integer> orderGoodsLimitMap, Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap, List<UpdateUserLimitVo> vos, Map<String, Integer> orderSkuValidMap) {
        for (UpdateUserLimitVo requestVo : vos) {

            String goodsKey = generateSKUKey(requestVo);
            updateOrderGoodsMap(orderGoodsLimitMap, orderGoodsQueryMap, requestVo, goodsKey);
            updateOrderGoodsMap(orderSkuValidMap, orderGoodsQueryMap, requestVo, goodsKey);
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
            limitBaseBo.setGoodsId(requestVo.getGoodsId());


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

    protected void validLimitation(List<LimitInfoEntity> limitInfoEntityList,
                                   List<GoodsLimitInfoEntity> goodsLimitInfoEntityList,
                                   List<UserLimitEntity> activityLimitList,
                                   List<UserGoodsLimitEntity> userGoodsLimitList,
                                   List<SkuLimitInfoEntity> skuLimitInfoEntityList,
                                   Map<String, Integer> orderGoodsLimitMap) {


        //2. 将活动以及商品的后台设置限购信息分组，活动Id对应的限购数，goodsId对应的限购数
        Map<Long, LimitInfoEntity> activityMap = MapUtils.EMPTY_MAP;
        if (CollectionUtils.isNotEmpty(limitInfoEntityList)) {
            activityMap = groupingActivityEntityMap(limitInfoEntityList);
        }
        Map<Long, List<GoodsLimitInfoEntity>> activityGoodsMap = MapUtils.EMPTY_MAP;
        if (CollectionUtils.isNotEmpty(goodsLimitInfoEntityList)) {
            activityGoodsMap = groupingActivityGoodsEntityMap(goodsLimitInfoEntityList);
        }

        //3. 校验某个活动的限购、活动商品的限购 (如果超过限购，文案提示需要显示较大的限购数量)

        for (Map.Entry<String, Integer> entry : orderGoodsLimitMap.entrySet()) {
            String entryKey = entry.getKey();
            int lastIndex = entryKey.lastIndexOf("_") + 1;
            //3.1 校验商品限购, 将用户该商品的购买记录与购买的数量相加 > 商品的限购

            String entryKeyPrefix = entryKey.substring(0, entryKey.indexOf("_", entryKey.indexOf("_") + 1) + 1);

            switch (entryKeyPrefix) {
                case LIMIT_PREFIX_GOODS:
                    long goodsId = Long.parseLong(entryKey.substring(lastIndex));
                    Long storeId = Long.parseLong(entryKey.split("_")[3]);
                    // 当用户没有购买记录的时候, 需要查看购买的记录, 当购买的商品超过商品限购记录则抛出异常
                    if (CollectionUtils.isEmpty(activityGoodsMap.get(goodsId))) {
                        throw new LimitationBizException(LimitationErrorCode.LIMIT_GOODS_IS_NULL);
                    }
                    Integer goodsLimitNum = null;
                    Integer pidGoodsLimitNum = null;
                    for (GoodsLimitInfoEntity goodsLimitInfoEntity : activityGoodsMap.get(goodsId)) {
                        if (goodsLimitInfoEntity.getLimitLevel() == 0) {
                            goodsLimitNum = goodsLimitInfoEntity.getLimitNum();
                        } else {
                            pidGoodsLimitNum = goodsLimitInfoEntity.getLimitNum();
                        }
                    }
                    if (pidGoodsLimitNum == null) {
                        pidGoodsLimitNum = 0;
                    }
                    if (LimitConstant.UNLIMITED_NUM == goodsLimitNum && LimitConstant.UNLIMITED_NUM == pidGoodsLimitNum) {
                        break;
                    }


                    boolean includeCurrentGoods = false;
                    if (CollectionUtils.isNotEmpty(userGoodsLimitList)) {
                        Map<Long, Integer> goodsAlreadayBuyNumMap = new HashMap<>();
                        //用户有购买记录
                        for (UserGoodsLimitEntity goodsLimitEntity : userGoodsLimitList) {
                            if (goodsLimitEntity.getGoodsId() == goodsId && Objects.equals(storeId, goodsLimitEntity.getStoreId())) {
                                includeCurrentGoods = true;
                                int finalGoodsNum = entry.getValue() + goodsLimitEntity.getBuyNum();
                                //等于0表示不限购
                                if (goodsLimitNum != LimitConstant.UNLIMITED_NUM) {
                                    if (finalGoodsNum > goodsLimitNum) {
                                        throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
                                    }
                                }
                            }
                            if (goodsAlreadayBuyNumMap.get(goodsLimitEntity.getGoodsId()) == null) {
                                goodsAlreadayBuyNumMap.put(goodsLimitEntity.getGoodsId(), goodsLimitEntity.getBuyNum());
                            } else {
                                goodsAlreadayBuyNumMap.put(goodsLimitEntity.getGoodsId(), goodsAlreadayBuyNumMap.get(goodsLimitEntity.getGoodsId()) + goodsLimitEntity.getBuyNum());
                            }
                        }
                        //校验店铺级商品限购
                        if (pidGoodsLimitNum != LimitConstant.UNLIMITED_NUM) {
                            int finalGoodsNum = entry.getValue() + (goodsAlreadayBuyNumMap.get(goodsId) == null ? 0 : goodsAlreadayBuyNumMap.get(goodsId));
                            if (finalGoodsNum > pidGoodsLimitNum) {
                                throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
                            }
                        }
                    }
                    if (!includeCurrentGoods) {
                        if (goodsLimitNum != LimitConstant.UNLIMITED_NUM && entry.getValue() > goodsLimitNum) {
                            throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
                        }
                        if (pidGoodsLimitNum != LimitConstant.UNLIMITED_NUM && entry.getValue() > pidGoodsLimitNum) {
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
                    if (CollectionUtils.isNotEmpty(activityLimitList)) {
                        //用户活动购买记录map
                        Map<Long, Integer> activityUserLimitNumMap = new HashMap<>();
                        for (UserLimitEntity vo : activityLimitList) {
                            //多门店下单，进行合并
                            if (activityUserLimitNumMap.get(vo.getBizId()) == null) {
                                activityUserLimitNumMap.put(vo.getBizId(), vo.getBuyNum());
                            } else {
                                Integer buyNum = activityUserLimitNumMap.get(vo.getBizId());
                                activityUserLimitNumMap.put(vo.getBizId(), vo.getBuyNum() + buyNum);
                            }
                        }
                        if (activityUserLimitNumMap.get(activityId) != null) {
                            included = true;
                            int finalGoodsNum = entry.getValue() + activityUserLimitNumMap.get(activityId);
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

    }

    protected void updateUserLimitRecord(Map<String, Integer> orderGoodsLimitMap) {
        List<UserGoodsLimitEntity> goodsLimitEntityList = new ArrayList<>();
        List<UserLimitEntity> activityLimitEntityList = new ArrayList<>();
        List<SkuLimitInfoEntity> activityGoodsSoldEntityList = new ArrayList<>();
        UserLimitBaseBo baseBo = null;
        LimitInfoEntity limitInfoEntity = null;
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
                    //活动过期
                    if (limitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.INVALID_LIMITATION_ACTIVITY);
                    }
                    goodsLimitEntityList.add(LimitConvertor.convertGoodsLimit(baseBo, goodsId, entry.getValue(), limitInfoEntity));
                    break;
                //保存活动限购记录,多门店的时候是否会出现问题？
                case LIMIT_PREFIX_ACTIVITY:
                    long activityId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getActivityIdLimitMap().get(activityId);
                    limitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    if (limitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.INVALID_LIMITATION_ACTIVITY);
                    }
                    activityLimitEntityList.add(LimitConvertor.convertActivityLimit(baseBo, activityId, entry.getValue(), limitInfoEntity));
                    break;
                //更新sku的售卖数量
                case LIMIT_PREFIX_SKU:
                    long skuId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getSkuIdLimitMap().get(skuId);
                    limitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    if (limitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.INVALID_LIMITATION_ACTIVITY);
                    }
                    activityGoodsSoldEntityList.add(LimitConvertor.convertActivitySoldEntity(baseBo, skuId, entry.getValue(), limitInfoEntity));
                    break;
                default:
                    break;
            }
        }
        if (CollectionUtils.isNotEmpty(goodsLimitEntityList)) {
            LimitContext.getLimitBo().setGoodsLimitEntityList(goodsLimitEntityList);
        }
        if (CollectionUtils.isNotEmpty(activityLimitEntityList)) {
            LimitContext.getLimitBo().setActivityLimitEntityList(activityLimitEntityList);
        }
        if (CollectionUtils.isNotEmpty(activityGoodsSoldEntityList)) {
            LimitContext.getLimitBo().setActivityGoodsSoldEntityList(activityGoodsSoldEntityList);
        }
    }

    private Map<Long, LimitInfoEntity> groupingActivityEntityMap(List<LimitInfoEntity> limitInfoEntityList) {
        Map<Long, LimitInfoEntity> resultMap = new HashMap<>();
        for (LimitInfoEntity entity : limitInfoEntityList) {
            resultMap.put(entity.getBizId(), entity);
        }
        return resultMap;
    }

    private Map<Long, List<GoodsLimitInfoEntity>> groupingActivityGoodsEntityMap(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList) {
        Map<Long, List<GoodsLimitInfoEntity>> resultMap = new HashMap<>();
        for (GoodsLimitInfoEntity entity : goodsLimitInfoEntityList) {
            if (CollectionUtils.isEmpty(resultMap.get(entity.getGoodsId()))) {
                List<GoodsLimitInfoEntity> entityList = new ArrayList<>();
                entityList.add(entity);
                resultMap.put(entity.getGoodsId(), entityList);
            } else {
                resultMap.get(entity.getGoodsId()).add(entity);
            }
        }
        return resultMap;
    }

    @Override
    public void doReverse(List<LimitOrderChangeLogEntity> logList) {

    }

    protected LimitServiceNameEnum getServiceName() {
        return null;
    }
}
