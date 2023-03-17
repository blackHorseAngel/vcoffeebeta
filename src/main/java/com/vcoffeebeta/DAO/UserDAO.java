package com.vcoffeebeta.DAO;

import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.domain.UserQuery;
import com.vcoffeebeta.util.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * 用户的DAO层
 * @author zhangshenming
 * @create 2022-09-27 00-05-19
 * @version 1.0
 */
@Repository
public interface UserDAO extends BaseDAO<User, UserQuery> {
    /**
     * 根据公司id查询全部用户数
     * @author zhangshenming 
     * @date 2022/9/30 17:25
     * @param companyId
     * @return int
     */
    int queryForAmountByCompanyId(long companyId);
    /**
     * 根据条件查询用户信息
     * @author zhangshenming
     * @date 2022/1/7 14:44
     * @param user
     * @return com.example.zam.vcoffee.domain.User
     */
    public User queryByNameAndPassword(User user);
    /**
     * 根据用户编号和公司id查找用户
     * @author zhangshenming
     * @date 2022/10/3 10:52
     * @param user
     * @return com.vcoffeebeta.domain.User
     */
    User findByUserNumberAndCompanyId(User user);
    /**
     * 更改密码
     * @author zhangshenming
     * @date 2022/10/8 20:28
     * @param user
     * @return int
     */
    int changePassword(User user);

    /**
     *
     * @return
     */
    User findByUserNameAndCompanyId(User user);
}
