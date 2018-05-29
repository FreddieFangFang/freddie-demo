package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.soa.common.response.SoaResponse;

/**
 * @author lujialin
 * @description 限购更新service
 * @date 2018/5/29 10:22
 */
public interface LimitationUpdateService {
    /**
     *  
     * @title 保存限购主要信息
     * @author lujialin
     * @date 2018/5/29 10:45
     * @useScene 保存限购主要信息
     * @parameterExample {	"pid": 1000,	"storeIdList": [200],	"channelType": "0",	"source": "0,1",	"limitLevel": 0,	"bizId": 212121,	"bizType": 3,	"limitType": 0,	"limitNum": 3}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":"10000",	"monitorTrackId":"d18e87dc-cdde-400a-984d-94c2a34f699e",	"processResult":true,	"responseVo":{		"limitId":10000,		"status":true	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1527581559940"}
     * @param 
     * @return 
     */
    SoaResponse<LimitationUpdateResponseVo,LimitationCommonErrorVo> saveLimitationInfo(LimitationInfoRequestVo requestVo);

    /**
     *
     * @title 更新限购主要信息
     * @author lujialin
     * @date 2018/5/29 16:35
     * @useScene
     * @parameterExample
     * @returnExample
     * @param
     * @return
     */
    SoaResponse<LimitationUpdateResponseVo,LimitationCommonErrorVo> updateLimitationInfo(LimitationInfoRequestVo requestVo);
}
