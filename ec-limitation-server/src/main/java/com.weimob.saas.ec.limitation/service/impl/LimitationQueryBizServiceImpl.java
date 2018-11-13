package com.weimob.saas.ec.limitation.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.weimob.saas.ec.activity.util.group.ListToMap;
import com.weimob.saas.ec.activity.util.group.TransformUtils;
import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.common.LimitBizTypeEnum;
import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.GoodsLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.SkuLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.dao.UserLimitDao;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.CommonLimitParam;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitDetailListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitDetailListVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitInfoListVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumListVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumRequestVo;
import com.weimob.saas.ec.limitation.model.request.SkuLimitInfo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitDetailListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitDetailVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitNumListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitNumVo;
import com.weimob.saas.ec.limitation.service.LimitationQueryBizService;
import com.weimob.saas.ec.limitation.thread.SkuQueryThread;
import com.weimob.saas.ec.limitation.utils.CommonBizUtil;
import com.weimob.saas.ec.limitation.utils.MapKeyUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;

/**
 * @author lujialin
 * @description 查询商品限购信息
 * @date 2018/6/4 11:30
 */
@Service(value = "limitationQueryBizService")
public class LimitationQueryBizServiceImpl implements LimitationQueryBizService {

    public static final Logger LOGGER = LoggerFactory.getLogger(LimitationQueryBizServiceImpl.class);

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

    @Autowired
    private ThreadPoolTaskExecutor threadExecutor;

