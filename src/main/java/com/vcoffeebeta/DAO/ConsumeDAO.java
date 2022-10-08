package com.vcoffeebeta.DAO;

import com.vcoffeebeta.util.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * 消费DAO层
 * @author zhangshenming
 * @create 2022-10-03 23-22-16
 * @version 1.0
 */
@Repository
public interface ConsumeDAO extends BaseDAO {
    /**
     * 根据用户id查询当前用户的消费记录数
     * @author zhangshenming
     * @date 2022/10/7 10:43
     * @param userId
     * @return int
     */
    int queryForAmountByUserId(long userId);
}
