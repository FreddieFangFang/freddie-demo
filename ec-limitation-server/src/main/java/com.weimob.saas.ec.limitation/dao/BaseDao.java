package com.weimob.saas.ec.limitation.dao;

import java.util.List;

/**
 * @author lujialin
 * @description 数据库操作基础dao
 * @date 2018/5/29 14:39
 */
public interface BaseDao<T> {
    /**
     *
     * @title 插入数据库
     * @author lujialin
     * @date 2018/5/29 14:41
     * @useScene
     * @parameterExample
     * @returnExample
     * @param
     * @return
     */
    Long insert(T entity);

    /**
     *
     * @title 批量插入数据库
     * @author lujialin
     * @date 2018/5/29 14:42
     * @useScene
     * @parameterExample
     * @returnExample
     * @param
     * @return
     */
    void batchInsert(List<T> entityList);
    
    /**
     *  
     * @title 
     * @author lujialin
     * @date 2018/5/29 17:03
     * @useScene 
     * @parameterExample 
     * @returnExample 
     * @param
     * @return 
     */
    Long update(T entity);
    
    /**
     *  
     * @title 
     * @author lujialin
     * @date 2018/5/29 17:11
     * @useScene 
     * @parameterExample 
     * @returnExample 
     * @param 
     * @return 
     */
    void delete(T entity);

}
