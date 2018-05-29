package com.weimob.saas.ec.limitation.utils;

import com.weimob.saas.ec.common.exception.BaseException;
import com.weimob.saas.ec.limitation.exception.LimitationBizException;
import com.weimob.saas.ec.limitation.exception.LimitationErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * 
 * @description 校验参数工具类
 * @author lujialin
 * @date 2017年11月10日 下午3:58:21
 */
public class VerifyParamUtils {
	public static final int LIST_SIZE_MAX_500 = 500;
	public static final int LIST_SIZE_MAX_200 = 200;
	public static final int LIST_SIZE_MAX_100 = 100;
	public static final int LIST_SIZE_MAX_50 = 50;
	
	public static final long TIME_SIZE_ONE_DAY = 60*60*24L; 
	/**
	 * @title 参数校验
	
	 */
	public static void checkParamBlank(Object... args){
		for(Object arg : args){
			if(isExistBlank(arg)){
				throw new LimitationBizException(LimitationErrorCode.REQUEST_PARAM_IS_NULL);
			}
		}
	}
	
	public static void checkParam(LimitationErrorCode errorCode,Object...objects){
		
		if (isNull(objects)){
			throw new BaseException(errorCode.getErrorCode(),errorCode.getErrorMsg());
		}
	}
	
	public static Boolean isNull(Object...objects){
		boolean isNull = false;
		if (null == objects){
			isNull = true;
		}
		for (int i = 0; !isNull && i < objects.length; i++)
		{
			if (null == objects[i])
			{
				isNull = true;
				break;
			}

			if (objects[i] instanceof String)
			{
				if ("".equals(objects[i]))
				{
					isNull = true;
					break;
				}
			}
		}
		return isNull;
	}

	public static boolean isExistBlank(Object... args) {
		for (Object arg : args) {
			if (arg == null || (arg.getClass().equals(String.class) && StringUtils.isBlank((String) arg))) {
				return true;
			}
		}
		return false;
	}
	
	public static void checkListParam(LimitationErrorCode errorCode,List<?> list) throws LimitationBizException {
		if (CollectionUtils.isEmpty(list)) {
			throw new LimitationBizException(errorCode);
		}
	}
	
	public static void checkListParam(List<?> list, int maxSize) throws LimitationBizException {
		if (CollectionUtils.isEmpty(list)) {
			throw new LimitationBizException(LimitationErrorCode.REQUEST_PARAM_IS_NULL);
		}
		if (list.size() > maxSize) {
			throw new LimitationBizException(LimitationErrorCode.FAIL_LIST_PARAM_TOO_LONG);
		}
	}
	
	public static void checkParamAllBlank(Object... args) throws LimitationBizException {
		if (isAllBlank(args)) {
			throw new LimitationBizException(LimitationErrorCode.REQUEST_PARAM_IS_NULL);
		}
	}
	
	public static boolean isAllBlank(Object... args) {
		for (Object arg : args) {
			if (arg != null) {
				return false;
			}
		}
		return true;
	}
	
}
