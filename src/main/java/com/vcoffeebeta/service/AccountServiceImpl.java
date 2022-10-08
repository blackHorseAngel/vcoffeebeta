package com.vcoffeebeta.service;

import com.vcoffeebeta.DAO.AccountDAO;
import com.vcoffeebeta.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 服务层实现类
 * @author zhangshenming
 * @date 2022/10/2 22:12
 * @version 1.0
 */
@Service
public class AccountServiceImpl implements AccountService{

    @Autowired
    private AccountDAO accountDAO;


    @Override
    public boolean insertAccount(Account account) {
        int num = accountDAO.insert(account);
        return num > 0 ? true : false;
    }

    @Override
    public Account findByUserId(long userId) {
        return (Account) accountDAO.findByUserId(userId);
    }

    @Override
    public boolean updateAccount(Account account) {
        int num = accountDAO.update(account);
        return num > 0 ? true : false;
    }

    @Override
    public boolean deleteByUserId(long userId) {
        int num = accountDAO.deleteByUserId(userId);
        return num > 0 ? true : false;
    }
}
