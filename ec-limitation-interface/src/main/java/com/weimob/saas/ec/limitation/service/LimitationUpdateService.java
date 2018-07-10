package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.model.request.*;
import com.weimob.saas.ec.limitation.model.response.DeleteDiscountUserLimitInfoResponseVo;
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
     * @parameterExample {	"deleteGoodsLimitVoList": [{		"bizId": 212122,		"bizType": 10,		"pid": 1000,		"storeId": 200,		"goodsId": 32783928	}]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":"212122",	"monitorTrackId":"9ec48ee7-9023-4d10-8992-1d7c0adafd45",	"processResult":true,	"responseVo":{		"limitId":212122,		"status":true	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528181547697"}
     */
    SoaResponse<LimitationUpdateResponseVo, LimitationCommonErrorVo> batchDeleteGoodsLimit(BatchDeleteGoodsLimitRequestVo requestVo);

    /**
     * @param
     * @return
     * @title 添加限购商品
     * @author lujialin
     * @date 2018/6/5 14:29
     * @useScene 添加限购商品
     * @parameterExample {	"goodsList": [{		"goodsId": 5072010200,		"channelType": "0,1",		"limitLevel": 1,		"pid": 100000068000,		"goodsLimitType": 0,		"invokeByOpenApi": false,		"source": "0,1",		"goodsLimitNum": 3,		"pidGoodsLimitNum": 0,		"bizType": 30,		"storeId": 2401000,		"bizId": 5072010200	}]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"a479a80e-bd7a-4ec7-b5b0-3a29161d9f50",	"processResult":true,	"responseVo":{		"limitId":17000,		"status":true	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528180120353"}
     */
    SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> saveGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo);

    /**
     *
     * @title 更改限购商品
     * @author lujialin
     * @date 2018/6/5 18:13
     * @useScene 更改限购商品
     * @parameterExample {	"goodsList": [{		"goodsId": 5072010200,		"channelType": "0,1",		"limitLevel": 1,		"pid": 100000068000,		"goodsLimitType": 0,		"invokeByOpenApi": false,		"source": "0,1",		"goodsLimitNum": 3,		"pidGoodsLimitNum": 0,		"bizType": 30,		"storeId": 2401000,		"bizId": 5072010200	}]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"030bceca-2cbb-4e56-841c-16b432458e44",	"processResult":true,	"responseVo":{		"limitId":18000,		"status":true	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528188496257"}
     * @param
     * @return
     */
    SoaResponse<SaveGoodsLimitInfoResponseVo, LimitationCommonErrorVo> updateGoodsLimitInfo(SaveGoodsLimitInfoRequestVo requestVo);
    
    /**
     *  
     * @title 周期性清除限时折扣用户购买记录
     * @author lujialin
     * @date 2018/6/29 14:37
     * @useScene 周期性清除限时折扣用户购买记录
     * @parameterExample 
     * @returnExample 
     * @param 
     * @return 
     */
    SoaResponse<DeleteDiscountUserLimitInfoResponseVo,LimitationCommonErrorVo> deleteDiscountUserLimitInfo(DeleteDiscountUserLimitInfoRequestVo requestVo);
}
