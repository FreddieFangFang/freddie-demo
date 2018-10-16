package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.common.LimitationCommonErrorVo;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.GoodsLimitInfoListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryActivityLimitInfoRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitDetailListRequestVo;
import com.weimob.saas.ec.limitation.model.request.QueryGoodsLimitNumRequestVo;
import com.weimob.saas.ec.limitation.model.response.*;
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
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"52fa3041-8aa8-4ab5-b700-d7eb5aaa6c95",	"processResult":true,	"responseVo":{		"goodsLimitInfoList":[			{				"alreadyBuyNum":0,				"appid":null,				"bizId":210000,				"bizStoreId":null,				"bizType":10,				"canBuyNum":1,				"goodsCanBuyNum":1,				"goodsId":3278392800,				"goodsLimitNum":8,				"invokeByOpenApi":false,				"latitude":null,				"limitStatus":true,				"longitude":null,				"operationSource":null,				"originalRefer":null,				"pid":1000,				"refer":null,				"sceneType":null,				"siteId":null,				"skuId":2193821900,				"skuLimitNum":1,				"storeId":200,				"wechatTemplateId":null,				"wid":212123			}		]	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1531798230534"}
     */
    SoaResponse<GoodsLimitInfoListResponseVo, LimitationCommonErrorVo> queryGoodsLimitInfoList(GoodsLimitInfoListRequestVo requestVo);

    /**
     * @param
     * @return
     * @title B端查询商品限购数
     * @author lujialin
     * @date 2018/6/8 14:32
     * @useScene
     * @parameterExample {	"queryGoodslimitNumVoList": [{		"goodsId": 32783954,		"pid": 1254,		"invokeByOpenApi": false,		"bizType": 10,		"storeId": 1470154,		"bizId": 212121,                "checkDeleteActivityGoods":1	}]}
     * @returnExample {	"errT": null,	"globalTicket": null,	"logBizData": null,	"monitorTrackId": "b999c0a5-a1ce-40b2-9875-c4689ed4c477",	"processResult": true,	"responseVo": {		"queryGoodsLimitNumList": [{			"bizId": 74410254,			"bizType": 30,			"goodsId": 74410254,			"goodsLimitNum": 5,			"pid": 1254,			"pidGoodsLimitNum": null,			"skuLimitInfoList": [{				"skuId": 2132123,				"skuLimitNum": 2,				"alreadySoldNum": 1,				"skuLimitType": 0			}],			"storeId": 1470154		}]	},	"returnCode": "000000",	"returnMsg": "处理成功",	"successForMornitor": true,	"timestamp": "1531207735181"}
     */
    SoaResponse<QueryGoodsLimitNumListResponseVo, LimitationErrorCode> queryGoodsLimitNumList(QueryGoodsLimitNumRequestVo requestVo);

    /**
     * @param
     * @return
     * @title B端查询活动限购数量及可售数量
     * @author lujialin
     * @date 2018/6/8 14:50
     * @useScene
     * @parameterExample {		"bizId":2212122,		"bizType":10,		"pid":1000,		"storeId":200	}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"7116bc05-54e7-4f92-ba6d-07386f4ccc78",	"processResult":true,	"responseVo":{		"activityLimitNum":3,		"appid":null,		"bizId":2212122,		"bizType":10,		"invokeByOpenApi":false,		"operationSource":null,		"originalRefer":null,		"pid":1000,		"refer":null,		"sceneType":null,		"siteId":null,		"storeId":200,		"wechatTemplateId":null,		"wid":null	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1528441104934"}
     */
    SoaResponse<QueryActivityLimitInfoResponseVo, LimitationErrorCode> queryActivityLimitInfo(QueryActivityLimitInfoRequestVo requestVo);

    /**
     *  
     * @title 商详页、活动专题页到goods级别
     * @author lujialin
     * @date 2018/7/24 10:22
     * @useScene 商详页、活动专题页到goods级别
     * @parameterExample {	"goodsList": [{		"pid": 1254,		"wid": 212123,		"bizId": 876787654,		"bizType": 30,		"goodsId": 876787654	}]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"df03b568-465f-48a3-a4fe-c63a54303f1f",	"processResult":true,	"responseVo":{		"queryGoodsLimitDetailVoList":[			{				"bizId":876787654,				"bizType":30,				"goodsCanBuyNum":3,				"goodsId":876787654,				"goodsLimit":true,				"pid":1254,				"realSoldNum":7,				"skuLimitInfoList":[					{						"alreadySoldNum":2,						"canBuySkuNum":3,						"skuId":8086778754,						"skuLimitNum":5,						"skuLimitType":null					},					{						"alreadySoldNum":3,						"canBuySkuNum":4,						"skuId":8886778754,						"skuLimitNum":7,						"skuLimitType":null					}				],				"storeId":null			}		]	},	"returnCode":"000000",	"returnMsg":"处理成功",	"successForMornitor":true,	"timestamp":"1532575428764"}
     * @param 
     * @return 
     */
    SoaResponse<QueryGoodsLimitDetailListResponseVo,LimitationCommonErrorVo> queryGoodsLimitDetailList(QueryGoodsLimitDetailListRequestVo requestVo);

    /***
     * @title 批量查询活动限购信息
     * @author qi.he
     * @date 2018/10/16 0016 14:54
     * @useScene
     * @parameterExample
     * @returnExample
     * @param [requestVo]
     * @return com.weimob.soa.common.response.SoaResponse<com.weimob.saas.ec.limitation.model.response.QueryActivityLimitInfoListResponseVo,com.weimob.saas.ec.limitation.exception.LimitationErrorCode>
     */
    SoaResponse<QueryActivityLimitInfoListResponseVo, LimitationErrorCode> queryActivityLimitInfoList(QueryActivityLimitInfoListRequestVo requestVo);
}
