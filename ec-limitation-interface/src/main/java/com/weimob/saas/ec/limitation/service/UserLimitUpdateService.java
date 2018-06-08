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


    SoaResponse<UpdateUserLimitResponseVo, LimitationErrorCode> deductUserLimit(DeductUserLimitRequestVo requestVo);


    SoaResponse<ReverseUserLimitResponseVo, LimitationErrorCode> reverseUserLimit(ReverseUserLimitRequestVo requestVo);
}
