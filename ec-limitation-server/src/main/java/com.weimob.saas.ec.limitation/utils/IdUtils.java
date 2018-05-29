package com.weimob.saas.ec.limitation.utils;

import com.weimob.saas.ec.limitation.constant.GenIdConstant;
import com.weimob.saas.genId.service.IdService;

public class IdUtils {
	
	private static IdService idService;
	
	static{
		idService = (IdService)SpringBeanUtils.getBean("idService");
	}
	
	/**
	 * 获取限购全局id
	 * @param pid
	 * @return
	 */
	public static long getLimitId(long pid){
		long uniqueId = idService.getPrimaryKeyId(GenIdConstant.EC_LIMITATION);
		return Long.valueOf(uniqueId+getClassifyIdAppend(pid));
	}

	
	/**
	 * 根据pid获取id的后两位
	 * @param pid
	 * @return
	 */
	public static String getClassifyIdAppend(long pid){
		String num= String.valueOf((pid%100));
		if(num.length()<2){
			num="0"+num;
		}
		return num;
	}
}
