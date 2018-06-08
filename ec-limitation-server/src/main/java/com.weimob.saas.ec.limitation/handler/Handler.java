package com.weimob.saas.ec.limitation.handler;

import com.weimob.saas.ec.limitation.entity.LimitOrderChangeLogEntity;

import java.util.List;

/**
 * @author lujialin
 * @description handler
 * @date 2018/6/6 13:42
 */
public interface Handler<T> {

    /**
     *
     * @title 增加限购、减少限购业务处理
     * @author lujialin
     * @date 2018/6/6 13:44
     * @useScene
     * @parameterExample
     * @returnExample
     * @param
     * @return
     */
    String doHandler(List<T> vos);

    /**
     *
     * @title 回滚限购处理
     * @author lujialin
     * @date 2018/6/6 13:44
     * @useScene
     * @parameterExample
     * @returnExample
     * @param
     * @return
     */
    void doReverse(List<LimitOrderChangeLogEntity> logList);

}
