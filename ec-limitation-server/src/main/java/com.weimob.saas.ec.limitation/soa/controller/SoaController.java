package com.weimob.saas.ec.limitation.soa.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.weimob.saas.ec.limitation.soa.utils.InterfaceParametersUtils;
import com.weimob.saas.ec.limitation.utils.SpringBeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Controller
public class SoaController {

	/** http相关参数 **/
	private static final String DEFAULT_CHARSET = "UTF-8";
	private static final String DEFAULT_CONTENT_TYPE_NAME = "content-typ";
	private static final String DEFAULT_CONTENT_TYPE_VALUE = "application/json;charset=UTF-8";
	
	
	private static Logger logger = Logger.getLogger(SoaController.class);

	@RequestMapping(value = "/service")
	public void service(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBodyStr) {
		Object result = null;
		try {
			String serviceName = request.getParameter("serviceName");
			String methodName = request.getParameter("methodName");

			String parameterInput = null;
			if (StringUtils.isNotBlank(requestBodyStr)) {
				parameterInput = requestBodyStr;
			} else {
				parameterInput = request.getParameter("paramterInput").trim();
			}


			// 每个接口均存在InvokeParamVo参数，加入到参数列表
			// 1、单个参数{}，构造成[]
			// 2/多参数[]，直接将InvokeParamVo加在第一个参数
			List<String> paramList = new ArrayList<String>();
			Object bean = SpringBeanUtils.getBean(serviceName);
			
			boolean isInvoked = false;
			// 必须用getInterfaces，这样才能取到方法参数的泛型，JSON参数才会争取
			if (bean.getClass().isInterface()) {
				// 获取接口或类的所有方法
				List<Method> methodList = new ArrayList<Method>();
				for (Class<?> beanInterface : bean.getClass().getInterfaces()) {
					for (Method method : beanInterface.getMethods()) {
						if (!methodList.contains(method)) {
							methodList.add(method);
						}
					}
				}
				//cglib动态代理
				for (Method method : bean.getClass().getDeclaredMethods()) {
					if (!methodList.contains(method)) {
						methodList.add(method);
					}
				}
				
				for (Method method : methodList) {
					try {
						if (methodName.equals(method.getName())) {
							Type[] types = method.getGenericParameterTypes();
							List<Object> params = new ArrayList<Object>();
							if (types.length == 1) {
								paramList.add(parameterInput);
							}
							if (types.length > 1) {// 超过2个参数，将JSON参数转为List<String>，后续再逐个转为对应的对象
								List<String> tmepList = JSON.parseArray(parameterInput, String.class);
								paramList.addAll(tmepList);
							}
							if (types.length == paramList.size()) {// 参数数量必须相同
								for (int i = 0; i < types.length; i++) {
									if (types[i] == String.class) {
										params.add(paramList.get(i));
									} else {
										params.add(JSON.parseObject(paramList.get(i), types[i]));
									}
								}
								isInvoked = true;
								result = method.invoke(bean, params.toArray());
								break;
							}
						}
					} catch (Exception e) {
						logger.error("调用服务方法通信异常", e);
					}
					if (isInvoked) {
						break;
					}
				}
			} else {
				//  非接口实现类
				for (Method method : bean.getClass().getDeclaredMethods()) {
					try {
						if (methodName.equals(method.getName())) {
							Type[] types = method.getGenericParameterTypes();
							List<Object> params = new ArrayList<Object>();
							if (types.length == 1) {
								paramList.add(parameterInput);
							}
							if (types.length > 1) {// 超过2个参数，将JSON参数转为List<String>，后续再逐个转为对应的对象
								List<String> tmepList = JSON.parseArray(parameterInput, String.class);
								paramList.addAll(tmepList);
							}
							if (types.length == paramList.size()) {// 参数数量必须相同
								for (int i = 0; i < types.length; i++) {
									Object object = null;
									try {
										object = JSON.parseObject(paramList.get(i), types[i]);
									} catch (Exception e) {
										object = paramList.get(i);
									}
									params.add(object);
								}
								isInvoked = true;
								result = method.invoke(bean, params.toArray());
								break;
							}
						}
					} catch (Exception e) {
						logger.error("调用服务方法通信异常", e);
					}
					if (isInvoked) {
						break;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handleResponse(response, result);
		}
	}
	
	
	
	@RequestMapping(value = "/load")
	public void load(HttpServletRequest request, HttpServletResponse response) {
		Object result = null;
		try {
			String serviceName = request.getParameter("serviceName");
		    String methodName = request.getParameter("methodName");
		    
		    Object bean = SpringBeanUtils.getBean(serviceName);
		    //必须用getInterfaces，这样才能取到方法参数的泛型，JSON参数才会争取
		    for (Method method : bean.getClass().getDeclaredMethods()) {
		          try{    
		                if(methodName.equals(method.getName())){
		                    result = InterfaceParametersUtils.getInterfaceInputJsonString(bean.getClass().getName(),method.getName());
		                    break;
		                }
		          }catch(Exception e){
		              e.printStackTrace();
		          }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			handleResponse(response, result);
		}
	}

	/**
	 * 处理输出结果
	 * 
	 * @param response
	 * @param bizResponse
	 */
	private void handleResponse(HttpServletResponse response, Object result) {
		response.setCharacterEncoding(DEFAULT_CHARSET);
		response.setHeader(DEFAULT_CONTENT_TYPE_NAME, DEFAULT_CONTENT_TYPE_VALUE);
		PrintWriter pw = null;
		try {
			String data ;
			pw = response.getWriter();
			if(result instanceof String){
				data = (String) result;
			}else{
				data = JSON.toJSONString(result,SerializerFeature.WriteMapNullValue,SerializerFeature.PrettyFormat);
			}
			
			if((data).startsWith("[")){
				data = (data).substring(1);
			}
			if((data).endsWith("]")){
				data = data.substring(0, data.length() - 1);
			}
			
			pw.print(data);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.flush();
				pw.close();
		}
	}

}
