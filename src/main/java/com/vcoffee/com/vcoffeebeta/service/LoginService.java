package com.vcoffee.com.vcoffeebeta.service;

import com.vcoffee.com.vcoffeebeta.domain.User;

/**
 * 登录业务层接口
 * @author zhangshenming
 * @version 1.0
 * @create 2022-01-11 19-55-58
 */
public interface LoginService {
    public User loginByNameAndPassword(String name, String password);
}
