package com.weimob.saas.ec.limitation.utils;

import com.weimob.saas.ec.common.exception.CommonErrorCode;
import com.weimob.saas.ec.common.response.HttpResponse;
import com.weimob.saas.ec.common.util.SoaUtil;
import com.weimob.soa.common.response.SoaResponse;

public class ResponseUtil {

	public static <T> HttpResponse<T> fail(SoaResponse soaResponse){
		HttpResponse<T> httpResponse = new HttpResponse<>();
		if(soaResponse!=null){
			httpResponse.setErrcode(soaResponse.getReturnCode());
			httpResponse.setErrmsg(soaResponse.getReturnMsg());
		}else {
			httpResponse.setErrcode(CommonErrorCode.FAIL.getErrorCode());
			httpResponse.setErrmsg(CommonErrorCode.FAIL.getErrorMsg());
		}
		return httpResponse;
	}
	
	public static <T, ErrT, R> HttpResponse<R> soa2http(SoaResponse<T, ErrT> soaResponse){
		HttpResponse<R> httpResponse = new HttpResponse<>();
		if(CommonErrorCode.SUCCESS.getErrorCode().equals(soaResponse.getReturnCode())){
			httpResponse.setErrcode("0");
		}else{
			httpResponse.setErrcode(soaResponse.getReturnCode());
		}
		httpResponse.setErrmsg(soaResponse.getReturnMsg());
		httpResponse.setMonitorTrackId(soaResponse.getMonitorTrackId());
		httpResponse.setGlobalTicket(SoaUtil.getGlobalTicket(soaResponse));
		return httpResponse;
	}
}
