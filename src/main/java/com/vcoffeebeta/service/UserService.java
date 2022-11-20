package com.vcoffeebeta.service;

import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.domain.UserQuery;

import java.util.List;

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
    /**
     * 查询全部用户信息
     * @author zhangshenming
     * @date 2022/9/27 0:03
     * @param
     * @return java.util.List<com.vcoffeebeta.domain.User>
     */
    List<User> findAllUsers(UserQuery userQuery);
    /**
     * 根据companyId查询用户数
     * @author zhangshenming
     * @date 2022/9/30 17:23
     * @param companyId
     * @return int
     */
    int queryForAmountByCompanyId(long companyId);
    /**
     * 更新用户信息
     * @author zhangshenming
     * @date 2022/9/30 20:56
     * @param user
     * @return boolean
     */
    boolean updateUser(User user);
    /**
     * 通过id查找用户信息
     * @author zhangshenming
     * @date 2022/9/30 21:21
     * @param id
     * @return com.vcoffeebeta.domain.User
     */
    User findById(long id);
    /**
     * 删除用户信息
     * @author zhangshenming
     * @date 2022/10/1 0:15
     * @param id
     * @return boolean
     */
    boolean deleteUser(long id);
    /**
     * 批量删除用户
     * @author zhangshenming
     * @date 2022/10/1 16:23
     * @param ids
     * @return boolean
     */
    boolean batchDeleteUser(List<Long> ids);
    /**
     * 条件查询用户信息
     * @author zhangshenming
     * @date 2022/10/1 22:41
     * @param userQuery
     * @return java.util.List<com.vcoffeebeta.domain.User>
     */
    List<User> queryForList(UserQuery userQuery);
    /**
     * 查询用户总数
     * @author zhangshenming
     * @date 2022/10/2 17:45
     * @param
     * @return int
     */
    int queryForAmount(UserQuery userQuery);
    /**
     * 更改密码
     * @author zhangshenming
     * @date 2022/10/8 20:26
     * @param user
     * @return boolean
     */
    boolean changePassword(User user);
}
