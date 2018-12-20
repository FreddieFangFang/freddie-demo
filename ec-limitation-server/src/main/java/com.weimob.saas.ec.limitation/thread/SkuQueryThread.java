package com.weimob.saas.ec.limitation.thread;

import com.alibaba.fastjson.JSON;
import com.weimob.saas.ec.limitation.dao.SkuLimitInfoDao;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import com.weimob.saas.ec.limitation.model.CommonLimitParam;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SkuQueryThread implements Callable<List<SkuLimitInfoEntity>> {
    private static final Logger logger = LoggerFactory.getLogger(SkuQueryThread.class);


    private CommonLimitParam commonLimitParam;

    private SkuLimitInfoDao skuLimitInfoDao;

    public SkuQueryThread(CommonLimitParam commonLimitParam, SkuLimitInfoDao skuLimitInfoDao) {
        this.commonLimitParam = commonLimitParam;
        this.skuLimitInfoDao = skuLimitInfoDao;
    }

    @Override
    public List<SkuLimitInfoEntity> call() {
        List<SkuLimitInfoEntity> list = skuLimitInfoDao.listSkuLimitBySkuList(commonLimitParam);
        if( CollectionUtils.isEmpty(list)){
            logger.error("commonLimitParam: {0}, result : list is null ", JSON.toJSONString(commonLimitParam));
        }
        return list;
    }


    public static boolean isAllDone(List<Future<List<SkuLimitInfoEntity>>> taskResultList,List<SkuLimitInfoEntity> result) {
        if (CollectionUtils.isEmpty(taskResultList)) {
            return true;
        }
        boolean isAllDone = true;

        try {
            for (Future<List<SkuLimitInfoEntity>> task : taskResultList) {
                List<SkuLimitInfoEntity> list =  task.get();

                if (CollectionUtils.isNotEmpty(list)) {
                    result.addAll(list);
                }
            }
        } catch (Exception e) {
            logger.error("isAllDone", e);
            isAllDone = false;
        }
        return isAllDone;
    }

}
