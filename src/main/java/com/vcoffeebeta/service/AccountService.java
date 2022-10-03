package com.vcoffeebeta.service;

import com.vcoffeebeta.domain.Account;

/**
 * 账户服务层接口
 * @author zhangshenming
 * @date 2022/10/2 22:11
 * @version 1.0
 */
public interface AccountService {
    /**
     * 新增账户
     * @author zhangshenming
     * @date 2022/10/2 23:14
     * @param account
     * @return boolean
     */
    boolean insertAccount(Account account);
    /**
     * 查询账户信息
     * @author zhangshenming
     * @date 2022/10/2 23:15
     * @param userId
     * @return com.vcoffeebeta.domain.Account
     */
    Account findByUserId(long userId);
    /**
     * 根据用户id删除账户信息
     * @author zhangshenming
     * @date 2022/10/2 23:45
     * @param userId
     * @return int
     */
    boolean deleteByUserId(long userId);
}
