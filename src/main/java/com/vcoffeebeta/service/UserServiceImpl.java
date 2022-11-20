package com.vcoffeebeta.service;

import com.alibaba.fastjson.JSON;
import com.vcoffeebeta.DAO.*;
import com.vcoffeebeta.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * loginService实现类
 * @author zhangshenming
 * @date 2022/1/11 22:10
 * @version 1.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {
    /**
     * 常数
     */
    public static final int TWO = 2;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private CompanyDAO companyDAO;

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public User loginByNameAndPassword(User user) {
        return userDAO.queryByNameAndPassword(user);
    }
    @Override
    public boolean  insertUser(User user) {
        log.info("进入userService的insertUser方法");
        long companyId = user.getCompanyId();
        log.info("userService中通过user拿到的companyId是： " + companyId);
        try{
            Company c = (Company) companyDAO.findById(companyId);
            log.info("userService中通过user中的companyId找到的公司信息是：" + JSON.toJSONString(c));
            if(c == null){
                return false;
            }else{
                StringBuilder equipmentIdBuilder = new StringBuilder();
                List<Equipment> equipmentList = equipmentDAO.findAllEquipmentsByCompanyId(companyId);
                log.info("当前登录用户u所在公司下的所有的设备信息：" + JSON.toJSONString(equipmentList));
                //若新增用户所在的公司名下没有暂时没有设备，按0处理进行新增
                if(equipmentList.size() == 0){
                    user.setEquipmentId("0");
                }else{
                    for(Equipment e : equipmentList){
                        long equipmentId = e.getId();
                        equipmentIdBuilder.append(equipmentId);
                        equipmentIdBuilder.append("|");
                    }
                    String equipmentIdStr = equipmentIdBuilder.toString();
                    equipmentIdStr = equipmentIdStr.substring(0,equipmentIdStr.length()-1);
                    user.setEquipmentId(equipmentIdStr);
                    equipmentIdBuilder.setLength(0);
                }
            }
            log.info("userService中新增前user数据是： " + JSON.toJSONString(user));
            int num = userDAO.insert(user);
            if(num > 0){
                User newUser = userDAO.findByUserNumberAndCompanyId(user);
                long newUserId = newUser.getId();
                Account account = handleAccount(newUserId,user);
                int accountNum = accountDAO.insert(account);
                Account oldAccount = null;
                if(accountNum > 0){
                    oldAccount = accountDAO.findByUserId(newUserId);
                    long oldAccountId = oldAccount.getId();
                    newUser.setAccountId(oldAccountId);
                    int updateUserNum = userDAO.update(newUser);
                    if(updateUserNum > 0){
                        return true;
                    }else{
                        return false;
                    }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 新增前，对新的account对象进行赋值
     * @author zhangshenming
     * @date 2022/10/3 11:01
     * @param userId, user
     * @return com.vcoffeebeta.domain.Account
     */
    private Account handleAccount(long userId,User user){
        Account account = new Account();
        account.setUserId(userId);
        //初始时每个账户充值100元
        account.setRemaining(new BigDecimal(100));
        account.setCreated(user.getCreated());
        account.setModified(user.getModified());
        account.setCreatedTime(new Date());
        account.setModifiedTime(new Date());
        return account;
    }
    @Override
    public List<User> findAllUsers(UserQuery userQuery) {
        log.info("进入userService的findAllUsers方法");
        List<User>userList;
        try{
            userList = userDAO.findAll(userQuery);
            for(User user1 : userList){
                long userCompanyId = user1.getCompanyId();
                if(userCompanyId == 0){
                    return null;
                }
                Company c = (Company) companyDAO.findById(user1.getCompanyId());
                if(c == null){
                   return null;
                }
                user1.setCompanyName(c.getCompanyName());
                String equipmentIdStr = user1.getEquipmentId();
                if(equipmentIdStr == null || "0".equals(equipmentIdStr)){
                    user1.setEquipmentName("");
                }else{
                    String[]equipmentIds = equipmentIdStr.split("|");
                    StringBuilder equipmentNameBuilder = new StringBuilder();
                    String equipmentNameStr = "";
                    for(String idStr : equipmentIds){
                        if("|".equals(idStr)){
                            continue;
                        }
                        long id = Long.parseLong(idStr);
                        Equipment e = (Equipment) equipmentDAO.findById(id);
                        if(e == null){
                           return null;
                       }
                        equipmentNameBuilder.append(e.getEquipmentName());
                        equipmentNameBuilder.append("|");
                    }
                    equipmentNameStr = equipmentNameBuilder.toString();
                    equipmentNameStr = equipmentNameStr.substring(0,equipmentNameStr.length() - 1);
                    user1.setEquipmentName(equipmentNameStr);
                    equipmentNameBuilder.setLength(0);
                }
            }
            return userList;
        }catch(Exception e){
            log.error("userService中findAllUsers内，查找全部用户信息报错，",e);
            return null;
        }
    }
    @Override
    public int queryForAmountByCompanyId(long companyId) {
        return userDAO.queryForAmountByCompanyId(companyId);
    }
    @Override
    public boolean updateUser(User user) {
        int num = userDAO.update(user);
        return num > 0 ? true : false;
    }
    @Override
    public User findById(long id) {
        log.info("进入userService的findById方法内");
        try{
            User  user = (User) userDAO.findById(id);
           if(user.getIsAdmin() != TWO){
               Company c = (Company) companyDAO.findById(user.getCompanyId());
               user.setCompanyName(c.getCompanyName());
           }
            return user;
        }catch(Exception e){
            log.error("userService的findById方法报错，",e);
           e.printStackTrace();
           return null;
        }
    }
    @Override
    public boolean deleteUser(long id) {
        int num = userDAO.deleteById(id);
        return num > 0 ? true : false;
    }
    @Override
    public boolean batchDeleteUser(List<Long> ids) {
        int num = 0;
        try{
            for(long id : ids){
                num = userDAO.deleteById(id);
                if(num > 0){
                    try{
                        int flag = accountDAO.deleteByUserId(id);
                        if(flag > 0){
                            continue;
                        }else{
                            return false;
                        }
                    }catch(Exception e){
                        log.error("userService的batchDeleteUser内，批量删除用户账户报错",e);
                        e.printStackTrace();
                        return false;
                    }
                }else{
                    return false;
                }
            }
            return true;
        }catch(Exception e){
            log.error("userService的batchDeleteUser内，批量删除用户报错，",e);
            return false;
        }
    }
    @Override
    public List<User> queryForList(UserQuery userQuery) {
        log.info("进入userService的queryForList方法");
        try{
            List<User>userList = userDAO.queryForList(userQuery);
            log.info("条件查询后的userList数据是：" + JSON.toJSONString(userList));
            for(User u : userList){
                Company c = (Company) companyDAO.findById(u.getCompanyId());
                u.setCompanyName(c.getCompanyName());
                String equipmentIdStr = u.getEquipmentId();
                String[] equipmentIds = equipmentIdStr.split("|");
                StringBuilder builder = new StringBuilder();
                String equipmentNameStr = "";
                for(String s : equipmentIds){
                    if("|".equals(s)){
                        continue;
                    }
                    long equipmentId = Long.parseLong(s);
                    Equipment e = (Equipment) equipmentDAO.findById(equipmentId);
                    builder.append(e.getEquipmentName());
                    builder.append("|");
                }
                equipmentNameStr = builder.toString();
                equipmentNameStr = equipmentNameStr.substring(0,equipmentNameStr.length() - 1);
                u.setEquipmentName(equipmentNameStr);
                builder.setLength(0);
            }
            return userList;
        }catch(Exception e){
            log.error("userService中的queryForList内，根据用户信息查询用户列表报错,",e);
            return null;
        }

    }
    @Override
    public int queryForAmount(UserQuery userQuery) {
        return userDAO.queryForAmount(userQuery);
    }

    @Override
    public boolean changePassword(User user) {
        int num = userDAO.changePassword(user);
        return num > 0 ? true : false;
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
