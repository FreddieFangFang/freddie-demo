package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumRequestVo;
import com.weimob.saas.ec.limitation.model.response.GoodsLimitInfoListResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoResponseVo;
import com.weimob.saas.ec.limitation.model.response.QueryGoodsLimitNumListResponseVo;
import com.weimob.soa.common.response.SoaResponse;

/**
 * @author lujialin
 * @description 限购查询service
 * @date 2018/5/29 10:21
 */
public interface LimitationQueryService {

    /**
     * @param
     * @return
     * @title 查询商品限购信息
     * @author lujialin
     * @date 2018/6/4 17:31
     * @useScene 结算、商详、活动页
     * @parameterExample {	"goodsDetailList": [{			"pid": 1000,			"storeId": 200,			"wid": 212123,			"bizId": 212121,			"bizType": 3,			"limitType": 0,			"limitLevel": 1,			"goodsId": 221312,			"checkLimit": false		},		{			"pid": 1000,			"storeId": 200,			"wid": 212123,			"bizId": 210000,			"bizType": 3,			"limitType": 0,			"limitLevel": 1,			"goodsId": 212121,			"checkLimit": true,                        "goodsBuyNum":1		}	]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"a3913d87-c7cd-4491-aa76-106819a5596b",	"processResult":true,	"responseVo":{		"goodsLimitInfoList":[			{				"appid":null,				"bizId":212121,				"bizType":3,				"canBuyNum":3,				"goodsId":221312,				"invokeByOpenApi":false,				"limitStatus":true,				"operationSource":null,				"originalRefer":null,				"pid":1000,				"refer":null,				"sceneType":null,				"siteId":null,				"skuId":null,				"storeId":200,				"wechatTemplateId":null,				"wid":null			},			{				"appid":null,				"bizId":210000,				"bizType":3,				"canBuyNum":1,				"goodsId":212121,				"invokeByOpenApi":false,				"limitStatus":true,				"operationSource":null,				"originalRefer":null,				"pid":1000,				"refer":null,				"sceneType":null,				"siteId":null,				"skuId":null,				"storeId":200,				"wechatTemplateId":null,				"wid":null			}		]	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528102472790"}
     */
    SoaResponse<GoodsLimitInfoListResponseVo, LimitationCommonErrorVo> queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo);

    /**
     * @param
     * @return
     * @title B端查询商品限购数
     * @author lujialin
     * @date 2018/6/8 14:32
     * @useScene
     * @parameterExample {	"queryGoodslimitNumVoList": [{		"bizId": 2212122,		"bizType": 30,		"goodsId": 32700928,		"pid": 1000,		"storeId": 200	}]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"c8fbaef6-bfbd-468d-9d5a-c7290e196591",	"processResult":true,	"responseVo":{		"queryGoodsLimitNumList":[			{				"goodsId":32700928,				"goodsLimitNum":12,				"pid":1000,				"skuLimitInfoList":null,				"storeId":200			}		]	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528706366432"}
     */
    SoaResponse<QueryGoodsLimitNumListResponseVo, LimitationErrorCode> queryGoodsLimitNumList(QueryGoodsLimitNumRequestVo requestVo);

    /**
     * @param
     * @return
     * @title B端查询活动限购数量
     * @author lujialin
     * @date 2018/6/8 14:50
     * @useScene
     * @parameterExample {		"bizId":2212122,		"bizType":10,		"pid":1000,		"storeId":200	}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"7116bc05-54e7-4f92-ba6d-07386f4ccc78",	"processResult":true,	"responseVo":{		"activityLimitNum":3,		"appid":null,		"bizId":2212122,		"bizType":10,		"invokeByOpenApi":false,		"operationSource":null,		"originalRefer":null,		"pid":1000,		"refer":null,		"sceneType":null,		"siteId":null,		"storeId":200,		"wechatTemplateId":null,		"wid":null	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528441104934"}
     */
    SoaResponse<QueryActivityLimitInfoResponseVo, LimitationErrorCode> queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo);
}
