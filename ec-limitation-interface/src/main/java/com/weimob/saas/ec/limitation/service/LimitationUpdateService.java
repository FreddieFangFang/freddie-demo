package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.model.request.BatchDeleteGoodsLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.DeleteLimitationRequestVo;
import com.weimob.saas.ec.limitation.model.request.LimitationInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.SaveGoodsLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.response.LimitationUpdateResponseVo;
import com.weimob.saas.ec.limitation.model.response.SaveGoodsLimitInfoResponseVo;
import com.weimob.soa.common.response.SoaResponse;

/**
 * @author lujialin
 * @description 限购更新service
 * @date 2018/5/29 10:22
 */
public interface LimitationUpdateService {
    /**
     * @param
     * @return
     * @title 保存限购主要信息
     * @author lujialin
     * @date 2018/5/29 10:45
     * @useScene 保存限购主要信息
     * @parameterExample {	"pid": 1000,	"storeIdList": [200],	"channelType": "0",	"source": "0,1",	"limitLevel": 0,	"bizId": 212121,	"bizType": 3,	"limitType": 0,	"limitNum": 3}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":"10000",	"monitorTrackId":"d18e87dc-cdde-400a-984d-94c2a34f699e",	"processResult":true,	"responseVo":{		"limitId":10000,		"status":true	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1527581559940"}
     */
    SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> saveLimitationInfo(LimitationInfoRequestVo requestVo);

    /**
     * @param
     * @return
     * @title 更新限购主要信息
     * @author lujialin
     * @date 2018/5/29 16:35
     * @useScene 更新限购主要信息
     * @parameterExample {	"pid": 1000,	"storeIdList": [200,300],	"channelType": "1",	"source": "0,1",	"limitLevel": 0,	"bizId": 210000,	"bizType": 3,	"limitType": 0,	"limitNum": 3}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":"11000",	"monitorTrackId":"d3f90f33-e1a0-43b8-9f94-b0a0d98ee1cd",	"processResult":true,	"responseVo":{		"limitId":11000,		"status":true	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1527588586231"}
     */
    SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> updateLimitationInfo(LimitationInfoRequestVo requestVo);

    /**
     * @param
     * @return
     * @title 删除限购信息
     * @author lujialin
     * @date 2018/5/30 16:36
     * @useScene 删除限购信息
     * @parameterExample
     * @returnExample
     */
    SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> deleteLimitationInfo(DeleteLimitationRequestVo requestVo);

    /**
     * @param
     * @return
     * @title 批量移除商品
     * @author lujialin
     * @date 2018/5/31 10:32
     * @useScene 批量移除商品
     * @parameterExample
     * @returnExample
     */
    SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> batchDeleteGoodsLimit(BatchDeleteGoodsLimitRequestVo requestVo);

    /**
     * @param
     * @return
     * @title 添加限购商品
     * @author lujialin
     * @date 2018/6/5 14:29
     * @useScene 添加限购商品
     * @parameterExample {	"bizId": 32783928,	"bizType": 30,	"pid": 1000,	"storeId": 200,	"limitLevel": 0,	"goodsLimitType": 0,	"goodsId": 32783928,	"goodsLimitNum": 12,	"channelType": 0,	"source": "0,1"}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"a479a80e-bd7a-4ec7-b5b0-3a29161d9f50",	"processResult":true,	"responseVo":{		"limitId":17000,		"status":true	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528180120353"}
     */
    SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> saveGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo);

    
    SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> updateGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo);
}
