package com.weimob.saas.ec.limitation.service;

import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import com.weimob.saas.ec.limitation.model.request.DeductUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.ReverseUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.SaveUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.response.ReverseUserLimitResponseVo;
import com.weimob.saas.ec.limitation.model.response.UpdateUserLimitResponseVo;
import com.weimob.soa.common.response.SoaResponse;

/**
 * @author lujialin
 * @description 用户限购校验service
 * @date 2018/6/5 17:32
 */
public interface UserLimitUpdateService {


    /**
     * @param
     * @return
     * @title 下单限购校验
     * @author lujialin
     * @date 2018/6/8 9:46
     * @useScene 下单限购校验
     * @parameterExample {	"updateUserLimitVoList": [{			"bizId": 212122,			"bizType": 30,			"pid": 1000,			"storeId": 200,			"goodsId": 32783928,			"orderNo": 123233132323,			"goodsNum": 1,			"wid": 212123		},		{			"bizId": 212122,			"bizType": 30,			"pid": 1000,			"storeId": 200,			"goodsId": 32783920,			"orderNo": 123233132323,			"goodsNum": 1,			"wid": 212123		}	]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"811aa9c3-d8dc-4b6f-b435-5c73fd2d1df8",	"processResult":true,	"responseVo":{		"ticket":"811aa9c3-d8dc-4b6f-b435-5c73fd2d1df8"	},	"returnCode":"000000",	"returnMsg":null,	"successForMornitor":true,	"timestamp":"1528422331387"}
     */
    SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> saveUserLimit(SaveUserLimitRequestVo requestVo);

    /**
     *
     * @title 取消订单，减少限购记录
     * @author lujialin
     * @date 2018/6/9 10:48
     * @useScene
     * @parameterExample {	"updateUserLimitVoList": [{			"bizId": 2212122,			"bizType": 10,			"pid": 1000,			"storeId": 200,			"goodsId": 32700928,                        "skuId":21038219,			"orderNo": 123233132323,			"goodsNum": 1,			"wid": 212123		}	]}
     * @returnExample {	"errT":null,	"globalTicket":null,	"logBizData":null,	"monitorTrackId":"811aa9c3-d8dc-4b6f-b435-5c73fd2d1df8",	"processResult":true,	"responseVo":{		"ticket":"811aa9c3-d8dc-4b6f-b435-5c73fd2d1df8"	},	"returnCode":"000000",	"returnMsg":null,	"successForMornitor":true,	"timestamp":"1528422331387"}
     * @param
     * @return
     */
    SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> deductUserLimit(DeductUserLimitRequestVo requestVo);

    /**
     *
     * @title 回滚限购记录
     * @author lujialin
     * @date 2018/6/9 10:50
     * @useScene
     * @parameterExample {	"ticket": "bdd2572e-8cfe-4a73-8179-b6adec666ebe"}
     * @returnExample {	"errT": null,	"globalTicket": null,	"logBizData": null,	"monitorTrackId": "532df4a6-29c9-439d-a92c-1baa684c1850",	"processResult": true,	"responseVo": {		"status": true	},	"returnCode": "000000",	"returnMsg": "处理成功",	"successForMornitor": true,	"timestamp": "1528426520636"}
     * @param
     * @return
     */
    SoaResponse<ReverseUserLimitResponseVo, LimitationErrorCode> reverseUserLimit(ReverseUserLimitRequestVo requestVo);
}
