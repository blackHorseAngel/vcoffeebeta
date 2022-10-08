package com.vcoffeebeta.service;

import com.alibaba.fastjson.JSON;
import com.vcoffeebeta.DAO.AccountDAO;
import com.vcoffeebeta.DAO.CompanyDAO;
import com.vcoffeebeta.DAO.EquipmentDAO;
import com.vcoffeebeta.DAO.UserDAO;
import com.vcoffeebeta.domain.Company;
import com.vcoffeebeta.domain.Equipment;
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
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {


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
        Company c = null;
        try{
            c = (Company) companyDAO.findById(companyId);
            log.info("userService中通过user中的companyId找到的公司信息是：" + JSON.toJSONString(c));
            if(c == null){
                return false;
            }
        }catch(Exception e){
            log.error("userService中根据companyId查找公司信息报错,",e);
            e.printStackTrace();
            return false;
        }
        StringBuilder equipmentIdBuilder = new StringBuilder();
        List<Equipment>equipmentList;
        try{
            equipmentList = equipmentDAO.findAllEquipmentsByCompanyId(companyId);
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
        }catch(Exception e){
            log.error("userService中通过companyId查找该公司名下所有设备信息报错,",e);
            e.printStackTrace();
            return false;
        }
        int userAmountByCompanyId = 0;
        try{
            userAmountByCompanyId = userDAO.queryForAmountByCompanyId(companyId);
            userAmountByCompanyId += 1;
            String userAmountByCompanyIdStr = String.valueOf(userAmountByCompanyId);
            int userAmountByCompanyIdStrLength = userAmountByCompanyIdStr.length();
            String companyIdStr = String.valueOf(companyId);
            int companyIdStrLength = companyIdStr.length();
            // userNumber字段的数据库中的长度是10，依次公司id的字符串长度和当前公司下的员工数的字符串长度，剩下的需要补0
            int zeroLength = 10 - userAmountByCompanyIdStrLength - companyIdStrLength;
            StringBuilder builder = new StringBuilder();
            builder.append(companyId);
            builder.append(userAmountByCompanyId);
            if(zeroLength > 0){
                for(int i = 0 ; i < zeroLength ; i++){
                    builder.append("0");
                }
            }
            String userNumber = builder.toString();
            builder.setLength(0);
            user.setUserNumber(userNumber);
        }catch(Exception e){
            log.error("userService中获取userNumber报错，",e);
            e.printStackTrace();
            return false;
        }
        log.info("userService中新增前user数据是： " + JSON.toJSONString(user));
        int num = userDAO.insert(user);
        return num > 0 ? true:false;
    }
    @Override
    public List<User> findAllUsers() {
        log.info("进入userService的findAllUsers方法");
        List<User>userList;
        try{
            userList = userDAO.findAll();
            for(User user1 : userList){
                long userCompanyId = user1.getCompanyId();
                if(userCompanyId == 0){
                    return null;
                }
                Company c = null;
                try{
                    c = (Company) companyDAO.findById(user1.getCompanyId());
                    if(c == null){
                        return null;
                    }
                }catch(Exception e){
                    log.error("userService中findAllUsers内，根据companyId查找公司信息报错，",e);
                    e.printStackTrace();
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
                        Equipment e = null;
                        try{
                            e = (Equipment) equipmentDAO.findById(id);
                            if(e == null){
                                return null;
                            }
                        }catch (Exception e1){
                            log.error("userService中findAllUsers内，根据id查找设备信息报错,",e);
                            e1.printStackTrace();
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
            e.printStackTrace();
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
        return (User) userDAO.findById(id);
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
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public List<User> queryForList(User user) {
        log.info("进入userService的queryForList方法");
        List<User>userList;
        try{
            userList = userDAO.queryForList(user);
            log.info("条件查询后的userList数据是：" + JSON.toJSONString(userList));
            for(User u : userList){
                try{
                    Company c = (Company) companyDAO.findById(u.getCompanyId());
                    u.setCompanyName(c.getCompanyName());
                }catch (Exception e){
                    log.error("userService的queryForList内，根据companyId查询公司信息报错，",e);
                    e.printStackTrace();
                    return null;
                }
                String equipmentIdStr = u.getEquipmentId();
                String[] equipmentIds = equipmentIdStr.split("|");
                StringBuilder builder = new StringBuilder();
                String equipmentNameStr = "";
                for(String s : equipmentIds){
                    if("|".equals(s)){
                        continue;
                    }
                    long equipmentId = Long.parseLong(s);
                    try{
                        Equipment e = (Equipment) equipmentDAO.findById(equipmentId);
                        builder.append(e.getEquipmentName());
                        builder.append("|");
                    }catch(Exception e1){
                        log.error("userService的queryForList内，根据设备id查找设备信息报错，",e1);
                        e1.printStackTrace();
                        return null;
                    }
                }
                equipmentNameStr = builder.toString();
                equipmentNameStr = equipmentNameStr.substring(0,equipmentNameStr.length() - 1);
                u.setEquipmentName(equipmentNameStr);
                builder.setLength(0);
            }
        }catch(Exception e){
            log.error("userService中的queryForList内，根据用户信息查询用户列表报错,",e);
            e.printStackTrace();
            return null;
        }
        return userList;
    }
    @Override
    public int queryForAmount() {
        return userDAO.queryForAmount();
    }
    @Override
    public User findByUserNumberAndCompanyId(User user) {
        return userDAO.findByUserNumberAndCompanyId(user);
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
