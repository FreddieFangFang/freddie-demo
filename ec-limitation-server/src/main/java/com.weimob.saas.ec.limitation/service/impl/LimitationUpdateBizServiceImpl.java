package com.weimob.saas.ec.limitation.service.impl;

import com.weimob.saas.ec.limitation.dao.LimitInfoDao;
import com.weimob.saas.ec.limitation.entity.LimitInfoEntity;
import com.weimob.saas.ec.limitation.entity.LimitStoreRelationshipEntity;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.LimitParam;
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
    @Autowired
    private LimitInfoDao limitInfoDao;


    @Override
    public LimitationUpdateResponseVo saveLimitationInfo(LimitationInfoRequestVo requestVo) {
        /** 1 生成全局id */
        Long limitId = IdUtils.getLimitId(requestVo.getPid());
        requestVo.setLimitId(limitId);
        /** 2 构建限购主表信息*/
        LimitInfoEntity limitInfoEntity = buildLimitInfoEntity(requestVo);
        /** 3 构建限购门店表信息*/
        List<LimitStoreRelationshipEntity> storeInfoList = buildStoreInfoList(requestVo);
        /** 4 保存数据库*/
        limitationService.saveLimitationInfo(limitInfoEntity, storeInfoList);

        return new LimitationUpdateResponseVo(limitId, true);
    }

    @Override
    public LimitationUpdateResponseVo updateLimitationInfo(LimitationInfoRequestVo requestVo) {
        /** 1 查询限购主表信息*/
        LimitInfoEntity oldLimitInfoEntity = limitInfoDao.selectByLimitParam(new LimitParam(requestVo.getPid(), requestVo.getBizId(), requestVo.getBizType()));

        if (oldLimitInfoEntity == null) {
            throw new LimitationBizException(LimitationErrorCode.LIMITATION_IS_NULL);
        }
        requestVo.setLimitId(oldLimitInfoEntity.getLimitId());
        /** 2 构建限购主表更新信息*/
        LimitInfoEntity limitInfoEntity = buildLimitInfoEntity(requestVo);
        /** 3 构建限购门店表信息*/
        List<LimitStoreRelationshipEntity> storeInfoList = buildStoreInfoList(requestVo);
        /** 4 更新数据库*/
        limitationService.updateLimitationInfo(limitInfoEntity, storeInfoList);

        return new LimitationUpdateResponseVo(oldLimitInfoEntity.getLimitId(), true);
    }

    private List<LimitStoreRelationshipEntity> buildStoreInfoList(LimitationInfoRequestVo requestVo) {
        List<LimitStoreRelationshipEntity> storeInfoList = new ArrayList<>();
        for (Long storeId : requestVo.getStoreIdList()) {
            LimitStoreRelationshipEntity entity = new LimitStoreRelationshipEntity();
            entity.setLimitId(requestVo.getLimitId());
            entity.setPid(requestVo.getPid());
            entity.setStoreId(storeId);
            storeInfoList.add(entity);
        }
        return storeInfoList;
    }

    private LimitInfoEntity buildLimitInfoEntity(LimitationInfoRequestVo requestVo) {
        LimitInfoEntity limitInfoEntity = new LimitInfoEntity();
        limitInfoEntity.setLimitId(requestVo.getLimitId());
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
