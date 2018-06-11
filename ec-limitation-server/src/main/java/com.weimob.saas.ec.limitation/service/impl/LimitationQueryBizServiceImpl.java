package com.weimob.saas.ec.limitation.service.impl;

import com.weimob.saas.ec.limitation.constant.LimitConstant;
import com.weimob.saas.ec.limitation.dao.GoodsLimitInfoDao;
import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.UserGoodsLimitDao;
import com.weimob.saas.ec.limitation.entity.GoodsLimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserGoodsLimitEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.LimitParam;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.*;
import com.weimob.saas.ec.limitation.service.LimitationQueryBizService;
import com.weimob.saas.ec.limitation.utils.MapKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @Override
    public GoodsLimitInfoListResponseVo queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo) {
        //构建查询主表limitId的入参
        List<LimitParam> queryLimitInfoList = new ArrayList<>();
        //查询商品限购信息的入参
        List<GoodsLimitInfoEntity> queryGoodsLimitList = new ArrayList<>();
        //查询用户购买商品记录
        List<UserGoodsLimitEntity> queryUserGoodsLimitList = new ArrayList<>();
        Map<String, Long> limitIdMap = new HashMap<>();
        Map<String, Integer> goodsLimitNumMap = new HashMap<>();
        Map<String, Integer> userGoodsLimitNumMap = new HashMap<>();
        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            LimitParam limitParam = new LimitParam();
            limitParam.setBizType(vo.getBizType());
            limitParam.setBizId(vo.getBizId());
            limitParam.setPid(vo.getPid());
            queryLimitInfoList.add(limitParam);
        }
        //查询限购主表
        List<LimitInfoEntity> limitInfoEntityList = limitInfoDao.queryLimitInfoList(queryLimitInfoList);
        for (LimitInfoEntity entity : limitInfoEntityList) {
            limitIdMap.put(MapKeyUtil.buildLimitIdMapKey(entity.getPid(), entity.getBizType(), entity.getBizId()), entity.getLimitId());
        }

        for (QueryGoodsLimitInfoListVo vo : requestVo.getGoodsDetailList()) {
            GoodsLimitInfoEntity goodsLimitInfoEntity = new GoodsLimitInfoEntity();
            goodsLimitInfoEntity.setPid(vo.getPid());
            goodsLimitInfoEntity.setStoreId(vo.getStoreId());
            goodsLimitInfoEntity.setGoodsId(vo.getGoodsId());
            goodsLimitInfoEntity.setLimitId(limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId())));
            queryGoodsLimitList.add(goodsLimitInfoEntity);

            UserGoodsLimitEntity userGoodsLimitEntity = new UserGoodsLimitEntity();
            userGoodsLimitEntity.setPid(vo.getPid());
            userGoodsLimitEntity.setStoreId(vo.getStoreId());
            userGoodsLimitEntity.setWid(vo.getWid());
            userGoodsLimitEntity.setGoodsId(vo.getGoodsId());
            userGoodsLimitEntity.setLimitId(limitIdMap.get(MapKeyUtil.buildLimitIdMapKey(vo.getPid(), vo.getBizType(), vo.getBizId())));
            queryUserGoodsLimitList.add(userGoodsLimitEntity);
        }
        //查询商品限购信息
        List<GoodsLimitInfoEntity> goodsLimitInfoList = goodsLimitInfoDao.queryGoodsLimitInfoList(queryGoodsLimitList);
        for (GoodsLimitInfoEntity entity : goodsLimitInfoList) {
            goodsLimitNumMap.put(MapKeyUtil.buildGoodsLimitNumMap(entity.getPid(), entity.getStoreId(), entity.getLimitId(), entity.getGoodsId()), entity.getLimitNum());
        }
        //查询用户商品下单记录
        List<UserGoodsLimitEntity> userGoodsLimitList = userGoodsLimitDao.queryUserGoodsLimitList(queryUserGoodsLimitList);
        for (UserGoodsLimitEntity entity : userGoodsLimitList) {
            userGoodsLimitNumMap.put(MapKeyUtil.buildUserGoodsLimitNumMap(entity.getPid(), entity.getStoreId(), entity.getWid(), entity.getLimitId(), entity.getGoodsId()), entity.getBuyNum());
        }


        return buildGoodsLimitInfoListResponseVo(requestVo, limitIdMap, goodsLimitNumMap, userGoodsLimitNumMap);
    }

    @Override
    public QueryGoodsLimitNumListResponseVo queryGoodsLimitNumList(QueryGoodsLimitNumRequestVo requestVo) {
        QueryGoodsLimitNumListResponseVo responseVo = new QueryGoodsLimitNumListResponseVo();
        List<QueryGoodsLimitNumVo> queryGoodsLimitNumList = new ArrayList<>();
        List<GoodsLimitInfoEntity> goodsLimitInfoEntityList = goodsLimitInfoDao.queryGoodsLimitNumList(requestVo.getQueryGoodslimitNumVoList());
        Map<String, Integer> goodsLimitNumMap = buildGoodsLimitMap(goodsLimitInfoEntityList);
        for (QueryGoodsLimitNumListVo request : requestVo.getQueryGoodslimitNumVoList()) {
            QueryGoodsLimitNumVo queryGoodsLimitNumVo = new QueryGoodsLimitNumVo();
            queryGoodsLimitNumVo.setPid(request.getPid());
            queryGoodsLimitNumVo.setGoodsId(request.getGoodsId());
            queryGoodsLimitNumVo.setStoreId(request.getStoreId());
            queryGoodsLimitNumVo.setGoodsLimitNum(goodsLimitNumMap.get(MapKeyUtil.buildPidStoreIdGoodsId(request.getPid(), request.getStoreId(), request.getGoodsId())));
            queryGoodsLimitNumList.add(queryGoodsLimitNumVo);
        }
        responseVo.setQueryGoodsLimitNumList(queryGoodsLimitNumList);
        return responseVo;
    }

    private Map<String, Integer> buildGoodsLimitMap(List<GoodsLimitInfoEntity> goodsLimitInfoEntityList) {
        Map<String, Integer> goodsLimitNumMap = new HashMap<>();
        for (GoodsLimitInfoEntity entity : goodsLimitInfoEntityList) {
            goodsLimitNumMap.put(MapKeyUtil.buildPidStoreIdGoodsId(entity.getPid(), entity.getStoreId(), entity.getGoodsId()), entity.getLimitNum());
        }
        return goodsLimitNumMap;
    }

    @Override
    public QueryActivityLimitInfoResponseVo queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo) {
        QueryActivityLimitInfoResponseVo responseVo = new QueryActivityLimitInfoResponseVo();
        LimitInfoEntity infoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));
        responseVo.setPid(requestVo.getPid());
        responseVo.setStoreId(requestVo.getStoreId());
        responseVo.setBizId(requestVo.getBizId());
        responseVo.setBizType(requestVo.getBizType());
        responseVo.setActivityLimitNum(infoEntity.getLimitNum());
        return responseVo;
    }

    private GoodsLimitInfoListResponseVo buildGoodsLimitInfoListResponseVo(GoodsLimitInfoListRequestVo requestVo, Map<String, Long> limitIdMap, Map<String, Integer> goodsLimitNumMap, Map<String, Integer> userGoodsLimitNumMap) {
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
            Integer goodsLimitNum = goodsLimitNumMap.get(MapKeyUtil.buildGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), limitId, vo.getGoodsId()));
            if (goodsLimitNum == null) {
                continue;
            }
            if (goodsLimitNum == LimitConstant.UNLIMITED_NUM) {
                goodsLimitInfoListVo.setLimitStatus(false);
            } else {
                goodsLimitInfoListVo.setLimitStatus(true);
            }
            Integer userBuyNum = userGoodsLimitNumMap.get(MapKeyUtil.buildUserGoodsLimitNumMap(vo.getPid(), vo.getStoreId(), vo.getWid(), limitId, vo.getGoodsId()));
            Integer canBuyNum = goodsLimitNum - (userBuyNum == null ? 0 : userBuyNum);
            //结算调用，且购买数量大于可以购买的数量，抛异常
            if (vo.getCheckLimit() && vo.getGoodsBuyNum() > canBuyNum) {
                throw new LimitationBizException(LimitationErrorCode.BEYOND_GOODS_LIMIT_NUM);
            }
            goodsLimitInfoListVo.setCanBuyNum(canBuyNum < 0 ? 0 : canBuyNum);
            goodsLimitInfoList.add(goodsLimitInfoListVo);
        }
        responseVo.setGoodsLimitInfoList(goodsLimitInfoList);
        return responseVo;
    }
}
