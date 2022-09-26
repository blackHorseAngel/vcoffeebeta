package com.vcoffeebeta.util;

import java.util.List;

/**
 * 基础DAO
 * @author zhangshenming
 * @date 2022/1/11 22:21
 * @version 1.0
 */
public interface BaseDAO<E,Q> {
    /**
     * 新增一条数据
     * @author zhangshenming
     * @date 2022/1/9 15:49
     * @param entity
     * @return int
     */
    public int insert(E entity);
    /**
     * 根据id删除一条数据
     * @author zhangshenming
     * @date 2022/1/9 15:50
     * @param id
     * @return void
     */
    public int deleteById(long id);
    /**
     * 修改一条数据
     * @author zhangshenming
     * @date 2022/1/9 15:51
     * @param entity
     * @return int
     */
    public int update(E entity);
    /**
     * 查询分页
     * @author zhangshenming
     * @date 2022/1/9 15:54
     * @param query
     * @return java.util.List<E>
     */
    public List<E> queryForList(Q query);
    /**
     * 根据id查询单个对象数据
     * @author zhangshenming
     * @date 2022/1/9 15:54
     * @param id
     * @return E
     */
    public E findById(long id);
    /**
     * 查询全部数据
     * @author zhangshenming
     * @date 2022/1/9 16:08
     * @param
     * @return java.util.List<E>
     */
    public List<E>findAll();
    /**
     * 查询全部数据条数
     * @author zhangshenming
     * @date 2022/9/25 21:54
     * @param
     * @return int
     */
    public int queryForAmount();
}
