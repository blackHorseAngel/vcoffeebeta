package com.vcoffeebeta.DAO;

import com.vcoffeebeta.domain.Account;
import com.vcoffeebeta.util.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * 账户DAO层
 * @author zhangshenming
 * @create 2022-10-02 22-14-07
 * @version 1.0
 */
@Repository
public interface AccountDAO extends BaseDAO {

    /**
     * 根据用户id查询账户信息
     * @author zhangshenming
     * @date 2022/10/2 23:24
     * @param userId
     * @return com.vcoffeebeta.domain.Account
     */
    Account findByUserId(long userId);
    /**
     * 根据用户id删除账户信息
     * @author zhangshenming
     * @date 2022/10/2 23:43
     * @param userId
     * @return int
     */
    int deleteByUserId(long userId);
}
