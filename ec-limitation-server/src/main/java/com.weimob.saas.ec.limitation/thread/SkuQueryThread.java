package com.weimob.saas.ec.limitation.thread;

import com.weimob.saas.ec.limitation.dao.SkuLimitInfoDao;
import com.weimob.saas.ec.limitation.entity.SkuLimitInfoEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class SkuQueryThread implements Callable<List<SkuLimitInfoEntity>> {
    private static final Log logger = LogFactory.getLog(SkuQueryThread.class);


    private List<SkuLimitInfoEntity> requestList;

    private SkuLimitInfoDao skuLimitInfoDao;


    public SkuQueryThread(List<SkuLimitInfoEntity> requestList, SkuLimitInfoDao skuLimitInfoDao) {
        this.requestList = requestList;
        this.skuLimitInfoDao = skuLimitInfoDao;
    }

    @Override
    public List<SkuLimitInfoEntity> call() {
        List<SkuLimitInfoEntity> list = skuLimitInfoDao.listSkuLimit(requestList);
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
