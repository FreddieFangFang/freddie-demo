package com.weimob.saas.ec.limitation.service.impl;

import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.*;
import com.weimob.saas.ec.limitation.entity.*;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.*;
import com.weimob.saas.ec.limitation.service.LimitationQueryBizService;
import com.weimob.saas.ec.limitation.utils.MapKeyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lujialin
 * @description 查询商品限购信息
 * @date 2018/6/4 11:30
 */
@Service(value = "limitationQueryBizService")
public class LimitationQueryBizServiceImpl implements LimitationQueryBizService {

    @Autowired
    private LimitInfoDao limitInfoDao;
    @Autowired
    private GoodsLimitInfoDao goodsLimitInfoDao;
    @Autowired
    private UserGoodsLimitDao userGoodsLimitDao;
    @Autowired
    private UserLimitDao userLimitDao;
    @Autowired
    private SkuLimitInfoDao skuLimitInfoDao;


    @Override
    public GoodsLimitInfoListResponseVo queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo) {
        //构建查询主表limitId的入参
        List<LimitParam> queryLimitInfoList = new ArrayList<>();
        //查询商品限购信息的入参
        List<GoodsLimitInfoEntity> queryGoodsLimitList = new ArrayList<>();
        //查询用户购买商品记录入参
        List<UserGoodsLimitEntity> queryUserGoodsLimitList = new ArrayList<>();
        //查询用户活动购买记录入参
        List<UserLimitEntity> queryUserLimitList = new ArrayList<>();
        //查询用户sku购买记录入参
        List<SkuLimitInfoEntity> querySkuLimitList = new ArrayList<>();
        //limitId的Map
        Map<String, Long> limitIdMap = new HashMap<>();
        //活动限购map
        Map<String, Integer> activityLimitNumMap = new HashMap<>();
        //用户活动购买记录map
        Map<String, Integer> activityUserLimitNumMap = new HashMap<>();
        //商品限购map
        Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap = new HashMap<>();
        //用户商品购买记录
        Map<String, Integer> userGoodsLimitNumMap = new HashMap<>();
        //用户店铺下购买记录
        Map<String, Integer> userPidGoodsLimitNumMap = new HashMap<>();
        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            LimitParam limitParam = new LimitParam();
            limitParam.setBizType(vo.getBizType());
            limitParam.setBizId(vo.getBizId());
            limitParam.setPid(vo.getPid());
            queryLimitInfoList.add(limitParam);
        }
        //查询限购主表
        List<LimitInfoEntity> limitInfoEntityList = limitInfoDao.queryLimitInfoList(queryLimitInfoList);
        if (CollectionUtils.isEmpty(limitInfoEntityList)) {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_GOODS_IS_NULL);
        }
        for (LimitInfoEntity entity : limitInfoEntityList) {
            String limitIdMapKey = MapKeyUtil.buildLimitIdMapKey(entity.getPid(), entity.getBizType(), entity.getBizId());
            limitIdMap.put(limitIdMapKey, entity.getLimitId());
            activityLimitNumMap.put(limitIdMapKey, entity.getLimitNum());
        }
        //构建查询数据库入参
        buildQueryEntity(requestVo, limitIdMap, queryGoodsLimitList, queryUserGoodsLimitList, queryUserLimitList, querySkuLimitList);

        //查询商品限购信息
        List<GoodsLimitInfoEntity> goodsLimitInfoList = goodsLimitInfoDao.queryGoodsLimitInfoList(queryGoodsLimitList);
        for (GoodsLimitInfoEntity entity : goodsLimitInfoList) {
            String goodsLimitNumKey = MapKeyUtil.buildGoodsLimitNumMap(entity.getPid(), entity.getStoreId(), entity.getLimitId(), entity.getGoodsId());
            if (CollectionUtils.isEmpty(goodsLimitNumMap.get(goodsLimitNumKey))) {
                List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = new ArrayList<>();
                goodsLimitInfoEntityList.add(entity);
                goodsLimitNumMap.put(goodsLimitNumKey, goodsLimitInfoEntityList);
            } else {
                goodsLimitNumMap.get(goodsLimitNumKey).add(entity);
            }
        }
        //查询用户商品下单记录
        List<UserGoodsLimitEntity> userGoodsLimitList = userGoodsLimitDao.queryUserGoodsLimitList(queryUserGoodsLimitList);
        for (UserGoodsLimitEntity entity : userGoodsLimitList) {
            String userPidGoodsLimitKey = MapKeyUtil.buildPidGoodsLimitNumMap(entity.getPid(), entity.getLimitId(), entity.getGoodsId());
            userGoodsLimitNumMap.put(MapKeyUtil.buildGoodsLimitNumMap(entity.getPid(), entity.getStoreId(), entity.getLimitId(), entity.getGoodsId()), entity.getBuyNum());
            if (userPidGoodsLimitNumMap.get(userPidGoodsLimitKey) == null) {
                userPidGoodsLimitNumMap.put(userPidGoodsLimitKey, entity.getBuyNum());
            } else {
                Integer alreadyNum = userPidGoodsLimitNumMap.get(userPidGoodsLimitKey);
                userPidGoodsLimitNumMap.put(userPidGoodsLimitKey, alreadyNum + entity.getBuyNum());
            }
        }
        //根据限购类型进行限购校验
        if (Objects.equals(requestVo.getGoodsDetailList().get(0).getBizType(), ActivityTypeEnum.DISCOUNT.getType())
                && Objects.equals(requestVo.getGoodsDetailList().get(0).getActivityStockType(), LimitConstant.DISCOUNT_TYPE_STOCK)) {
            //限时折扣要校验活动限购
            List<UserLimitEntity> userLimitEntityList = userLimitDao.queryUserLimitEntityList(queryUserLimitList);
            for (UserLimitEntity vo : userLimitEntityList) {
                activityUserLimitNumMap.put(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()), vo.getBuyNum());
            }

            return buildResponseVo(requestVo, limitIdMap, activityLimitNumMap, activityUserLimitNumMap, goodsLimitNumMap, userGoodsLimitNumMap, userPidGoodsLimitNumMap);

        } else if (Objects.equals(requestVo.getGoodsDetailList().get(0).getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())
                || (Objects.equals(requestVo.getGoodsDetailList().get(0).getBizType(), ActivityTypeEnum.DISCOUNT.getType())
                && Objects.equals(requestVo.getGoodsDetailList().get(0).getActivityStockType(), LimitConstant.DISCOUNT_TYPE_SKU))) {
            //特权价要校验活动限购和sku可售数量
            List<SkuLimitInfoEntity> skuLimitList = skuLimitInfoDao.querySkuLimitList(querySkuLimitList);
            List<UserLimitEntity> userLimitEntityList = userLimitDao.queryUserLimitEntityList(queryUserLimitList);
            for (UserLimitEntity vo : userLimitEntityList) {
                activityUserLimitNumMap.put(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()), vo.getBuyNum());
            }
            GoodsLimitInfoListResponseVo responseVo = buildResponseVo(requestVo, limitIdMap, activityLimitNumMap, activityUserLimitNumMap, goodsLimitNumMap, userGoodsLimitNumMap, userPidGoodsLimitNumMap);
            //处理sku的限购
            volidSkuLimit(requestVo, limitIdMap, skuLimitList, responseVo);
            return responseVo;
        }

        return buildGoodsLimitInfoListResponseVo(requestVo, limitIdMap, goodsLimitNumMap, userGoodsLimitNumMap);
    }

    private void volidSkuLimit(GoodsLimitInfoListRequestVo requestVo, Map<String, Long> limitIdMap, List<SkuLimitInfoEntity> skuLimitList, GoodsLimitInfoListResponseVo responseVo) {
        Map<String, SkuLimitInfoEntity> skuLimitMap = new HashMap<>();
        for (SkuLimitInfoEntity entity : skuLimitList) {
            skuLimitMap.put(MapKeyUtil.buildSkuLimitMapKey(entity.getPid(), entity.getStoreId(), entity.getLimitId(), entity.getGoodsId(), entity.getSkuId()), entity);
        }
        for (GoodsLimitInfoListVo vo : responseVo.getGoodsLimitInfoList()) {
            vo.setLimitStatus(true);
            Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            if (skuLimitMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId(), vo.getSkuId())) == null) {
                throw new LimitationBizException(LimitationErrorCode.LIMIT_SKU_IS_NULL);
            }
            Integer alreadyBuyNum = skuLimitMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId(), vo.getSkuId())).getSoldNum();
            vo.setAlreadyBuyNum(alreadyBuyNum == null ? 0 : alreadyBuyNum);
            Integer skuLimitNum = skuLimitMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId(), vo.getSkuId())).getLimitNum();
            Integer canBuyNum = skuLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
            vo.setCanBuyNum(vo.getCanBuyNum() > canBuyNum ? canBuyNum : vo.getCanBuyNum());
        }
        if (requestVo.getGoodsDetailList().get(0).getCheckLimit()) {
            //结算调用，抛异常
            Map<String, Integer> activityBuyNumMap = new HashMap<>();
            for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
                Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
                if (activityBuyNumMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId(), vo.getSkuId())) == null) {
                    activityBuyNumMap.put(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId(), vo.getSkuId()), vo.getGoodsBuyNum());
                } else {
                    activityBuyNumMap.put(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId(), vo.getSkuId()), activityBuyNumMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId(), vo.getSkuId())) + vo.getGoodsBuyNum());
                }
            }
            Iterator<Map.Entry<String, Integer>> activityBuyNumIterator = activityBuyNumMap.entrySet().iterator();
            while (activityBuyNumIterator.hasNext()) {
                Map.Entry<String, Integer> entry = activityBuyNumIterator.next();
                Integer alreadyBuyNum = skuLimitMap.get(entry.getKey()).getSoldNum();
                Integer skuLimitNum = skuLimitMap.get(entry.getKey()).getLimitNum();
                int alreadyBuyNumValue = alreadyBuyNum == null ? 0 : alreadyBuyNum;
                if (alreadyBuyNumValue + entry.getValue() > skuLimitNum) {
                    throw new LimitationBizException(LimitationErrorCode.BEYOND_SKU_LIMIT_NUM);
                }
            }
        }
    }

    private GoodsLimitInfoListResponseVo buildResponseVo(GoodsLimitInfoListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, Integer> activityLimitNumMap,
                                                         Map<String, Integer> activityUserLimitNumMap, Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap,
                                                         Map<String, Integer> userGoodsLimitNumMap, Map<String, Integer> userPidGoodsLimitNumMap) {
        GoodsLimitInfoListResponseVo responseVo = new GoodsLimitInfoListResponseVo();
        List<GoodsLimitInfoListVo> goodsLimitInfoList = new ArrayList<>();
        validActivityLimit(requestVo, limitIdMap, activityLimitNumMap, activityUserLimitNumMap, goodsLimitInfoList);
        validGoodsLimit(requestVo, limitIdMap, goodsLimitNumMap, userGoodsLimitNumMap, goodsLimitInfoList, userPidGoodsLimitNumMap);
        responseVo.setGoodsLimitInfoList(goodsLimitInfoList);
        return responseVo;
    }

    private void validGoodsLimit(GoodsLimitInfoListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap,
                                 Map<String, Integer> userGoodsLimitNumMap, List<GoodsLimitInfoListVo> goodsLimitInfoList, Map<String, Integer> userPidGoodsLimitNumMap) {
        for (GoodsLimitInfoListVo vo : goodsLimitInfoList) {
            Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            Integer alreadyBuyNum = userGoodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            Integer pidAlreadyBuyNum = userPidGoodsLimitNumMap.get(MapKeyUtil.buildPidGoodsLimitNumMap(vo.getPid(), limitId, vo.getGoodsId()));
            List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            if (CollectionUtils.isEmpty(goodsLimitInfoEntityList)) {
                continue;
            }
            Integer goodsLimitNum = null;
            Integer pidGoodsLimitNum = null;
            for (GoodsLimitInfoEntity goodsLimitInfoEntity : goodsLimitInfoEntityList) {
                if (goodsLimitInfoEntity.getLimitLevel() == 0) {
                    goodsLimitNum = goodsLimitInfoEntity.getLimitNum();
                } else {
                    pidGoodsLimitNum = goodsLimitInfoEntity.getLimitNum();
                }
            }
            vo.setAlreadyBuyNum(alreadyBuyNum == null ? 0 : alreadyBuyNum);
            if (LimitConstant.UNLIMITED_NUM == goodsLimitNum) {
                if (LimitConstant.UNLIMITED_NUM == pidGoodsLimitNum) {

                } else {
                    vo.setLimitStatus(true);
                    Integer canBuyNum = pidGoodsLimitNum - (pidAlreadyBuyNum == null ? 0 : pidAlreadyBuyNum);
                    vo.setCanBuyNum(vo.getCanBuyNum() < canBuyNum ? vo.getCanBuyNum() : canBuyNum);
                }
            } else {
                if (LimitConstant.UNLIMITED_NUM == pidGoodsLimitNum) {
                    vo.setLimitStatus(true);
                    Integer canBuyNum = goodsLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                    vo.setCanBuyNum(vo.getCanBuyNum() < canBuyNum ? vo.getCanBuyNum() : canBuyNum);
                } else {
                    vo.setLimitStatus(true);
                    Integer storeCanBuyNum = goodsLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                    Integer pidCanBuyNum = pidGoodsLimitNum - (pidAlreadyBuyNum == null ? 0 : pidAlreadyBuyNum);
                    Integer canBuyNum = storeCanBuyNum < pidCanBuyNum ? storeCanBuyNum : pidCanBuyNum;
                    vo.setCanBuyNum(vo.getCanBuyNum() < canBuyNum ? vo.getCanBuyNum() : canBuyNum);
                }
            }
        }
        if (requestVo.getGoodsDetailList().get(0).getCheckLimit()) {
            //结算调用，抛异常
            Map<String, Integer> activityBuyNumMap = new HashMap<>();
            for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
                Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
                if (activityBuyNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId())) == null) {
                    activityBuyNumMap.put(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()), vo.getGoodsBuyNum());
                } else {
                    activityBuyNumMap.put(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()), activityBuyNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId())) + vo.getGoodsBuyNum());
                }
            }
            for (GoodsLimitInfoListVo vo : goodsLimitInfoList) {
                Integer canBuyNum = vo.getCanBuyNum();
                Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
                Integer orderBuyNum = activityBuyNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
                if (canBuyNum < orderBuyNum) {
                    throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
                }
            }
        }
    }

    private void validActivityLimit(GoodsLimitInfoListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, Integer> activityLimitNumMap, Map<String, Integer> activityUserLimitNumMap, List<GoodsLimitInfoListVo> goodsLimitInfoList) {
        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            GoodsLimitInfoListVo goodsLimitInfoListVo = new GoodsLimitInfoListVo();
            goodsLimitInfoListVo.setPid(vo.getPid());
            goodsLimitInfoListVo.setStoreId(vo.getStoreId());
            goodsLimitInfoListVo.setWid(vo.getWid());
            goodsLimitInfoListVo.setBizType(vo.getBizType());
            goodsLimitInfoListVo.setBizId(vo.getBizId());
            goodsLimitInfoListVo.setGoodsId(vo.getGoodsId());
            goodsLimitInfoListVo.setSkuId(vo.getSkuId());
            String limitIdKey = MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId());
            Integer alreadyBuyNum = activityUserLimitNumMap.get(limitIdKey);
            goodsLimitInfoListVo.setAlreadyBuyNum(alreadyBuyNum == null ? 0 : alreadyBuyNum);
            Integer activityLimitNum = activityLimitNumMap.get(limitIdKey);
            if (activityLimitNum == null) {
                throw new LimitationBizException(LimitationErrorCode.LIMIT_ACTIVITY_IS_NULL);
            }
            if (activityLimitNum == LimitConstant.UNLIMITED_NUM) {
                goodsLimitInfoListVo.setLimitStatus(false);
            } else {
                goodsLimitInfoListVo.setLimitStatus(true);
                goodsLimitInfoListVo.setCanBuyNum(activityLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum));
            }
            goodsLimitInfoList.add(goodsLimitInfoListVo);
        }
        if (requestVo.getGoodsDetailList().get(0).getCheckLimit()) {
            //结算调用，抛异常
            Map<String, Integer> activityBuyNumMap = new HashMap<>();
            for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
                String limitIdKey = MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId());
                if (activityBuyNumMap.get(limitIdKey) == null) {
                    activityBuyNumMap.put(limitIdKey, vo.getGoodsBuyNum());
                } else {
                    activityBuyNumMap.put(limitIdKey, activityBuyNumMap.get(limitIdKey) + vo.getGoodsBuyNum());
                }
            }
            Iterator<Map.Entry<String, Integer>> activityBuyNumIterator = activityBuyNumMap.entrySet().iterator();
            while (activityBuyNumIterator.hasNext()) {
                Map.Entry<String, Integer> entry = activityBuyNumIterator.next();
                Integer alreadyBuyNum = activityUserLimitNumMap.get(entry.getKey());
                Integer activityLimitNum = activityLimitNumMap.get(entry.getKey());
                int alreadyBuyNumValue = alreadyBuyNum == null ? 0 : alreadyBuyNum;
                if (alreadyBuyNumValue + entry.getValue() > activityLimitNum) {
                    throw new LimitationBizException(LimitationErrorCode.BEYOND_ACTIVITY_LIMIT_NUM);
                }
            }
        }
    }

    private void buildQueryEntity(GoodsLimitInfoListRequestVo requestVo, Map<String, Long> limitIdMap,
                                  List<GoodsLimitInfoEntity> queryGoodsLimitList,
                                  List<UserGoodsLimitEntity> queryUserGoodsLimitList,
                                  List<UserLimitEntity> queryUserLimitList, List<SkuLimitInfoEntity> querySkuLimitList) {
        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            GoodsLimitInfoEntity goodsLimitInfoEntity = new GoodsLimitInfoEntity();
            goodsLimitInfoEntity.setPid(vo.getPid());
            goodsLimitInfoEntity.setStoreId(vo.getStoreId());
            goodsLimitInfoEntity.setGoodsId(vo.getGoodsId());
            goodsLimitInfoEntity.setLimitId(limitId);
            queryGoodsLimitList.add(goodsLimitInfoEntity);

            UserGoodsLimitEntity userGoodsLimitEntity = new UserGoodsLimitEntity();
            userGoodsLimitEntity.setPid(vo.getPid());
            userGoodsLimitEntity.setStoreId(vo.getStoreId());
            userGoodsLimitEntity.setWid(vo.getWid());
            userGoodsLimitEntity.setGoodsId(vo.getGoodsId());
            userGoodsLimitEntity.setLimitId(limitId);
            queryUserGoodsLimitList.add(userGoodsLimitEntity);

            UserLimitEntity userLimitEntity = new UserLimitEntity();
            userLimitEntity.setLimitId(limitId);
            userLimitEntity.setPid(vo.getPid());
            userLimitEntity.setStoreId(vo.getStoreId());
            userLimitEntity.setBizType(vo.getBizType());
            userLimitEntity.setBizId(vo.getBizId());
            userLimitEntity.setWid(vo.getWid());
            queryUserLimitList.add(userLimitEntity);

            SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
            skuLimitInfoEntity.setLimitId(limitId);
            skuLimitInfoEntity.setPid(vo.getPid());
            skuLimitInfoEntity.setStoreId(vo.getStoreId());
            skuLimitInfoEntity.setGoodsId(vo.getGoodsId());
            skuLimitInfoEntity.setSkuId(vo.getSkuId());
            querySkuLimitList.add(skuLimitInfoEntity);
        }
    }

    @Override
    public QueryGoodsLimitNumListResponseVo queryGoodsLimitNumList(QueryGoodsLimitNumRequestVo requestVo) {
        QueryGoodsLimitNumListResponseVo responseVo = new QueryGoodsLimitNumListResponseVo();
        List<QueryGoodsLimitNumVo> queryGoodsLimitNumList = new ArrayList<>();
        List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitInfoDao.queryGoodsLimitNumList(requestVo.getQueryGoodslimitNumVoList());
        if (CollectionUtils.isEmpty(goodsLimitInfoEntityList)) {
            return responseVo;
        }
        //如果是特权价或者限时折扣可售数量的，需要查询sku的限购数量
        Map<String, List<SkuLimitInfoEntity>> skuLimitNumMap = null;
        if (Objects.equals(requestVo.getQueryGoodslimitNumVoList().get(0).getBizType(), ActivityTypeEnum.PRIVILEGE_PRICE.getType())
                || (Objects.equals(requestVo.getQueryGoodslimitNumVoList().get(0).getBizType(), ActivityTypeEnum.DISCOUNT.getType())
                && Objects.equals(requestVo.getQueryGoodslimitNumVoList().get(0).getActivityStockType(), LimitConstant.DISCOUNT_TYPE_SKU))) {
            List<SkuLimitInfoEntity> skuLimitInfoEntityList = skuLimitInfoDao.queryGoodsSkuLimitList(requestVo.getQueryGoodslimitNumVoList());
            skuLimitNumMap = buildSkuLimitMap(skuLimitInfoEntityList);
        }
        Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap = buildGoodsLimitMap(goodsLimitInfoEntityList);
        for (QueryGoodsLimitNumListVo request : requestVo.getQueryGoodslimitNumVoList()) {
            QueryGoodsLimitNumVo queryGoodsLimitNumVo = new QueryGoodsLimitNumVo();
            queryGoodsLimitNumVo.setPid(request.getPid());
            queryGoodsLimitNumVo.setGoodsId(request.getGoodsId());
            queryGoodsLimitNumVo.setStoreId(request.getStoreId());
            queryGoodsLimitNumVo.setBizId(request.getBizId());
            queryGoodsLimitNumVo.setBizType(request.getBizType());
            List<GoodsLimitInfoEntity> entityList = goodsLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(request.getPid(), request.getStoreId(), request.getGoodsId()));
            if (CollectionUtils.isEmpty(entityList)) {
                continue;
            }
            for (GoodsLimitInfoEntity entity : entityList) {
                if (entity.getLimitLevel() != null && entity.getLimitLevel() == 1) {
                    queryGoodsLimitNumVo.setPidGoodsLimitNum(entity.getLimitNum());
                } else {
                    queryGoodsLimitNumVo.setGoodsLimitNum(entity.getLimitNum());
                }
            }
            //如果是特权价或者限时折扣可售数量的，需要查询sku的限购数量
            List<SkuLimitInfo> skuLimitInfoList = new ArrayList<>();
            if (MapUtils.isNotEmpty(skuLimitNumMap)) {
                List<SkuLimitInfoEntity> skuLimitInfoEntityList = skuLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(request.getPid(), request.getStoreId(), request.getGoodsId()));
                if (CollectionUtils.isNotEmpty(skuLimitInfoEntityList)) {
                    for (SkuLimitInfoEntity entity : skuLimitInfoEntityList) {
                        SkuLimitInfo skuLimitInfo = new SkuLimitInfo();
                        skuLimitInfo.setSkuId(entity.getSkuId());
                        skuLimitInfo.setSkuLimitNum(entity.getLimitNum());
                        skuLimitInfo.setAlreadySoldNum(entity.getSoldNum());
                        skuLimitInfo.setSkuLimitType(entity.getLimitType());
                        skuLimitInfoList.add(skuLimitInfo);
                    }
                }
            }
            queryGoodsLimitNumVo.setSkuLimitInfoList(skuLimitInfoList);
            queryGoodsLimitNumList.add(queryGoodsLimitNumVo);
        }
        responseVo.setQueryGoodsLimitNumList(queryGoodsLimitNumList);
        return responseVo;
    }

    private Map<String, List<SkuLimitInfoEntity>> buildSkuLimitMap(List<SkuLimitInfoEntity> skuLimitInfoEntityList) {
        Map<String, List<SkuLimitInfoEntity>> skuLimitNumMap = new HashMap<>();
        for (SkuLimitInfoEntity entity : skuLimitInfoEntityList) {
            if (CollectionUtils.isEmpty(skuLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getStoreId(), entity.getGoodsId())))) {
                List<SkuLimitInfoEntity> entityList = new ArrayList<>();
                entityList.add(entity);
                skuLimitNumMap.put(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getStoreId(), entity.getGoodsId()), entityList);
            } else {
                skuLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getStoreId(), entity.getGoodsId())).add(entity);
            }
        }
        return skuLimitNumMap;
    }

    private Map<String, List<GoodsLimitInfoEntity>> buildGoodsLimitMap(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList) {
        Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap = new HashMap<>();
        for (GoodsLimitInfoEntity entity : goodsLimitInfoEntityList) {
            if (CollectionUtils.isEmpty(goodsLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getStoreId(), entity.getGoodsId())))) {
                List<GoodsLimitInfoEntity> entityList = new ArrayList<>();
                entityList.add(entity);
                goodsLimitNumMap.put(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getStoreId(), entity.getGoodsId()), entityList);
            } else {
                goodsLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getStoreId(), entity.getGoodsId())).add(entity);
            }
        }
        return goodsLimitNumMap;
    }

    @Override
    public QueryActivityLimitInfoResponseVo queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo) {
        QueryActivityLimitInfoResponseVo responseVo = new QueryActivityLimitInfoResponseVo();
        LimitInfoEntity infoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
        if (infoEntity == null) {
            return responseVo;
        }
        responseVo.setPid(requestVo.getPid());
        responseVo.setStoreId(requestVo.getStoreId());
        responseVo.setBizId(requestVo.getBizId());
        responseVo.setBizType(requestVo.getBizType());
        responseVo.setActivityLimitNum(infoEntity.getLimitNum());
        return responseVo;
    }

    private GoodsLimitInfoListResponseVo buildGoodsLimitInfoListResponseVo(GoodsLimitInfoListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap, Map<String, Integer> userGoodsLimitNumMap) {
        GoodsLimitInfoListResponseVo responseVo = new GoodsLimitInfoListResponseVo();
        List<GoodsLimitInfoListVo> goodsLimitInfoList = new ArrayList<>();
        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            GoodsLimitInfoListVo goodsLimitInfoListVo = new GoodsLimitInfoListVo();
            goodsLimitInfoListVo.setPid(vo.getPid());
            goodsLimitInfoListVo.setStoreId(vo.getStoreId());
            goodsLimitInfoListVo.setBizType(vo.getBizType());
            goodsLimitInfoListVo.setBizId(vo.getBizId());
            goodsLimitInfoListVo.setGoodsId(vo.getGoodsId());
            Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            if (CollectionUtils.isEmpty(goodsLimitInfoEntityList)) {
                continue;
            }
            Integer goodsLimitNum = null;
            for (GoodsLimitInfoEntity goodsLimitInfoEntity : goodsLimitInfoEntityList) {
                if (goodsLimitInfoEntity.getLimitLevel() == 0) {
                    goodsLimitNum = goodsLimitInfoEntity.getLimitNum();
                }
            }
            if (goodsLimitNum == null) {
                continue;
            }
            if (goodsLimitNum == LimitConstant.UNLIMITED_NUM) {
                goodsLimitInfoListVo.setLimitStatus(false);
            } else {
                goodsLimitInfoListVo.setLimitStatus(true);
            }
            Integer userBuyNum = userGoodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            Integer canBuyNum = goodsLimitNum - (userBuyNum == null ? 0 : userBuyNum);
            //结算调用，且购买数量大于可以购买的数量，抛异常
            if (vo.getCheckLimit() && vo.getGoodsBuyNum() > canBuyNum) {
                throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
            }
            goodsLimitInfoListVo.setAlreadyBuyNum(userBuyNum == null ? 0 : userBuyNum);
            goodsLimitInfoListVo.setCanBuyNum(canBuyNum < 0 ? 0 : canBuyNum);
            goodsLimitInfoList.add(goodsLimitInfoListVo);
        }
        responseVo.setGoodsLimitInfoList(goodsLimitInfoList);
        return responseVo;
    }
}
