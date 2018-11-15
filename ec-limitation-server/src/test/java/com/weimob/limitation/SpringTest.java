package com.weimob.limitation;


import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.service.LimitationQueryBizService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/spring/applicationContext-all.xml")
public class SpringTest {

    @Autowired
    private LimitationQueryBizService limitationQueryBizService;
    @Test
    public void testRpcException() {

        List<SkuLimitInfoEntity> queryList = new ArrayList<>();
        SkuLimitInfoEntity entity = new SkuLimitInfoEntity();
        entity.setPid(1122L);
        entity.setSkuId(102440122L);
        entity.setGoodsId(49980122L);
        entity.setLimitId(290722L);
        queryList.add(entity);

        entity.setPid(1122L);
        entity.setSkuId(102400122L);
        entity.setGoodsId(49940122L);
        entity.setLimitId(290722L);
        queryList.add(entity);


        limitationQueryBizService.getSkuLimitInfoList(queryList, 1L);



    }

}