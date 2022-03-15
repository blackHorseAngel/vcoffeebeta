package com.vcoffeebeta.service;

import com.vcoffeebeta.DAO.LoginDAO;
import com.vcoffeebeta.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * loginService实现类
 * @author zhangshenming
 * @date 2022/1/11 22:10
 * @version 1.0
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private LoginDAO loginDAO;

    @Override
    public User loginByNameAndPassword(String username, String password) {
        log.info("进入loginByNameAndPassword的service层");
        return loginDAO.queryByNameAndPassword(username,password);
    }

    @Override
    public boolean  insertUser(User user) {
        log.info("进入insertUser");
        int num = loginDAO.insert(user);
        return num == 0 ? true:false;
    }

    @Override
    public boolean isExist(String username) {
        User user = findByUserName(username);
        return null != user;
    }

    @Override
    public User findByUserName(String username) {
        log.info("进入findByUserName");
        return loginDAO.findByUsername(username);
    }

}
