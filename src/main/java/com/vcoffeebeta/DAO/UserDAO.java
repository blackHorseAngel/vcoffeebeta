package com.vcoffeebeta.DAO;

import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.util.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * 用户的DAO层
 * @author zhangshenming
 * @create 2022-09-27 00-05-19
 * @version 1.0
 */
@Repository
public interface UserDAO extends BaseDAO {
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
}
