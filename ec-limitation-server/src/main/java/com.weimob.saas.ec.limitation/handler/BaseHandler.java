package com.weimob.saas.ec.limitation.handler;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitLevelEnum;
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
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.thread.SaveLimitChangeLogThread;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import com.weimob.saas.ec.limitation.utils.VerifyParamUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.aspectj.lang.annotation.After;
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
    @Autowired
    private LimitationServiceImpl limitationService;

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
        for (T inputVo : vos) {
            logEntity = createOrderChangeLog(inputVo);
            logEntityList.add(logEntity);
        }
        threadExecutor.execute(new SaveLimitChangeLogThread(limitOrderChangeLogDao, logEntityList));
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


    protected void groupingOrderGoodsRequestVoList(Map<String, Integer> globalOrderBuyNumMap,
                                                   Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap,
                                                   List<UpdateUserLimitVo> vos,
                                                   Map<String, Integer> localOrderBuyNumMap) {
        for (UpdateUserLimitVo requestVo : vos) {
            String goodsKey = generateGoodsKey(requestVo);
            updateOrderGoodsMap(globalOrderBuyNumMap, orderGoodsQueryMap, requestVo, goodsKey);
            updateOrderGoodsMap(localOrderBuyNumMap, orderGoodsQueryMap, requestVo, goodsKey);
        }
    }

    protected void groupingOrderActivityRequestVoList(Map<String, Integer> globalOrderBuyNumMap,
                                                      Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap,
                                                      List<UpdateUserLimitVo> vos,
                                                      Map<String, Integer> localOrderBuyNumMap) {
        for (UpdateUserLimitVo requestVo : vos) {
            String activityKey = generateActivityKey(requestVo);
            updateOrderGoodsMap(globalOrderBuyNumMap, orderGoodsQueryMap, requestVo, activityKey);
            updateOrderGoodsMap(localOrderBuyNumMap, orderGoodsQueryMap, requestVo, activityKey);
        }
    }

    protected void groupingOrderSkuRequestVoList(Map<String, Integer> globalOrderBuyNumMap,
                                                 Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap,
                                                 List<UpdateUserLimitVo> vos,
                                                 Map<String, Integer> localOrderBuyNumMap) {
        for (UpdateUserLimitVo requestVo : vos) {
            String skuKey = generateSKUKey(requestVo);
            updateOrderGoodsMap(globalOrderBuyNumMap, orderGoodsQueryMap, requestVo, skuKey);
            updateOrderGoodsMap(localOrderBuyNumMap, orderGoodsQueryMap, requestVo, skuKey);
        }
    }

    private void updateOrderGoodsMap(Map<String, Integer> orderBuyNumMap,
                                     Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap,
                                     UpdateUserLimitVo requestVo,
                                     String mapKey) {
        //限购map的封装
        if (orderBuyNumMap.containsKey(mapKey)) {
            orderBuyNumMap.put(mapKey, orderBuyNumMap.get(mapKey) + requestVo.getGoodsNum());
        } else {
            orderBuyNumMap.put(mapKey, requestVo.getGoodsNum());
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
                    if (Objects.equals(requestVo.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
                        LimitContext.getLimitBo().getSkuIdLimitMap().put(requestVo.getBizId(), limitBaseBo);
                    } else {
                        LimitContext.getLimitBo().getSkuIdLimitMap().put(requestVo.getSkuId(), limitBaseBo);
                    }
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
        if (Objects.equals(requestVo.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
            key.append(requestVo.getBizId());
        } else {
            key.append(requestVo.getSkuId());
        }
        return key.toString();
    }

    protected void validLimitation(List<LimitInfoEntity> limitInfoEntityList,
                                   List<GoodsLimitInfoEntity> goodsLimitInfoEntityList,
                                   List<UserLimitEntity> activityLimitList,
                                   List<UserGoodsLimitEntity> userGoodsLimitList,
                                   List<SkuLimitInfoEntity> skuLimitInfoEntityList,
                                   Map<String, Integer> orderBuyNumMap) {


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

        for (Map.Entry<String, Integer> entry : orderBuyNumMap.entrySet()) {
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

    protected void updateUserLimitRecord(Map<String, Integer> orderBuyNumMap) {
        UserLimitBaseBo baseBo = null;
        LimitInfoEntity limitInfoEntity = null;
        for (Map.Entry<String, Integer> entry : orderBuyNumMap.entrySet()) {
            int lastIndex = entry.getKey().lastIndexOf("_") + 1;
            String entryKey = entry.getKey();
            String entryKeyPrefix = entryKey.substring(0, entryKey.indexOf("_", entryKey.indexOf("_") + 1) + 1);

            switch (entryKeyPrefix) {
                //保存商品限购记录
                case LIMIT_PREFIX_GOODS:
                    long goodsId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getGoodsIdLimitMap().get(goodsId);
                    limitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    //活动过期
                    if (limitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.INVALID_LIMITATION_ACTIVITY);
                    }
                    LimitContext.getLimitBo().getGoodsLimitEntityList().add(LimitConvertor.convertGoodsLimit(baseBo, goodsId, entry.getValue(), limitInfoEntity));
                    break;
                //保存活动限购记录,多门店的时候是否会出现问题？
                case LIMIT_PREFIX_ACTIVITY:
                    long activityId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getActivityIdLimitMap().get(activityId);
                    limitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    if (limitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.INVALID_LIMITATION_ACTIVITY);
                    }
                    LimitContext.getLimitBo().getActivityLimitEntityList().add(LimitConvertor.convertActivityLimit(baseBo, activityId, entry.getValue(), limitInfoEntity));
                    break;
                //更新sku的售卖数量
                case LIMIT_PREFIX_SKU:
                    long skuId = Long.parseLong(entry.getKey().substring(lastIndex));
                    baseBo = LimitContext.getLimitBo().getSkuIdLimitMap().get(skuId);
                    limitInfoEntity = limitInfoDao.getLimitInfo(new LimitParam(baseBo.getPid(), baseBo.getBizId(), baseBo.getBizType()));
                    if (limitInfoEntity == null) {
                        throw new LimitationBizException(LimitationErrorCode.INVALID_LIMITATION_ACTIVITY);
                    }
                    LimitContext.getLimitBo().getActivityGoodsSoldEntityList().add(LimitConvertor.convertActivitySoldEntity(baseBo, skuId, entry.getValue(), limitInfoEntity));
                    break;
                default:
                    break;
            }
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

    public void reverseSaveOrDeductUserLimit(List<LimitOrderChangeLogEntity> logList,
                                             Map<String, UserLimitEntity> activityMap,
                                             Map<String, UserGoodsLimitEntity> goodsLimitMap,
                                             Map<String, SkuLimitInfoEntity> skuLimitMap) {

        for (LimitOrderChangeLogEntity logEntity : logList) {
            if (Objects.equals(logEntity.getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())
                    || Objects.equals(logEntity.getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {
                buildActivityBuyInfoLogEntity(activityMap, logEntity);
                buildGoodsBuyInfoLogEntity(goodsLimitMap, logEntity);
                buildSkuBuyInfoLogEntity(skuLimitMap, logEntity);
            } else if (Objects.equals(logEntity.getBizType(), LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())) {
                buildGoodsBuyInfoLogEntity(goodsLimitMap, logEntity);
                buildSkuBuyInfoLogEntity(skuLimitMap, logEntity);
            } else if (Objects.equals(logEntity.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
                buildCombinationBuyLogEntity(activityMap, skuLimitMap, logEntity);
            }
        }
    }

    private void buildCombinationBuyLogEntity(Map<String, UserLimitEntity> activityMap, Map<String, SkuLimitInfoEntity> skuLimitMap, LimitOrderChangeLogEntity logEntity) {
        buildActivityBuyInfoLogEntity(activityMap, logEntity);
        String prefixKey = LIMIT_PREFIX_SKU + logEntity.getBizType();
        if (skuLimitMap.get(prefixKey + logEntity.getBizId()) == null) {
            SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
            skuLimitInfoEntity.setPid(logEntity.getPid());
            skuLimitInfoEntity.setStoreId(logEntity.getStoreId());
            skuLimitInfoEntity.setLimitId(logEntity.getLimitId());
            skuLimitInfoEntity.setGoodsId(logEntity.getBizId());
            skuLimitInfoEntity.setSkuId(logEntity.getBizId());
            skuLimitInfoEntity.setSoldNum(logEntity.getBuyNum());
            skuLimitMap.put(prefixKey + logEntity.getBizId(), skuLimitInfoEntity);
        } else {
            skuLimitMap.get(prefixKey + logEntity.getBizId()).setSoldNum(skuLimitMap.get(prefixKey + logEntity.getBizId()).getSoldNum() + logEntity.getBuyNum());
        }
    }

    private void buildActivityBuyInfoLogEntity(Map<String, UserLimitEntity> activityMap, LimitOrderChangeLogEntity logEntity) {
        String prefixKey = LIMIT_PREFIX_ACTIVITY + logEntity.getBizType();
        if (activityMap.get(prefixKey + logEntity.getBizId()) == null) {
            UserLimitEntity userLimitEntity = new UserLimitEntity();
            userLimitEntity.setPid(logEntity.getPid());
            userLimitEntity.setStoreId(logEntity.getStoreId());
            userLimitEntity.setLimitId(logEntity.getLimitId());
            userLimitEntity.setWid(logEntity.getWid());
            userLimitEntity.setBuyNum(logEntity.getBuyNum());
            activityMap.put(prefixKey + logEntity.getBizId(), userLimitEntity);
        } else {
            activityMap.get(prefixKey + logEntity.getBizId()).setBuyNum(activityMap.get(prefixKey + logEntity.getBizId()).getBuyNum() + logEntity.getBuyNum());
        }
    }

    private void buildGoodsBuyInfoLogEntity(Map<String, UserGoodsLimitEntity> goodsLimitMap, LimitOrderChangeLogEntity logEntity) {
        String prefixKey = LIMIT_PREFIX_GOODS + logEntity.getBizType();
        if (goodsLimitMap.get(prefixKey + logEntity.getGoodsId()) == null) {
            UserGoodsLimitEntity userGoodsLimitEntity = new UserGoodsLimitEntity();
            userGoodsLimitEntity.setPid(logEntity.getPid());
            userGoodsLimitEntity.setStoreId(logEntity.getStoreId());
            userGoodsLimitEntity.setLimitId(logEntity.getLimitId());
            userGoodsLimitEntity.setGoodsId(logEntity.getGoodsId());
            userGoodsLimitEntity.setWid(logEntity.getWid());
            userGoodsLimitEntity.setBuyNum(logEntity.getBuyNum());
            goodsLimitMap.put(prefixKey + logEntity.getGoodsId(), userGoodsLimitEntity);
        } else {
            goodsLimitMap.get(prefixKey + logEntity.getGoodsId()).setBuyNum(goodsLimitMap.get(prefixKey + logEntity.getGoodsId()).getBuyNum() + logEntity.getBuyNum());
        }
    }

    private void buildSkuBuyInfoLogEntity(Map<String, SkuLimitInfoEntity> skuLimitMap, LimitOrderChangeLogEntity logEntity) {
        String prefixKey = LIMIT_PREFIX_SKU + logEntity.getBizType();
        if (logEntity.getSkuId() != null) {
            if (skuLimitMap.get(prefixKey + logEntity.getSkuId()) == null) {
                SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
                skuLimitInfoEntity.setPid(logEntity.getPid());
                skuLimitInfoEntity.setStoreId(logEntity.getStoreId());
                skuLimitInfoEntity.setLimitId(logEntity.getLimitId());
                skuLimitInfoEntity.setGoodsId(logEntity.getGoodsId());
                skuLimitInfoEntity.setSoldNum(logEntity.getBuyNum());
                skuLimitInfoEntity.setSkuId(logEntity.getSkuId());
                skuLimitMap.put(prefixKey + logEntity.getSkuId(), skuLimitInfoEntity);
            } else {
                skuLimitMap.get(prefixKey + logEntity.getSkuId()).setSoldNum(skuLimitMap.get(prefixKey + logEntity.getSkuId()).getSoldNum() + logEntity.getBuyNum());
            }
        }
    }

    protected void checkCreateOrDeductOrderParams(List<UpdateUserLimitVo> vos) {
        for (UpdateUserLimitVo limitVo : vos) {
            VerifyParamUtils.checkParam(LimitationErrorCode.PID_IS_NULL, limitVo.getPid());
            VerifyParamUtils.checkParam(LimitationErrorCode.STORE_IS_NULL, limitVo.getStoreId());
            if (!Objects.equals(ActivityTypeEnum.COMBINATION_BUY.getType(), limitVo.getBizType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.GOODSID_IS_NULL, limitVo.getGoodsId());
            }
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZTYPE_IS_NULL, limitVo.getBizType());
            VerifyParamUtils.checkParam(LimitationErrorCode.BIZID_IS_NULL, limitVo.getBizId());
            VerifyParamUtils.checkParam(LimitationErrorCode.GOODSNUM_IS_NULL, limitVo.getGoodsNum());
            if (limitVo.getGoodsNum() < 1) {
                throw new LimitationBizException(LimitationErrorCode.GOODSNUM_IS_ILLEGAL);
            }
            VerifyParamUtils.checkParam(LimitationErrorCode.ORDERNO_IS_NULL, limitVo.getOrderNo());
            VerifyParamUtils.checkParam(LimitationErrorCode.WID_IS_NULL, limitVo.getWid());
            if (Objects.equals(limitVo.getBizType(), ActivityTypeEnum.DISCOUNT.getType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.ACTIVITY_STOCK_TYPE_IS_NULL, limitVo.getActivityStockType());
            }
            if (Objects.equals(ActivityTypeEnum.PRIVILEGE_PRICE.getType(), limitVo.getBizType())
                    || (Objects.equals(ActivityTypeEnum.DISCOUNT.getType(), limitVo.getBizType())
                    && Objects.equals(limitVo.getActivityStockType(), LimitConstant.DISCOUNT_TYPE_SKU))
                    || Objects.equals(LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel(), limitVo.getBizType())) {
                VerifyParamUtils.checkParam(LimitationErrorCode.SKUINFO_IS_NULL, limitVo.getSkuId());
            }
        }
    }

    protected LimitServiceNameEnum getServiceName() {
        return null;
    }
}