    @Override
    public GoodsLimitInfoListResponseVo queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo) {

        QueryGoodsLimitInfoListVo first = requestVo.getGoodsDetailList().get(0);
        Long pid = first.getPid();
        Integer type = first.getBizType();
        Long wid = first.getWid();
        Integer activityStockType = first.getActivityStockType();

        //将入参商品分组, key: pid_bizType_bizId
        Map<String, List<QueryGoodsLimitInfoListVo>> requestLimitMap = this.groupRequestLimitToMap(requestVo);
        //将入参商品分组, key: pid_bizType_bizId, value: goodsSet
        Map<String, Set<Long>> requestLimitGoodsMap = this.groupRequestGoodsToMap(requestVo);
        //获取入参商品的限购主表信息
        List<LimitInfoEntity> limitInfoEntityList = this.getLimitInfo(pid, type, requestLimitMap);
        if (limitInfoEntityList.contains(null)) {
            LOGGER.error("查询限购主表空指针复现---》list.size为："+ limitInfoEntityList.size()+ "");
            for (LimitInfoEntity entity : limitInfoEntityList) {
                if (entity != null) {
                    LOGGER.error(entity.toString());
                }
            }
        }
        //构建限购主表信息map, key pid_bizType_bizId
        Map<String, LimitInfoEntity> limitInfoMap = this.buildLimitInfoMap(limitInfoEntityList);
        //查询用户sku限购信息
        List<SkuLimitInfoEntity> skuLimitList = new ArrayList<>();
        if (CommonBizUtil.isValidSkuLimit(type, activityStockType)) {
            List<SkuLimitInfoEntity> queryList = this.buildQueryEntity(requestVo, limitInfoMap);
            skuLimitList = this.getSkuLimitInfoList(queryList, pid);
            if (CollectionUtils.isNotEmpty(skuLimitList)) {
                if (skuLimitList.contains(null)) {
                    LOGGER.error("查询SKU表空指针复现---》list.size为："+ skuLimitList.size()+ "");
                    for (SkuLimitInfoEntity entity : skuLimitList) {
                        if (entity != null) {
                            LOGGER.error(entity.toString());
                        }
                    }
                }
            }
        }
        //获取用户活动限购数量map
        Map<String, Integer> activityUserLimitNumMap =
                this.getActivityUserLimitNumMap(pid, type, wid, activityStockType, limitInfoEntityList);
        //如果是优惠套装,返回结果
        if (CommonBizUtil.isValidCombination(type)) {
            return buildCombinationBuyResponseVo(requestVo, skuLimitList, limitInfoMap, activityUserLimitNumMap);
        }

        //用户店铺pid级别下购买记录map
        Map<String, Integer> userPidGoodsLimitNumMap = new HashMap<>();
        //用户商品购买限购map
        Map<String, Integer> userGoodsLimitNumMap = new HashMap<>();
        //用户商品购买记录信息
        List<UserGoodsLimitEntity> userGoodsLimitList = this.getUserGoodsLimitList(requestLimitGoodsMap, limitInfoMap, wid);
        //构建用户购买信息map
        this.buildGoodsLimitMap(userGoodsLimitList, userPidGoodsLimitNumMap, userGoodsLimitNumMap);


        //获取商品限购map
        Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap = this.getGoodsLimitMap(requestLimitGoodsMap, limitInfoMap, pid);

        //限时折扣冻结库存
        if (CommonBizUtil.isValidDiscountStock(type, activityStockType)) {
            //限时折扣要校验活动限购
            return this.buildResponseVo(requestVo,
                    activityUserLimitNumMap, goodsLimitNumMap, userGoodsLimitNumMap, userPidGoodsLimitNumMap, limitInfoMap);

        }
        //特权价 && 限时折扣sku
        else if (CommonBizUtil.isValidPrivilegePrice(type) || CommonBizUtil.isValidDiscountSku(type, activityStockType)) {
            //特权价要校验活动限购和sku可售数量
            GoodsLimitInfoListResponseVo responseVo = this.buildResponseVo(requestVo, activityUserLimitNumMap,
                            goodsLimitNumMap, userGoodsLimitNumMap, userPidGoodsLimitNumMap, limitInfoMap);
            //处理sku的限购
            this.validGoodsSkuLimit(requestVo, skuLimitList, responseVo, limitInfoMap);
            return responseVo;
        }
        //积分商城
        else if (CommonBizUtil.isValidPoint(type)) {
            GoodsLimitInfoListResponseVo responseVo = this.buildGoodsLimitInfoListResponseVo(requestVo, goodsLimitNumMap,
                    userGoodsLimitNumMap, limitInfoMap);
            //处理sku的限购
            this.validGoodsSkuLimit(requestVo, skuLimitList, responseVo, limitInfoMap);
            return responseVo;
        } else {
            return new GoodsLimitInfoListResponseVo();
        }
    }

    @Override
    public List<SkuLimitInfoEntity> getSkuLimitInfoList(List<SkuLimitInfoEntity> queryList, Long pid) {
        List<SkuLimitInfoEntity> skuLimitList = new ArrayList<>(queryList.size());

        // 每个线程查询5个
        int perNum = 50;
        int totalSize = queryList.size();
        // 线程数目
        int threadNum = totalSize / perNum + (totalSize % perNum == 0 ? 0 : 1);

        List<SkuQueryThread> taskList = new ArrayList<>(threadNum);


        for (int i = 0; i < threadNum; i++) {
            CommonLimitParam commonLimitParam = new CommonLimitParam();
            commonLimitParam.setPid(pid);
            if (i == threadNum - 1) {
                commonLimitParam.setSkuLimitInfoEntityList(queryList.subList(i * perNum, totalSize));
                taskList.add(new SkuQueryThread(commonLimitParam, skuLimitInfoDao));
            } else {
                commonLimitParam.setSkuLimitInfoEntityList(queryList.subList(i * perNum, (i + 1) * perNum));
                taskList.add(new SkuQueryThread(commonLimitParam, skuLimitInfoDao));
            }
        }

        List<Future<List<SkuLimitInfoEntity>>> taskResultList = new ArrayList<>();

        for (SkuQueryThread task : taskList) {
            Future<List<SkuLimitInfoEntity>> taskResult = threadExecutor.submit(task);
            taskResultList.add(taskResult);
        }
        // 得到执行结果
        if (SkuQueryThread.isAllDone(taskResultList, skuLimitList)) {
            taskResultList.clear();
        }
        return skuLimitList;
    }

    private Map<String, Integer> getActivityUserLimitNumMap(Long pid, Integer type, Long wid, Integer activityStockType, List<LimitInfoEntity> limitInfoEntityList) {
        //限购idList
        List<Long> limitIdList = Lists.transform(limitInfoEntityList, new Function<LimitInfoEntity, Long>() {
            @Override
            public Long apply(LimitInfoEntity limitInfoEntity) {
                return limitInfoEntity.getLimitId();
            }
        });

        CommonLimitParam userLimitParam = new CommonLimitParam();
        userLimitParam.setPid(pid);
        userLimitParam.setWid(wid);
        userLimitParam.setLimitIdList(limitIdList);

        //用户活动购买记录map
        Map<String, Integer> activityUserLimitNumMap =null;
        if (CommonBizUtil.isValidUserActivityLimit(type, activityStockType)) {
            activityUserLimitNumMap = this.queryUserActivityBuyRecord(userLimitParam);
        }
        return activityUserLimitNumMap;
    }

    private Map<String, LimitInfoEntity> buildLimitInfoMap(List<LimitInfoEntity> limitInfoEntityList) {
        return TransformUtils
                    .listToMap(limitInfoEntityList, new ListToMap<String, LimitInfoEntity, LimitInfoEntity>() {
                @Override
                public String key(LimitInfoEntity o) {
                    return MapKeyUtil.buildLimitIdMapKey(o.getPid(), o.getBizType(), o.getBizId());
                }

                @Override
                public LimitInfoEntity value(LimitInfoEntity o) {
                    return o;
                }
            });
    }

    private List<LimitInfoEntity> getLimitInfo(Long pid, Integer type, Map<String, List<QueryGoodsLimitInfoListVo>> requestLimitMap) {
        List<LimitParam> queryLimitInfoList = new ArrayList<>();
        LimitParam limitParam = null;
        String[] keys = null;
        for (String key : requestLimitMap.keySet()) {
            keys = MapKeyUtil.getLimitIdMapKeyArray(key);
            limitParam = new LimitParam();
            limitParam.setPid(pid);
            limitParam.setBizType(type);
            limitParam.setBizId(Long.valueOf(keys[MapKeyUtil.LIMIT_ID_BIZID_INDEX]));
            queryLimitInfoList.add(limitParam);

        }

        //查询限购主表
        List<LimitInfoEntity> limitInfoEntityList = limitInfoDao.listLimitInfoByBizId(queryLimitInfoList);
        if (CollectionUtils.isEmpty(limitInfoEntityList)) {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_GOODS_IS_NULL);
        }
        return limitInfoEntityList;
    }

    /**
     * @description 将入参分组
     * @author haojie.jin
     * @date 5:10 PM 2018/11/8
     **/

    private Map<String, List<QueryGoodsLimitInfoListVo>> groupRequestLimitToMap(GoodsLimitInfoListRequestVo requestVo) {
        return TransformUtils
                    .groupListToMapList(requestVo.getGoodsDetailList(),
                            new ListToMap<String, QueryGoodsLimitInfoListVo, QueryGoodsLimitInfoListVo>() {
                @Override
                public String key(QueryGoodsLimitInfoListVo o) {
                    return MapKeyUtil.buildLimitIdMapKey(o.getPid(), o.getBizType(), o.getBizId());
                }

                @Override
                public QueryGoodsLimitInfoListVo value(QueryGoodsLimitInfoListVo o) {
                    if (Objects.equals(o.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
                        o.setGoodsId(o.getBizId());
                        o.setSkuId(o.getBizId());
                    }
                    return o;
                }
            });
    }

    /**
     * @description 将入参分组
     * @author haojie.jin
     * @date 5:10 PM 2018/11/8
     **/

    private Map<String, Set<Long>> groupRequestGoodsToMap(GoodsLimitInfoListRequestVo requestVo) {

        return TransformUtils.groupListToMapSet(requestVo.getGoodsDetailList(),
                new ListToMap<String, Long, QueryGoodsLimitInfoListVo>() {
            @Override
            public String key(QueryGoodsLimitInfoListVo o) {
                return MapKeyUtil.buildLimitIdMapKey(o.getPid(), o.getBizType(), o.getBizId());
            }

            @Override
            public Long value(QueryGoodsLimitInfoListVo o) {
                return o.getGoodsId();
            }
        });
    }

    private void buildGoodsLimitMap(List<UserGoodsLimitEntity> userGoodsLimitList, Map<String, Integer> userPidGoodsLimitNumMap, Map<String, Integer> userGoodsLimitNumMap) {
        if (CollectionUtils.isNotEmpty(userGoodsLimitList)) {
            String userPidGoodsLimitKey = null;
            for (UserGoodsLimitEntity entity : userGoodsLimitList) {
                userPidGoodsLimitKey = MapKeyUtil.buildPidGoodsLimitNumMap(entity.getPid(), entity.getLimitId(), entity.getGoodsId());
                userGoodsLimitNumMap.put(MapKeyUtil.buildGoodsLimitNumMap(entity.getPid(), entity.getStoreId(), entity.getLimitId(), entity.getGoodsId()), entity.getBuyNum());
                if (userPidGoodsLimitNumMap.get(userPidGoodsLimitKey) == null) {
                    userPidGoodsLimitNumMap.put(userPidGoodsLimitKey, entity.getBuyNum());
                } else {
                    Integer alreadyNum = userPidGoodsLimitNumMap.get(userPidGoodsLimitKey);
                    userPidGoodsLimitNumMap.put(userPidGoodsLimitKey, alreadyNum + entity.getBuyNum());
                }
            }
        }
    }


    private Map<String, Integer> queryUserActivityBuyRecord(CommonLimitParam commonLimitParam) {

        Map<String, Integer> activityUserLimitNumMap = new HashMap<>();
        List<UserLimitEntity> userLimitEntityList = userLimitDao.listUserLimitByLimitIdList(commonLimitParam);
        if (CollectionUtils.isEmpty(userLimitEntityList)) {
            return activityUserLimitNumMap;
        }

        return TransformUtils.gourpListSumToMap(userLimitEntityList, new ListToMap<String, Integer, UserLimitEntity>() {
            @Override
            public String key(UserLimitEntity o) {
                return MapKeyUtil.buildLimitIdMapKey(o.getPid(), o.getBizType(), o.getBizId());
            }

            @Override
            public Integer value(UserLimitEntity o) {
                return o.getBuyNum();
            }
        });
    }

    private GoodsLimitInfoListResponseVo buildCombinationBuyResponseVo(
            GoodsLimitInfoListRequestVo requestVo,
            List<SkuLimitInfoEntity> skuLimitList,
            Map<String, LimitInfoEntity> limitInfoMap,
            Map<String, Integer> activityUserLimitNumMap
    ) {
        List<GoodsLimitInfoListVo> goodsLimitInfoList = new ArrayList<>();
        // 校验活动限购
        this.validActivityLimit(requestVo, activityUserLimitNumMap, goodsLimitInfoList, limitInfoMap);

        // 校验sku限购
        GoodsLimitInfoListResponseVo responseVo = new GoodsLimitInfoListResponseVo();
        responseVo.setGoodsLimitInfoList(goodsLimitInfoList);
        validGoodsSkuLimit(requestVo, skuLimitList, responseVo, limitInfoMap);
        return responseVo;
    }


    private void validGoodsSkuLimit(GoodsLimitInfoListRequestVo requestVo,
                                    List<SkuLimitInfoEntity> skuLimitList,
                                    GoodsLimitInfoListResponseVo responseVo,
                                    Map<String, LimitInfoEntity> limitInfoMap) {
        Map<String, SkuLimitInfoEntity> skuLimitMap = new HashMap<>();
        for (SkuLimitInfoEntity entity : skuLimitList) {
            skuLimitMap.put(MapKeyUtil.buildSkuLimitMapKey(entity.getPid(), entity.getLimitId(), entity.getGoodsId(), entity.getSkuId()), entity);
        }
        LimitInfoEntity limitInfoEntity = null;
        for (GoodsLimitInfoListVo vo : responseVo.getGoodsLimitInfoList()) {
            vo.setLimitStatus(true);

            limitInfoEntity = limitInfoMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            Long limitId = limitInfoEntity == null ? null : limitInfoEntity.getLimitId();

            if (skuLimitMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), limitId, vo.getGoodsId(), vo.getSkuId())) == null) {
                //throw new LimitationBizException(LimitationErrorCode.LIMIT_SKU_IS_NULL);
                continue;
            }
            Integer alreadyBuyNum = skuLimitMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), limitId, vo.getGoodsId(), vo.getSkuId())).getSoldNum();
            if (!Objects.equals(requestVo.getGoodsDetailList().get(0).getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
                vo.setAlreadyBuyNum(alreadyBuyNum == null ? 0 : alreadyBuyNum);
            }
            vo.setSoldNum(alreadyBuyNum == null ? 0 : alreadyBuyNum);
            Integer skuLimitNum = skuLimitMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), limitId, vo.getGoodsId(), vo.getSkuId())).getLimitNum();
            vo.setSkuLimitNum(skuLimitNum);
            Integer canBuyNum = skuLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
            if (canBuyNum < 0) {
                canBuyNum = 0;
            }
            vo.setCanBuyNum(vo.getCanBuyNum() > canBuyNum ? canBuyNum : vo.getCanBuyNum());
        }
        if (requestVo.getGoodsDetailList().get(0).getCheckLimit()) {
            //结算调用，抛异常
            Map<String, Integer> activityBuyNumMap = new HashMap<>();
            for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {

                limitInfoEntity = limitInfoMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
                Long limitId = limitInfoEntity == null ? null : limitInfoEntity.getLimitId();
                if (activityBuyNumMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), limitId, vo.getGoodsId(), vo.getSkuId())) == null) {
                    activityBuyNumMap.put(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), limitId, vo.getGoodsId(), vo.getSkuId()), vo.getGoodsBuyNum());
                } else {
                    activityBuyNumMap.put(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), limitId, vo.getGoodsId(), vo.getSkuId()), activityBuyNumMap.get(MapKeyUtil.buildSkuLimitMapKey(vo.getPid(), limitId, vo.getGoodsId(), vo.getSkuId())) + vo.getGoodsBuyNum());
                }
            }
            Iterator<Map.Entry<String, Integer>> activityBuyNumIterator = activityBuyNumMap.entrySet().iterator();
            while (activityBuyNumIterator.hasNext()) {
                Map.Entry<String, Integer> entry = activityBuyNumIterator.next();
                Integer alreadyBuyNum = skuLimitMap.get(entry.getKey()).getSoldNum();
                Integer skuLimitNum = skuLimitMap.get(entry.getKey()).getLimitNum();
                if (skuLimitNum == null) {
                    throw new LimitationBizException(LimitationErrorCode.LIMIT_SKU_IS_NULL);
                }
                int alreadyBuyNumValue = alreadyBuyNum == null ? 0 : alreadyBuyNum;
                if (alreadyBuyNumValue + entry.getValue() > skuLimitNum) {
                    String msg = "超出" + LimitBizTypeEnum.getLimitLevelEnumByLevel(requestVo.getGoodsDetailList().get(0).getBizType()).getName()
                            + "可售sku" + (alreadyBuyNumValue + entry.getValue() - skuLimitNum) + "件";
                    throw new LimitationBizException(LimitationErrorCode.BEYOND_SKU_LIMIT_NUM, msg, msg);
                }
            }
        }
    }

    private GoodsLimitInfoListResponseVo buildResponseVo(GoodsLimitInfoListRequestVo requestVo,
                                                         Map<String, Integer> activityUserLimitNumMap,
                                                         Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap,
                                                         Map<String, Integer> userGoodsLimitNumMap,
                                                         Map<String, Integer> userPidGoodsLimitNumMap,
                                                         Map<String, LimitInfoEntity> limitInfoMap
                                                         ) {
        GoodsLimitInfoListResponseVo responseVo = new GoodsLimitInfoListResponseVo();
        List<GoodsLimitInfoListVo> goodsLimitInfoList = new ArrayList<>();
        validActivityLimit(requestVo, activityUserLimitNumMap, goodsLimitInfoList, limitInfoMap);
        validGoodsLimit(requestVo, goodsLimitNumMap, userGoodsLimitNumMap, goodsLimitInfoList, userPidGoodsLimitNumMap,limitInfoMap);
        responseVo.setGoodsLimitInfoList(goodsLimitInfoList);
        return responseVo;
    }

    private void validGoodsLimit(GoodsLimitInfoListRequestVo requestVo,
                                 Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap,
                                 Map<String, Integer> userGoodsLimitNumMap,
                                 List<GoodsLimitInfoListVo> goodsLimitInfoList,
                                 Map<String, Integer> userPidGoodsLimitNumMap,
                                 Map<String, LimitInfoEntity> limitInfoMap

    ) {
        LimitInfoEntity limitInfoEntity = null;
        for (GoodsLimitInfoListVo vo : goodsLimitInfoList) {
            limitInfoEntity = limitInfoMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            if (limitInfoEntity == null) {
                continue;
            }
            Long limitId = limitInfoEntity.getLimitId();
            Integer alreadyBuyNum = userGoodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            Integer pidAlreadyBuyNum = userPidGoodsLimitNumMap.get(MapKeyUtil.buildPidGoodsLimitNumMap(vo.getPid(), limitId, vo.getGoodsId()));
            List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitNumMap.get(MapKeyUtil.buildPidGoodsLimitNumMap(vo.getPid(), limitId, vo.getGoodsId()));
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
            if (pidGoodsLimitNum == null) {
                pidGoodsLimitNum = 0;
            }
            vo.setAlreadyBuyNum(alreadyBuyNum == null ? 0 : alreadyBuyNum);
            vo.setGoodsLimitNum(goodsLimitNum);
            if (pidGoodsLimitNum != LimitConstant.UNLIMITED_NUM) {
                vo.setGoodsLimitNum(pidGoodsLimitNum);
            }
            if (LimitConstant.UNLIMITED_NUM == goodsLimitNum) {
                // 不限购
                if (LimitConstant.UNLIMITED_NUM == pidGoodsLimitNum) {
                    if (vo.getLimitStatus()) {
                        vo.setGoodsCanBuyNum(vo.getCanBuyNum());
                    } else {
                        vo.setGoodsCanBuyNum(Integer.MAX_VALUE);
                    }
                } else {
                    // 新零售-店铺限购
                    vo.setLimitStatus(true);
                    Integer canBuyNum = pidGoodsLimitNum - (pidAlreadyBuyNum == null ? 0 : pidAlreadyBuyNum);
                    if (canBuyNum < 0) {
                        canBuyNum = 0;
                    }
                    vo.setCanBuyNum(vo.getCanBuyNum() < canBuyNum ? vo.getCanBuyNum() : canBuyNum);
                    vo.setGoodsCanBuyNum(vo.getCanBuyNum());
                }
            } else {
                // 微商城-门店限购
                if (LimitConstant.UNLIMITED_NUM == pidGoodsLimitNum) {
                    vo.setLimitStatus(true);
                    Integer canBuyNum = goodsLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                    if (canBuyNum < 0) {
                        canBuyNum = 0;
                    }
                    vo.setCanBuyNum(vo.getCanBuyNum() < canBuyNum ? vo.getCanBuyNum() : canBuyNum);
                    vo.setGoodsCanBuyNum(vo.getCanBuyNum());
                } else {
                    // 店铺、门店均限购
                    vo.setLimitStatus(true);
                    Integer storeCanBuyNum = goodsLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                    Integer pidCanBuyNum = pidGoodsLimitNum - (pidAlreadyBuyNum == null ? 0 : pidAlreadyBuyNum);
                    Integer canBuyNum = storeCanBuyNum < pidCanBuyNum ? storeCanBuyNum : pidCanBuyNum;
                    if (canBuyNum < 0) {
                        canBuyNum = 0;
                    }
                    vo.setCanBuyNum(vo.getCanBuyNum() < canBuyNum ? vo.getCanBuyNum() : canBuyNum);
                    vo.setGoodsCanBuyNum(vo.getCanBuyNum());
                }
            }
        }
        if (requestVo.getGoodsDetailList().get(0).getCheckLimit()) {
            //结算调用，抛异常
            Map<String, Integer> activityBuyNumMap = new HashMap<>();
            for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
                limitInfoEntity = limitInfoMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
                if (limitInfoEntity == null) {
                    continue;
                }
                Long limitId = limitInfoEntity.getLimitId();
                if (activityBuyNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId())) == null) {
                    activityBuyNumMap.put(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()), vo.getGoodsBuyNum());
                } else {
                    activityBuyNumMap.put(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()), activityBuyNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId())) + vo.getGoodsBuyNum());
                }
            }
            for (GoodsLimitInfoListVo vo : goodsLimitInfoList) {
                Integer canBuyNum = vo.getCanBuyNum();
                limitInfoEntity = limitInfoMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
                if (limitInfoEntity == null) {
                    continue;
                }
                Long limitId = limitInfoEntity.getLimitId();
                Integer orderBuyNum = activityBuyNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
                if (canBuyNum < orderBuyNum) {
                    String msg = "超出" + LimitBizTypeEnum.getLimitLevelEnumByLevel(requestVo.getGoodsDetailList().get(0).getBizType()).getName()
                            + "商品限购" + (orderBuyNum - canBuyNum) + "件";
                    throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM, msg, msg);
                }
            }
        }
    }

    private void validActivityLimit(GoodsLimitInfoListRequestVo requestVo,
                                    Map<String, Integer> activityUserLimitNumMap,
                                    List<GoodsLimitInfoListVo> goodsLimitInfoList,
                                    Map<String, LimitInfoEntity> limitInfoMap
                                    ) {
        LimitInfoEntity limitInfoEntity = null;

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

            limitInfoEntity = limitInfoMap.get(limitIdKey);
            Integer activityLimitNum = limitInfoEntity == null ? null : limitInfoEntity.getLimitNum();

            if (activityLimitNum == null) {
                //throw new LimitationBizException(LimitationErrorCode.LIMIT_ACTIVITY_IS_NULL);
                continue;
            }
            goodsLimitInfoListVo.setActivityLimitNum(activityLimitNum);
            if (activityLimitNum == LimitConstant.UNLIMITED_NUM) {
                goodsLimitInfoListVo.setLimitStatus(false);
                goodsLimitInfoListVo.setCanBuyNum(Integer.MAX_VALUE);
            } else {
                goodsLimitInfoListVo.setLimitStatus(true);
                Integer canBuyNum = activityLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                if (canBuyNum < 0) {
                    canBuyNum = 0;
                }
                goodsLimitInfoListVo.setCanBuyNum(canBuyNum);
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
                limitInfoEntity = limitInfoMap.get(entry.getKey());
                Integer activityLimitNum = limitInfoEntity == null ? null : limitInfoEntity.getLimitNum();

                if (Objects.equals(LimitConstant.UNLIMITED_NUM, activityLimitNum)) {
                    continue;
                }
                int alreadyBuyNumValue = alreadyBuyNum == null ? 0 : alreadyBuyNum;
                if (alreadyBuyNumValue + entry.getValue() > activityLimitNum) {
                    String msg = "超出" + LimitBizTypeEnum.getLimitLevelEnumByLevel(requestVo.getGoodsDetailList().get(0).getBizType()).getName()
                            + "活动限购" + (alreadyBuyNumValue + entry.getValue() - activityLimitNum) + "件";
                    throw new LimitationBizException(LimitationErrorCode.BEYOND_ACTIVITY_LIMIT_NUM, msg, msg);
                }
            }
        }
    }

    private List<SkuLimitInfoEntity>  buildQueryEntity(GoodsLimitInfoListRequestVo requestVo,
                                                       Map<String, LimitInfoEntity> limitInfoMap) {
        List<SkuLimitInfoEntity> querySkuLimitList = new ArrayList<>(requestVo.getGoodsDetailList().size());
        LimitInfoEntity limitInfoEntity = null;
        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            limitInfoEntity = limitInfoMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            if (limitInfoEntity == null) {
                continue;
            }
            Long limitId = limitInfoEntity.getLimitId();
            SkuLimitInfoEntity skuLimitInfoEntity = new SkuLimitInfoEntity();
            skuLimitInfoEntity.setLimitId(limitId);
            skuLimitInfoEntity.setPid(vo.getPid());
            skuLimitInfoEntity.setStoreId(vo.getStoreId());
            skuLimitInfoEntity.setGoodsId(vo.getGoodsId());
            skuLimitInfoEntity.setSkuId(vo.getSkuId());
            querySkuLimitList.add(skuLimitInfoEntity);
        }
        return querySkuLimitList;
    }

    @Override
    public QueryGoodsLimitNumListResponseVo queryGoodsLimitNumList(QueryGoodsLimitNumRequestVo requestVo) {
        QueryGoodsLimitNumListResponseVo responseVo = new QueryGoodsLimitNumListResponseVo();
        List<QueryGoodsLimitNumVo> queryGoodsLimitNumList = new ArrayList<>();
        Integer bizType = requestVo.getQueryGoodslimitNumVoList().get(0).getBizType();
        Integer activityStockType = requestVo.getQueryGoodslimitNumVoList().get(0).getActivityStockType();
        List<GoodsLimitInfoEntity> goodsLimitInfoEntityList;
        Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap = null;
        // 非社区团购查goods限购信息
        if (!Objects.equals(bizType, LimitBizTypeEnum.BIZ_TYPE_COMMUNITY_GROUPON.getLevel())) {
            goodsLimitInfoEntityList = goodsLimitInfoDao.listGoodsLimitNum(requestVo.getQueryGoodslimitNumVoList());
            if (CollectionUtils.isEmpty(goodsLimitInfoEntityList)) {
                return responseVo;
            }
            goodsLimitNumMap = buildGoodsLimitMap(goodsLimitInfoEntityList);
        }
        //如果是特权价或者限时折扣可售数量的 +社区团购，需要查询sku的限购数量
        Map<String, List<SkuLimitInfoEntity>> skuLimitNumMap = null;
        if (Objects.equals(bizType, ActivityTypeEnum.PRIVILEGE_PRICE.getType())
                || (Objects.equals(bizType, ActivityTypeEnum.DISCOUNT.getType())
                && Objects.equals(activityStockType, LimitConstant.DISCOUNT_TYPE_SKU))
                || Objects.equals(bizType, LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())
                || Objects.equals(bizType, LimitBizTypeEnum.BIZ_TYPE_COMMUNITY_GROUPON.getLevel())) {
            List<SkuLimitInfoEntity> skuLimitInfoEntityList = skuLimitInfoDao.listSkuLimitNum(requestVo.getQueryGoodslimitNumVoList());
            skuLimitNumMap = buildSkuLimitMap(skuLimitInfoEntityList);
        }
        for (QueryGoodsLimitNumListVo request : requestVo.getQueryGoodslimitNumVoList()) {
            QueryGoodsLimitNumVo queryGoodsLimitNumVo = new QueryGoodsLimitNumVo();
            queryGoodsLimitNumVo.setPid(request.getPid());
            queryGoodsLimitNumVo.setGoodsId(request.getGoodsId());
            queryGoodsLimitNumVo.setBizId(request.getBizId());
            queryGoodsLimitNumVo.setBizType(request.getBizType());
            // 非社区团购查goods限购信息
            if (!Objects.equals(bizType, LimitBizTypeEnum.BIZ_TYPE_COMMUNITY_GROUPON.getLevel())) {
                List<GoodsLimitInfoEntity> entityList = goodsLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(request.getPid(), request.getGoodsId()));
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
            }
            //如果是特权价或者限时折扣可售数量的 +社区团购，需要设置sku的限购数量
            List<SkuLimitInfo> skuLimitInfoList = new ArrayList<>();
            if (MapUtils.isNotEmpty(skuLimitNumMap)) {
                List<SkuLimitInfoEntity> skuLimitInfoEntityList = skuLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(request.getPid(), request.getGoodsId()));
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
            if (CollectionUtils.isEmpty(skuLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getGoodsId())))) {
                List<SkuLimitInfoEntity> entityList = new ArrayList<>();
                entityList.add(entity);
                skuLimitNumMap.put(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getGoodsId()), entityList);
            } else {
                skuLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getGoodsId())).add(entity);
            }
        }
        return skuLimitNumMap;
    }

    private Map<String, List<GoodsLimitInfoEntity>> buildGoodsLimitMap(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList) {
        Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap = new HashMap<>();
        if (goodsLimitInfoEntityList.contains(null)) {
            LOGGER.error("查询商品限购表空指针复现---》list.size为："+ goodsLimitInfoEntityList.size()+ "");
            for (GoodsLimitInfoEntity goodsEntity : goodsLimitInfoEntityList) {
                if (goodsEntity != null) {
                    LOGGER.error(goodsEntity.toString());
                }
            }
        }
        for (GoodsLimitInfoEntity entity : goodsLimitInfoEntityList) {
            if (CollectionUtils.isEmpty(goodsLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getGoodsId())))) {
                List<GoodsLimitInfoEntity> entityList = new ArrayList<>();
                entityList.add(entity);
                goodsLimitNumMap.put(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getGoodsId()), entityList);
            } else {
                goodsLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getGoodsId())).add(entity);
            }
        }
        return goodsLimitNumMap;
    }

    @Override
    public QueryActivityLimitInfoResponseVo queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo) {
        QueryActivityLimitInfoResponseVo responseVo = new QueryActivityLimitInfoResponseVo();
        LimitInfoEntity infoEntity = null;
        try {
            infoEntity = limitInfoDao.getLimitInfo(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType(), LimitConstant.DELETED));
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_LIMIT_INFO_ERROR, e);
        }
        if (infoEntity == null) {
            return responseVo;
        }
        Integer threshold = null;
        Integer soldNum = null;
        if (Objects.equals(requestVo.getBizType(), ActivityTypeEnum.COMBINATION_BUY.getType())) {
            List<SkuLimitInfoEntity> skuLimitInfoEntities = null;
            try {
                skuLimitInfoEntities = skuLimitInfoDao.listSkuLimitByLimitId(new LimitParam(infoEntity.getPid(), LimitConstant.DELETED, infoEntity.getLimitId()));
            } catch (Exception e) {
                throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_SKU_INFO_ERROR, e);
            }
            if (CollectionUtils.isNotEmpty(skuLimitInfoEntities)) {
                threshold = skuLimitInfoEntities.get(0).getLimitNum();
                soldNum = skuLimitInfoEntities.get(0).getSoldNum();
            }
        }
        responseVo.setPid(requestVo.getPid());
        responseVo.setStoreId(requestVo.getStoreId());
        responseVo.setBizId(requestVo.getBizId());
        responseVo.setBizType(requestVo.getBizType());
        responseVo.setActivityLimitNum(infoEntity.getLimitNum());
        responseVo.setThreshold(threshold);
        responseVo.setSoldNum(soldNum);
        return responseVo;
    }

    @Override
    public QueryGoodsLimitDetailListResponseVo queryGoodsLimitDetailList(QueryGoodsLimitDetailListRequestVo requestVo) {
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
        for (QueryGoodsLimitDetailListVo vo : requestVo.getGoodsList()) {
            LimitParam limitParam = new LimitParam();
            limitParam.setBizType(vo.getBizType());
            limitParam.setBizId(vo.getBizId());
            limitParam.setPid(vo.getPid());
            queryLimitInfoList.add(limitParam);
        }
        //查询限购主表
        List<LimitInfoEntity> limitInfoEntityList = limitInfoDao.listLimitInfoByBizId(queryLimitInfoList);
        if (CollectionUtils.isEmpty(limitInfoEntityList)) {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_GOODS_IS_NULL);
        }
        for (LimitInfoEntity entity : limitInfoEntityList) {
            String limitIdMapKey = MapKeyUtil.buildLimitIdMapKey(entity.getPid(), entity.getBizType(), entity.getBizId());
            limitIdMap.put(limitIdMapKey, entity.getLimitId());
            activityLimitNumMap.put(limitIdMapKey, entity.getLimitNum());
        }
        //构建查询数据库入参
        buildGoodsQueryEntity(requestVo, limitIdMap, queryGoodsLimitList, queryUserGoodsLimitList, queryUserLimitList, querySkuLimitList);

        //查询商品限购信息
        List<GoodsLimitInfoEntity> goodsLimitInfoList = goodsLimitInfoDao.listGoodsLimitByGoodsId(queryGoodsLimitList);
        for (GoodsLimitInfoEntity entity : goodsLimitInfoList) {
            String goodsLimitNumKey = MapKeyUtil.buildPidGoodsLimitNumMap(entity.getPid(), entity.getLimitId(), entity.getGoodsId());
            if (CollectionUtils.isEmpty(goodsLimitNumMap.get(goodsLimitNumKey))) {
                List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = new ArrayList<>();
                goodsLimitInfoEntityList.add(entity);
                goodsLimitNumMap.put(goodsLimitNumKey, goodsLimitInfoEntityList);
            } else {
                goodsLimitNumMap.get(goodsLimitNumKey).add(entity);
            }
        }
        //查询用户商品下单记录
        List<UserGoodsLimitEntity> userGoodsLimitList = userGoodsLimitDao.listUserGoodsLimit(queryUserGoodsLimitList);
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
        //查询sku的限购和限购记录
        List<SkuLimitInfoEntity> skuLimitList = skuLimitInfoDao.listSkuLimit(querySkuLimitList);


        //根据限购类型进行限购校验
        Map<Long, List<SkuLimitInfoEntity>> goodsSkuMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(skuLimitList)) {
            for (SkuLimitInfoEntity skuLimitInfoEntity : skuLimitList) {
                if (CollectionUtils.isEmpty(goodsSkuMap.get(skuLimitInfoEntity.getGoodsId()))) {
                    List<SkuLimitInfoEntity> skuLimitInfoEntityList = new ArrayList<>();
                    skuLimitInfoEntityList.add(skuLimitInfoEntity);
                    goodsSkuMap.put(skuLimitInfoEntity.getGoodsId(), skuLimitInfoEntityList);
                } else {
                    goodsSkuMap.get(skuLimitInfoEntity.getGoodsId()).add(skuLimitInfoEntity);
                }
            }
        }

        if (Objects.equals(requestVo.getGoodsList().get(0).getBizType(), LimitBizTypeEnum.BIZ_TYPE_POINT.getLevel())) {

            QueryGoodsLimitDetailListResponseVo responseVo = buildPointQueryGoodsLimitDetailListResponseVo(requestVo, limitIdMap, goodsLimitNumMap, userGoodsLimitNumMap);
            if (MapUtils.isNotEmpty(goodsSkuMap)) {
                validSkuLimit(responseVo, goodsSkuMap);
            }
            return responseVo;
        } else {
            //要校验活动限购
            List<UserLimitEntity> userLimitEntityList = userLimitDao.listUserLimitByLimitId(queryUserLimitList);
            for (UserLimitEntity vo : userLimitEntityList) {
                //多门店下单，进行合并
                if (activityUserLimitNumMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId())) == null) {
                    activityUserLimitNumMap.put(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()), vo.getBuyNum());
                } else {
                    Integer buyNum = activityUserLimitNumMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
                    activityUserLimitNumMap.put(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()), vo.getBuyNum() + buyNum);
                }
            }
            QueryGoodsLimitDetailListResponseVo responseVo = buildQueryGoodsLimitDetailListResponseVo(requestVo, limitIdMap, activityLimitNumMap, activityUserLimitNumMap, goodsLimitNumMap, userGoodsLimitNumMap, userPidGoodsLimitNumMap);
            if (MapUtils.isNotEmpty(goodsSkuMap)) {
                validSkuLimit(responseVo, goodsSkuMap);
            }
            return responseVo;
        }
    }

    @Override
    public QueryActivityLimitInfoListResponseVo queryActivityLimitInfoList(QueryActivityLimitInfoListRequestVo requestVo) {
        // 构建查询限购主表入参
        List<LimitParam> limitParams = new ArrayList<>(requestVo.getBizIds().size());
        for (Long bizId : requestVo.getBizIds()) {
            LimitParam limitParam = new LimitParam(requestVo.getPid(), bizId, requestVo.getBizType(), LimitConstant.DELETED);
            limitParams.add(limitParam);
        }

        // 查询限购主表获取限购信息
        List<LimitInfoEntity> limitInfoList = null;
        try {
            limitInfoList = limitInfoDao.listLimitInfoByBizId(limitParams);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_LIMIT_INFO_LIST_ERROR, e);
        }
        if (CollectionUtils.isEmpty(limitInfoList)) {
            return new QueryActivityLimitInfoListResponseVo();
        }

        // 构建查询SKU表入参
        List<LimitParam> skuLimitParams = new ArrayList<>(limitInfoList.size());
        for (LimitInfoEntity limitInfo : limitInfoList) {
            skuLimitParams.add(new LimitParam(requestVo.getPid(), LimitConstant.DELETED, limitInfo.getLimitId()));
        }

        // 查询SKU表获取限购信息
        List<SkuLimitInfoEntity> skuInfoList = null;
        try {
            skuInfoList = skuLimitInfoDao.listSkuLimitByLimitIdList(skuLimitParams);
        } catch (Exception e) {
            throw new LimitationBizException(LimitationErrorCode.SQL_QUERY_SKU_INFO_LIST_ERROR, e);
        }

        // 构建limit对应可售数量的映射Map
        Map<Long, Integer> limitIdMappingThreshold = new HashMap<>();
        // 构建limit对应已售数量的映射Map
        Map<Long, Integer> limitIdMappingSoldNum = new HashMap<>();
        if (CollectionUtils.isNotEmpty(skuInfoList)) {
            for (SkuLimitInfoEntity skuInfo : skuInfoList) {
                limitIdMappingThreshold.put(skuInfo.getLimitId(), skuInfo.getLimitNum());
                limitIdMappingSoldNum.put(skuInfo.getLimitId(), skuInfo.getSoldNum());
            }
        }

        // 构建出参
        QueryActivityLimitInfoListResponseVo responseVo = buildQueryActivityLimitInfoListResponseVo(requestVo,
                limitInfoList, limitIdMappingThreshold, limitIdMappingSoldNum);

        // 返回出参
        return responseVo;
    }

    private QueryActivityLimitInfoListResponseVo buildQueryActivityLimitInfoListResponseVo(QueryActivityLimitInfoListRequestVo requestVo,
                                                                                           List<LimitInfoEntity> limitInfoList,
                                                                                           Map<Long, Integer> limitIdMappingThreshold,
                                                                                           Map<Long, Integer> limitIdMappingSoldNum) {
        QueryActivityLimitInfoListResponseVo responseVo = new QueryActivityLimitInfoListResponseVo();
        responseVo.setPid(requestVo.getPid());
        responseVo.setStoreId(requestVo.getStoreId());
        List<QueryActivityLimitInfoResponseVo> returnList = new ArrayList<>();
        for (LimitInfoEntity limitInfo : limitInfoList) {
            QueryActivityLimitInfoResponseVo limitInfoResponseVo = new QueryActivityLimitInfoResponseVo();
            limitInfoResponseVo.setPid(requestVo.getPid());
            limitInfoResponseVo.setStoreId(requestVo.getStoreId());
            limitInfoResponseVo.setBizId(limitInfo.getBizId());
            limitInfoResponseVo.setBizType(limitInfo.getBizType());
            limitInfoResponseVo.setActivityLimitNum(limitInfo.getLimitNum());
            limitInfoResponseVo.setThreshold(limitIdMappingThreshold.get(limitInfo.getLimitId()));
            limitInfoResponseVo.setSoldNum(limitIdMappingSoldNum.get(limitInfo.getLimitId()));
            returnList.add(limitInfoResponseVo);
        }
        responseVo.setLimitInfoVos(returnList);
        return responseVo;
    }

    private QueryGoodsLimitDetailListResponseVo buildQueryGoodsLimitDetailListResponseVo(QueryGoodsLimitDetailListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, Integer> activityLimitNumMap, Map<String, Integer> activityUserLimitNumMap, Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap, Map<String, Integer> userGoodsLimitNumMap, Map<String, Integer> userPidGoodsLimitNumMap) {
        QueryGoodsLimitDetailListResponseVo responseVo = new QueryGoodsLimitDetailListResponseVo();
        List<QueryGoodsLimitDetailVo> goodsLimitInfoList = new ArrayList<>();
        validActivityLimitForGoods(requestVo, limitIdMap, activityLimitNumMap, activityUserLimitNumMap, goodsLimitInfoList);
        validGoodsLimitForGoods(requestVo, limitIdMap, goodsLimitNumMap, userGoodsLimitNumMap, goodsLimitInfoList, userPidGoodsLimitNumMap);
        responseVo.setQueryGoodsLimitDetailVoList(goodsLimitInfoList);
        return responseVo;
    }

    private void validGoodsLimitForGoods(QueryGoodsLimitDetailListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap, Map<String, Integer> userGoodsLimitNumMap, List<QueryGoodsLimitDetailVo> goodsLimitInfoList, Map<String, Integer> userPidGoodsLimitNumMap) {
        for (QueryGoodsLimitDetailVo vo : goodsLimitInfoList) {
            Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            Integer alreadyBuyNum = userGoodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            Integer pidAlreadyBuyNum = userPidGoodsLimitNumMap.get(MapKeyUtil.buildPidGoodsLimitNumMap(vo.getPid(), limitId, vo.getGoodsId()));
            List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitNumMap.get(MapKeyUtil.buildPidGoodsLimitNumMap(vo.getPid(), limitId, vo.getGoodsId()));
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
            if (pidGoodsLimitNum == null) {
                pidGoodsLimitNum = 0;
            }
            vo.setGoodsLimitNum(goodsLimitNum);
            if (LimitConstant.UNLIMITED_NUM == goodsLimitNum) {
                if (LimitConstant.UNLIMITED_NUM == pidGoodsLimitNum) {
                    if (vo.getGoodsLimit()) {

                    } else {
                        vo.setGoodsCanBuyNum(Integer.MAX_VALUE);
                    }
                } else {
                    vo.setGoodsLimit(true);
                    Integer canBuyNum = pidGoodsLimitNum - (pidAlreadyBuyNum == null ? 0 : pidAlreadyBuyNum);
                    if (canBuyNum < 0) {
                        canBuyNum = 0;
                    }
                    vo.setGoodsCanBuyNum(vo.getGoodsCanBuyNum() < canBuyNum ? vo.getGoodsCanBuyNum() : canBuyNum);
                }
            } else {
                if (LimitConstant.UNLIMITED_NUM == pidGoodsLimitNum) {
                    vo.setGoodsLimit(true);
                    Integer canBuyNum = goodsLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                    if (canBuyNum < 0) {
                        canBuyNum = 0;
                    }
                    vo.setGoodsCanBuyNum(vo.getGoodsCanBuyNum() < canBuyNum ? vo.getGoodsCanBuyNum() : canBuyNum);
                } else {
                    vo.setGoodsLimit(true);
                    Integer storeCanBuyNum = goodsLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                    Integer pidCanBuyNum = pidGoodsLimitNum - (pidAlreadyBuyNum == null ? 0 : pidAlreadyBuyNum);
                    Integer canBuyNum = storeCanBuyNum < pidCanBuyNum ? storeCanBuyNum : pidCanBuyNum;
                    if (canBuyNum < 0) {
                        canBuyNum = 0;
                    }
                    vo.setGoodsCanBuyNum(vo.getGoodsCanBuyNum() < canBuyNum ? vo.getGoodsCanBuyNum() : canBuyNum);
                }
            }
        }
    }

    private void validActivityLimitForGoods(QueryGoodsLimitDetailListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, Integer> activityLimitNumMap, Map<String, Integer> activityUserLimitNumMap, List<QueryGoodsLimitDetailVo> goodsLimitInfoList) {
        for (QueryGoodsLimitDetailListVo vo : requestVo.getGoodsList()) {
            QueryGoodsLimitDetailVo goodsLimitInfoListVo = new QueryGoodsLimitDetailVo();
            goodsLimitInfoListVo.setPid(vo.getPid());
            goodsLimitInfoListVo.setStoreId(vo.getStoreId());
            goodsLimitInfoListVo.setBizType(vo.getBizType());
            goodsLimitInfoListVo.setBizId(vo.getBizId());
            goodsLimitInfoListVo.setGoodsId(vo.getGoodsId());
            String limitIdKey = MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId());
            Integer alreadyBuyNum = activityUserLimitNumMap.get(limitIdKey);
            Integer activityLimitNum = activityLimitNumMap.get(limitIdKey);
            if (activityLimitNum == null) {
                //throw new LimitationBizException(LimitationErrorCode.LIMIT_ACTIVITY_IS_NULL);
                continue;
            }
            if (activityLimitNum == LimitConstant.UNLIMITED_NUM) {
                goodsLimitInfoListVo.setGoodsLimit(false);
                goodsLimitInfoListVo.setGoodsCanBuyNum(Integer.MAX_VALUE);
            } else {
                goodsLimitInfoListVo.setGoodsLimit(true);
                Integer canBuyNum = activityLimitNum - (alreadyBuyNum == null ? 0 : alreadyBuyNum);
                if (canBuyNum < 0) {
                    canBuyNum = 0;
                }
                goodsLimitInfoListVo.setGoodsCanBuyNum(canBuyNum);
            }
            goodsLimitInfoList.add(goodsLimitInfoListVo);
        }
    }

    private void validSkuLimit(QueryGoodsLimitDetailListResponseVo responseVo, Map<Long, List<SkuLimitInfoEntity>> goodsSkuMap) {
        for (QueryGoodsLimitDetailVo vo : responseVo.getQueryGoodsLimitDetailVoList()) {
            List<SkuLimitInfoEntity> skuLimitInfoEntityList = goodsSkuMap.get(vo.getGoodsId());
            if (CollectionUtils.isEmpty(skuLimitInfoEntityList)) {
                continue;
            }
            List<SkuLimitInfo> skuLimitInfoList = new ArrayList<>();
            Integer realSoldNum = 0;
            for (SkuLimitInfoEntity skuLimitInfoEntity : skuLimitInfoEntityList) {
                SkuLimitInfo skuLimitInfo = new SkuLimitInfo();
                skuLimitInfo.setSkuId(skuLimitInfoEntity.getSkuId());
                skuLimitInfo.setSkuLimitNum(skuLimitInfoEntity.getLimitNum());
                skuLimitInfo.setAlreadySoldNum(skuLimitInfoEntity.getSoldNum());
                Integer canBuyNum = skuLimitInfoEntity.getLimitNum() - (skuLimitInfoEntity.getSoldNum() == null ? 0 : skuLimitInfoEntity.getSoldNum());
                if (canBuyNum < 0) {
                    canBuyNum = 0;
                }
                if (vo.getGoodsCanBuyNum() == 0) {
                    skuLimitInfo.setCanBuySkuNum(canBuyNum);
                } else {
                    skuLimitInfo.setCanBuySkuNum(canBuyNum > vo.getGoodsCanBuyNum() ? vo.getGoodsCanBuyNum() : canBuyNum);
                }
                realSoldNum = realSoldNum + skuLimitInfo.getCanBuySkuNum();
                skuLimitInfoList.add(skuLimitInfo);
            }
            vo.setSkuLimitInfoList(skuLimitInfoList);
            vo.setGoodsLimit(true);
            vo.setRealSoldNum(realSoldNum);
            vo.setGoodsCanBuyNum(vo.getGoodsCanBuyNum() > realSoldNum ? realSoldNum : vo.getGoodsCanBuyNum());
        }
    }

    private QueryGoodsLimitDetailListResponseVo buildPointQueryGoodsLimitDetailListResponseVo(QueryGoodsLimitDetailListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap, Map<String, Integer> userGoodsLimitNumMap) {
        QueryGoodsLimitDetailListResponseVo responseVo = new QueryGoodsLimitDetailListResponseVo();
        List<QueryGoodsLimitDetailVo> goodsLimitInfoList = new ArrayList<>();
        for (QueryGoodsLimitDetailListVo vo : requestVo.getGoodsList()) {
            QueryGoodsLimitDetailVo goodsLimitInfoListVo = new QueryGoodsLimitDetailVo();
            goodsLimitInfoListVo.setPid(vo.getPid());
            goodsLimitInfoListVo.setStoreId(vo.getStoreId());
            goodsLimitInfoListVo.setBizType(vo.getBizType());
            goodsLimitInfoListVo.setBizId(vo.getBizId());
            goodsLimitInfoListVo.setGoodsId(vo.getGoodsId());
            Long limitId = limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitNumMap.get(MapKeyUtil.buildPidGoodsLimitNumMap(vo.getPid(), limitId, vo.getGoodsId()));
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
                goodsLimitInfoListVo.setGoodsLimit(false);
            } else {
                goodsLimitInfoListVo.setGoodsLimit(true);
            }
            Integer userBuyNum = userGoodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            Integer canBuyNum = goodsLimitNum - (userBuyNum == null ? 0 : userBuyNum);

            goodsLimitInfoListVo.setGoodsLimitNum(goodsLimitNum);
            goodsLimitInfoListVo.setAlreadyBuyGoodsNum(userBuyNum == null ? 0 : userBuyNum);
            goodsLimitInfoListVo.setGoodsCanBuyNum(canBuyNum < 0 ? 0 : canBuyNum);
            goodsLimitInfoList.add(goodsLimitInfoListVo);
        }
        responseVo.setQueryGoodsLimitDetailVoList(goodsLimitInfoList);
        return responseVo;
    }

    private void buildGoodsQueryEntity(QueryGoodsLimitDetailListRequestVo requestVo, Map<String, Long> limitIdMap, List<GoodsLimitInfoEntity> queryGoodsLimitList, List<UserGoodsLimitEntity> queryUserGoodsLimitList, List<UserLimitEntity> queryUserLimitList, List<SkuLimitInfoEntity> querySkuLimitList) {
        for (QueryGoodsLimitDetailListVo vo : requestVo.getGoodsList()) {
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
            querySkuLimitList.add(skuLimitInfoEntity);
        }
    }

    private GoodsLimitInfoListResponseVo buildGoodsLimitInfoListResponseVo(
            GoodsLimitInfoListRequestVo requestVo,
            Map<String, List<GoodsLimitInfoEntity>> goodsLimitNumMap,
            Map<String, Integer> userGoodsLimitNumMap,
            Map<String, LimitInfoEntity> limitInfoMap

    ) {
        GoodsLimitInfoListResponseVo responseVo = new GoodsLimitInfoListResponseVo();
        List<GoodsLimitInfoListVo> goodsLimitInfoList = new ArrayList<>();
        LimitInfoEntity limitInfoEntity = null;
        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            GoodsLimitInfoListVo goodsLimitInfoListVo = new GoodsLimitInfoListVo();
            goodsLimitInfoListVo.setPid(vo.getPid());
            goodsLimitInfoListVo.setStoreId(vo.getStoreId());
            goodsLimitInfoListVo.setBizType(vo.getBizType());
            goodsLimitInfoListVo.setBizId(vo.getBizId());
            goodsLimitInfoListVo.setGoodsId(vo.getGoodsId());
            goodsLimitInfoListVo.setSkuId(vo.getSkuId());
            limitInfoEntity = limitInfoMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId()));
            if (limitInfoEntity == null) {
                continue;
            }

            Long limitId = limitInfoEntity.getLimitId();
            List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitNumMap.get(MapKeyUtil.buildPidGoodsLimitNumMap(vo.getPid(), limitId, vo.getGoodsId()));
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

    private List<UserGoodsLimitEntity>   getUserGoodsLimitList(
            Map<String, Set<Long>> requestLimitGoodsMap,
            Map<String, LimitInfoEntity> limitInfoMap,
            Long wid
    ) {
        List<GoodsLimitInfoEntity> goodsLimitInfoList = new ArrayList<>();
        String[] keys = null;
        CommonLimitParam commonLimitParam = new CommonLimitParam();
        List<UserGoodsLimitEntity> tmpUserGoodsLimitList = null;
        List<UserGoodsLimitEntity> userGoodsLimitList = new ArrayList<>();
        LimitInfoEntity limitInfoEntity = null;
        for (String key : requestLimitGoodsMap.keySet()) {
            keys = MapKeyUtil.getLimitIdMapKeyArray(key);
            commonLimitParam.setPid(Long.valueOf(keys[MapKeyUtil.LIMIT_ID_PID_INDEX]));
            limitInfoEntity = limitInfoMap.get(key);

            if (limitInfoEntity == null) {
                continue;
            }
            commonLimitParam.setLimitId(limitInfoEntity.getLimitId());
            commonLimitParam.setGoodsIdList(requestLimitGoodsMap.get(key));
            commonLimitParam.setWid(wid);
            tmpUserGoodsLimitList = userGoodsLimitDao.listUserGoodsLimitByGoodsIdList(commonLimitParam);
            if (CollectionUtils.isNotEmpty(tmpUserGoodsLimitList)) {
                userGoodsLimitList.addAll(tmpUserGoodsLimitList);
            }
        }
        return userGoodsLimitList;
    }

    private  Map<String, List<GoodsLimitInfoEntity>>  getGoodsLimitMap(
            Map<String, Set<Long>> requestLimitGoodsMap,
            Map<String, LimitInfoEntity> limitInfoMap,
            Long pid
    ) {
        List<GoodsLimitInfoEntity> goodsLimitInfoList = new ArrayList<>();
        CommonLimitParam commonLimitParam = new CommonLimitParam();
        commonLimitParam.setPid(pid);

        LimitInfoEntity limitInfoEntity = null;
        List<GoodsLimitInfoEntity> tmpGoodsLimitInfoList = null;
        for (String key : requestLimitGoodsMap.keySet()) {

            limitInfoEntity = limitInfoMap.get(key);
            if (limitInfoEntity == null) {
                continue;
            }

            commonLimitParam.setLimitId(limitInfoEntity.getLimitId());
            commonLimitParam.setGoodsIdList(requestLimitGoodsMap.get(key));

            tmpGoodsLimitInfoList = goodsLimitInfoDao.listGoodsLimitByGoodsIdList(commonLimitParam);
            if (CollectionUtils.isEmpty(tmpGoodsLimitInfoList)) {
                continue;

            }
            if (CollectionUtils.isNotEmpty(tmpGoodsLimitInfoList)) {
                goodsLimitInfoList.addAll(tmpGoodsLimitInfoList);
            }
        }

        return TransformUtils.groupListToMapList(goodsLimitInfoList, new ListToMap<String, GoodsLimitInfoEntity, GoodsLimitInfoEntity>() {
            @Override
            public String key(GoodsLimitInfoEntity entity) {
                return MapKeyUtil.buildPidGoodsLimitNumMap(entity.getPid(), entity.getLimitId(), entity.getGoodsId());
            }

            @Override
            public GoodsLimitInfoEntity value(GoodsLimitInfoEntity o) {
                return o;
            }
        });
    }


    public static void main(String[] args) {
        // 每个线程查询5个
        int perNum = 50;
        int size = 100;

        int threadNum = size / perNum  + (size % perNum == 0 ? 0 : 1);
        System.out.println(threadNum);
    }

}
