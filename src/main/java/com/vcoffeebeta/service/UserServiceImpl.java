package com.vcoffeebeta.service;

import com.vcoffeebeta.DAO.UserDAO;
import com.vcoffeebeta.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * loginService实现类
 * @author zhangshenming
 * @date 2022/1/11 22:10
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {


    @Autowired
    private UserDAO userDAO;

    @Override
    public User loginByNameAndPassword(User user) {
        return userDAO.queryByNameAndPassword(user);
    }

    @Override
    public boolean  insertUser(User user) {
        int num = userDAO.insert(user);
        return num >= 0 ? true:false;
    }

    @Override
    public List<User> findAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public int queryForAmountByCompanyId(long companyId) {
        return userDAO.queryForAmountByCompanyId(companyId);
    }

    @Override
    public boolean updateUser(User user) {
        int num = userDAO.update(user);
        return num >= 0 ? true : false;
    }

    @Override
    public User findById(long id) {
        return (User) userDAO.findById(id);
    }

    @Override
    public boolean deleteUser(long id) {
        int num = userDAO.deleteById(id);
        return num >= 0 ? true : false;
    }

    @Override
    public boolean batchDeleteUser(List<Long> ids) {
        int num = 0;
        try{
            for(long id : ids){
                num = userDAO.deleteById(id);
                if(num >= 0){
                    continue;
                }else{
                    return false;
                }
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean isExist(User user) {
        User u = userDAO.queryByNameAndPassword(user);
        if(u != null){
            return true;
        }
        return false;
    }

}
