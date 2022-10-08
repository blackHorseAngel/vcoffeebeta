package com.vcoffeebeta.service;

import com.vcoffeebeta.domain.Consume;

import java.util.List;

/**
 * 消费服务端接口
 * @author zhangshenming
 * @create 2022-10-03 23-17-32
 * @version 1.0
 */
public interface ConsumeService {
    /**
     * 新增一条消费记录
     * @author zhangshenming
     * @date 2022/10/3 23:18
     * @param consume
     * @return boolean
     */
    boolean insertConsume(Consume consume);
    /**
     * 查询全部消费记录
     * @author zhangshenming
     * @date 2022/10/3 23:19
     * @param
     * @return java.util.List<com.vcoffeebeta.domain.Consume>
     */
    List<Consume>queryAllConsumes();
    /**
     * 查询全部条数
     * @author zhangshenming
     * @date 2022/10/4 9:50
     * @param
     * @return int
     */
    int queryForAmount();
    /**
     * 条件查询消费记录信息
     * @author zhangshenming
     * @date 2022/10/4 17:22
     * @param consume
     * @return java.util.List<com.vcoffeebeta.domain.Consume>
     */
    List<Consume> queryForList(Consume consume);
    /**
     * 根据用户id查询当前用户的消费记录总数
     * @author zhangshenming
     * @date 2022/10/7 10:42
     * @param userId
     * @return int
     */
    int queryForAmountByUserId(long userId);
}
