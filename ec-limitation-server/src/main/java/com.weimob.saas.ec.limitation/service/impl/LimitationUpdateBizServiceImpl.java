package com.weimob.saas.ec.limitation.service.impl;

import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.service.LimitationServiceImpl;
import com.weimob.saas.ec.limitation.service.LimitationUpdateBizService;
import com.weimob.saas.ec.limitation.utils.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lujialin
 * @description 限购更新service层实现类
 * @date 2018/5/29 11:16
 */
@Service(value = "limitationUpdateBizService")
public class LimitationUpdateBizServiceImpl implements LimitationUpdateBizService {

    @Autowired
    private LimitationServiceImpl limitationService;


    @Override
    public LimitationUpdateResponseVo saveLimitationInfo(LimitationInfoRequestVo requestVo) {
        /** 1 生成全局id */
        Long limitId = IdUtils.getLimitId(requestVo.getPid());
        /** 2 构建限购主表信息*/
        LimitInfoEntity limitInfoEntity = buildLimitInfoEntity(limitId, requestVo);
        /** 3 构建限购门店表信息*/
        List<LimitStoreRelationshipEntity> storeInfoList = buildStoreInfoList(limitId, requestVo);
        /** 4 保存数据库*/
        limitationService.saveLimitationInfo(limitInfoEntity, storeInfoList);

        return new LimitationUpdateResponseVo(limitId,true);
    }

    private List<LimitStoreRelationshipEntity> buildStoreInfoList(Long limitId, LimitationInfoRequestVo requestVo) {
        List<LimitStoreRelationshipEntity> storeInfoList = new ArrayList<>();
        for (Long storeId : requestVo.getStoreIdList()) {
            LimitStoreRelationshipEntity entity = new LimitStoreRelationshipEntity();
            entity.setLimitId(limitId);
            entity.setPid(requestVo.getPid());
            entity.setStoreId(storeId);
            storeInfoList.add(entity);
        }
        return storeInfoList;
    }

    private LimitInfoEntity buildLimitInfoEntity(Long limitId, LimitationInfoRequestVo requestVo) {
        LimitInfoEntity limitInfoEntity = new LimitInfoEntity();
        limitInfoEntity.setLimitId(limitId);
        limitInfoEntity.setBizId(requestVo.getBizId());
        limitInfoEntity.setBizType(requestVo.getBizType());
        limitInfoEntity.setChannelType(requestVo.getChannelType());
        limitInfoEntity.setLimitLevel(requestVo.getLimitLevel());
        limitInfoEntity.setLimitNum(requestVo.getLimitNum());
        limitInfoEntity.setLimitType(requestVo.getLimitType());
        limitInfoEntity.setPid(requestVo.getPid());
        limitInfoEntity.setSource(requestVo.getSource());
        return limitInfoEntity;
    }
}
