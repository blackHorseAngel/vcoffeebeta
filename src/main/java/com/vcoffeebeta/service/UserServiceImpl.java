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
    public User loginByNameAndPassword(User user) {
        log.info("进入loginByNameAndPassword的service层");
        return loginDAO.queryByNameAndPassword(user);
    }

    @Override
    public boolean  insertUser(User user) {
        log.info("进入insertUser");
        int num = loginDAO.insert(user);
        return num == 1 ? true:false;
    }


    @Override
    public boolean isExist(User user) {
        User u = loginDAO.queryByNameAndPassword(user);
        if(u != null){
            return true;
        }
        return false;
    }

}
