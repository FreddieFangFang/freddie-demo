package com.weimob.saas.ec.limitation.handler.limit;

import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.dao.UserLimitDao;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.UserLimitEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.handler.BaseHandler;
import com.weimob.saas.ec.limitation.handler.LimitBizHandler;
import com.weimob.saas.ec.limitation.model.LimitBo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import com.weimob.saas.ec.limitation.utils.LimitContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lujialin
 * @description 活动限购校验handler
 * @date 2018/6/6 15:32
 */
@Service(value = "activityLimitBizHandler")
public class ActivityLimitBizHandler extends BaseHandler implements LimitBizHandler {

    @Autowired
    private LimitInfoDao limitInfoDao;
    @Autowired
    private UserLimitDao userLimitDao;

    @Override
    public void doLimitHandler(List<UpdateUserLimitVo> vos) {
        Map<String, List<UpdateUserLimitVo>> orderGoodsQueryMap = new HashMap<>();
        Map<String, Integer> orderActivityValidMap = new HashMap();
        /** 1 处理入参数据 **/
        groupingOrderActivityRequestVoList(LimitContext.getLimitBo().getOrderGoodsLimitMap(), orderGoodsQueryMap, vos, orderActivityValidMap);
        /** 2 查询活动限购信息 **/
        List<LimitInfoEntity> limitInfoEntityList = limitInfoDao.queryOrderLimitInfoList(orderGoodsQueryMap.get(LIMIT_PREFIX_ACTIVITY));
        if (CollectionUtils.isEmpty(limitInfoEntityList)) {
            throw new LimitationBizException(LimitationErrorCode.LIMIT_ACTIVITY_IS_NULL);
        }
        /** 3 查询用户购买记录 **/
        List<UserLimitEntity> userLimitEntityList = userLimitDao.queryUserLimitInfoList(vos);
        /** 4 校验商品是否超出活动限购 **/
        validLimitation(limitInfoEntityList, null, userLimitEntityList,
                null, null, orderActivityValidMap);
        /** 5 封装更新数据库入参 **/
        updateUserLimitRecord(LimitContext.getLimitBo().getOrderGoodsLimitMap());

    }
}
