package com.vcoffeebeta.service;

import com.vcoffeebeta.domain.User;

/**
 * 登录业务层接口
 * @author zhangshenming
 * @version 1.0
 * @create 2022-01-11 19-55-58
 */
public interface UserService {
    public boolean isExist(String username);
    public User findByUserName(String username);
    public User loginByNameAndPassword(String username, String password);
}
