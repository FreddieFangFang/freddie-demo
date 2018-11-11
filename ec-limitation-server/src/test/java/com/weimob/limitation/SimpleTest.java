package com.weimob.limitation;

import com.alibaba.fastjson.JSON;
import com.weimob.saas.ec.common.constant.ActivityTypeEnum;
import com.weimob.saas.ec.limitation.model.request.DeductUserLimitRequestVo;
import com.weimob.saas.ec.limitation.model.request.UpdateUserLimitVo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @Auther: fei.zheng
 * @Date: 2018/11/10 23:14
 * @Description:
 */
public class SimpleTest {

    public static final Logger LOGGER = LoggerFactory.getLogger(SimpleTest.class);

    @Test
    public void test() {
        UpdateUserLimitVo item = new UpdateUserLimitVo();
        item.setBizId(123L);
        item.setBizType(ActivityTypeEnum.DISCOUNT.getType());
        DeductUserLimitRequestVo req = new DeductUserLimitRequestVo();
        req.setUpdateUserLimitVoList(Arrays.asList(item));
        System.out.println(JSON.toJSONString(req));
    }
}
