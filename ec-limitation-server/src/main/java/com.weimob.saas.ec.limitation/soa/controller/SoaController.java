package com.weimob.saas.ec.limitation.soa.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.weimob.saas.ec.limitation.utils.SpringBeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class SoaController {

  /**
   * @description export的包路径
   */
  private static final String SERVICE_PACKAGE_NAME = "com.weimob.saas.ec.limitation.export";


  /**
   * @description 调用服务方法
   */
  @RequestMapping(value = "/service", consumes = {"application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8"},
          produces = {"application/x-www-form-urlencoded;charset=UTF-8", "application/json;charset=UTF-8"})
  @ResponseBody
  public Object service(HttpServletRequest request, @RequestBody String requestBody) {
    Object result = null;
    try {
      String serviceName = request.getParameter("serviceName");
      String methodName = request.getParameter("methodName");
      String parameterInput = request.getParameter("parameterInput");
      if (StringUtils.isEmpty(requestBody)) {
        parameterInput = requestBody.trim();
      } else {
        parameterInput = parameterInput.trim();
      }

      // 每个接口均存在InvokeParamVo参数，加入到参数列表
      // 1、单个参数{}，构造成[]
      // 2/多参数[]，直接将InvokeParamVo加在第一个参数
      List<String> paramList = new ArrayList<>();
      Object bean = SpringBeanUtils.getBean(serviceName);

      boolean isInvoked = false;
      //必须用getInterfaces，这样才能取到方法参数的泛型，JSON参数才会争取
      if (bean.getClass().isInterface()) {
        //获取接口或类的所有方法
        List<Method> methodList = new ArrayList<>();
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
              List<Object> params = new ArrayList<>();
              if (types.length == 1) {
                paramList.add(parameterInput);
              }

              //超过2个参数，将JSON参数转为List<String>，后续再逐个转为对应的对象
              if (types.length > 1) {
                List<String> tmepList = JSON.parseArray(parameterInput, String.class);
                paramList.addAll(tmepList);
              }

              //参数数量必须相同
              if (types.length == paramList.size()) {
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
            e.printStackTrace();
          }

          if (isInvoked) {
            break;
          }
        }
      } else {
        //非接口实现类
        for (Method method : bean.getClass().getDeclaredMethods()) {
          try {
            if (methodName.equals(method.getName())) {
              Type[] types = method.getGenericParameterTypes();
              List<Object> params = new ArrayList<>();
              if (types.length == 1) {
                paramList.add(parameterInput);
              }
              //超过2个参数，将JSON参数转为List<String>，后续再逐个转为对应的对象
              if (types.length > 1) {
                List<String> tmepList = JSON.parseArray(parameterInput, String.class);
                paramList.addAll(tmepList);
              }
              //参数数量必须相同
              if (types.length == paramList.size()) {
                for (int i = 0; i < types.length; i++) {
                  Object object;
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
            e.printStackTrace();
          }
          if (isInvoked) {
            break;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return JSON.toJSONString(result);
  }

  /**
   * @description 加载方法参数
   */
  @RequestMapping(value = "/loadParam")
  @ResponseBody
  public Object loadParam(HttpServletRequest request) {
    try {
      Object bean = SpringBeanUtils.getBean(request.getParameter("serviceName"));
      //必须用getInterfaces，这样才能取到方法参数的泛型，JSON参数才会争取
      for (Class<?> beanInterface : bean.getClass().getInterfaces()) {
        for (Method method : beanInterface.getMethods()) {
          if (request.getParameter("methodName").equals(method.getName())) {
            SerializeWriter out = new SerializeWriter();
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.config(SerializerFeature.QuoteFieldNames, true);
            serializer.setDateFormat("yyyy-MM-dd HH:mm:ss");
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
            serializer.config(SerializerFeature.WriteMapNullValue, true);
            serializer.write(method.getParameterTypes()[0].newInstance());
            String jsonString = out.toString();
            out.close();
            return jsonString;
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "{}";
  }

  /**
   * @description 获取服务list
   */
  @RequestMapping({"/getServiceList"})
  @ResponseBody
  public Object getServiceList() {
    String packageName = SERVICE_PACKAGE_NAME.replace('.', '/');
    URL url = Thread.currentThread().getContextClassLoader().getResource(packageName);
    if (url == null) {
      return Collections.emptyList();
    }

    String urlPath = null;
    try {
      urlPath = URLDecoder.decode(url.getFile(), "UTF-8");
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (urlPath == null) {
      return Collections.emptyList();
    }

    File dir = new File(urlPath);
    if (!dir.exists() || !dir.isDirectory()) {
      return Collections.emptyList();
    }
    File[] files = dir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.getName().endsWith(".class");
      }
    });

    List<String> serviceNameList = new ArrayList<String>();
    for (File file : files) {
      String fileName = file.getName();
      String className =
          fileName.substring(0, 1).toLowerCase() + fileName.substring(1).replace(".class", "");
      serviceNameList.add(className);
    }

    return serviceNameList;
  }

  /**
   * @description 获取服务方法list
   */
  @RequestMapping({"/getMethodList"})
  @ResponseBody
  public Object geMethodList(HttpServletRequest request) {
    ArrayList<String> methodList = new ArrayList<>();
    Object bean = SpringBeanUtils.getBean(request.getParameter("serviceName"));
    Class[] classes =
        bean.getClass().getName().contains("CGLIB$$") ? bean.getClass().getSuperclass()
            .getInterfaces() : bean.getClass().getInterfaces();
    int length = classes.length;
    for (int i = 0; i < length; ++i) {
      Method[] methods = classes[i].getDeclaredMethods();
      int len = methods.length;
      for (int n = 0; n < len; ++n) {
        Method m = methods[n];
        methodList.add(m.getName());
      }
    }
    Collections.sort(methodList);
    return methodList;
  }

}
