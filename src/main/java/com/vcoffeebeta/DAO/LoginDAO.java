package com.vcoffeebeta.DAO;

import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.util.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * 登录DAO
 * @author zhangshenming
 * @date 2022/1/11 22:20
 * @version 1.0
 */
@Repository
public interface LoginDAO extends BaseDAO {
    /**
     * 根据条件查询用户信息
     * @author zhangshenming
     * @date 2022/1/7 14:44
     * @param user
     * @return com.example.zam.vcoffee.domain.User
     */
    public User queryByNameAndPassword(User user);

}
