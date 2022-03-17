package com.vcoffeebeta.service;

import com.vcoffeebeta.domain.User;

/**
 * 登录业务层接口
 * @author zhangshenming
 * @version 1.0
 * @create 2022-01-11 19-55-58
 */
public interface UserService {
    /**
     * 查询用户名是否存在
     * @author zhangshenming
     * @date 2022/3/11 19:04
     * @param user
     * @return boolean
     */
    public boolean isExist(User user);
    /**
     * 使用用户名和用户密码登录
     * @author zhangshenming
     * @date 2022/3/11 19:04
     * @param user
     * @return com.vcoffeebeta.domain.User
     */
    public User loginByNameAndPassword(User user);
    /**
     * 新增用户
     * @author zhangshenming
     * @date 2022/3/11 19:05
     * @param user
     * @return int
     */
    public boolean insertUser(User user);
}
