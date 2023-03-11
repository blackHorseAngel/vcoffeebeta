package com.vcoffeebeta.service;

import com.alibaba.fastjson.JSON;
import com.vcoffeebeta.DAO.*;
import com.vcoffeebeta.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

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
                log.info("新增之后的newUser实体类数据：" + JSON.toJSONString(newUser));
                long newUserId = newUser.getId();
                log.info("新增user之后获取的userId： " + newUserId);
                Account account = handleAccount(newUserId,user);
                log.info("新建用户账户之前构造好的账户实体类信息：" + JSON.toJSONString(account));
                int accountNum = accountDAO.insert(account);
                Account oldAccount = null;
                if(accountNum > 0){
                    oldAccount = accountDAO.findByUserId(newUserId);
                    long oldAccountId = oldAccount.getId();
                    log.info("获取新增用户账户成功之后的账户id： " + oldAccountId);
                    newUser.setAccountId(oldAccountId);
                    newUser.setModifiedTime(new Date());
                    int updateUserNum = userDAO.update(newUser);
                    if(updateUserNum > 0){
                        log.info("更新用户信息中的账户id成功");
                        return true;
                    }else{
                        log.info("更新用户信息中的账户id失败");
                        return false;
                    }
                }else{
                    log.info("新增用户账户失败");
                    return false;
                }
            }else{
                log.info("新增用户失败");
                return false;
            }
        }catch(Exception e){
            log.info("新增用户失败报错，",e);
            e.printStackTrace();
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
    public void writeUserInfoToFile() {
        long startTime = System.currentTimeMillis();
        System.out.println("开始时间： " + startTime);
        File file = new File("D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo.txt");
        try {
                if(file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                FileWriter fw = new FileWriter(file,true);
                for(int i = 0 ; i < 10000000 ; i++){
                    long num = generateRandomNumber(1100L);
                    User user = userDAO.findById(num);
                    long oneDay = TimeUnit.DAYS.toMillis(1);
                    long now = new Date().getTime();
                    long start = now - oneDay * 365 * 5;
                    Date neededDate = generateRandomDate(start,now);
                    if(i%2 == 0){
                        user.setEmail("714680900@sina.cn");
                    } else if (i%3 == 0) {
                        user.setNewPassword("121212");
                    }else if(i%5 == 0){
                        user.setTelephoneNumber("010123123123");
                    }else if(i%7 == 0){
                        user.setUserNumber("010111111111");
                    }
                    user.setModified(user.getUserNumber());
                    user.setModifiedTime(neededDate);
                    fw.write(JSON.toJSONString(user));
                }
                long endTime = System.currentTimeMillis();
            System.out.println("结束时间：" + endTime);
            System.out.println("耗时：" + (endTime-startTime)/1000);
            } catch (IOException e) {
                log.error("写用户信息文件报错",e);
                throw new RuntimeException(e);
            }

    }

    /**
     * 生成[20,num+20)的随机数
     * @param num
     * @return
     */
    private long generateRandomNumber(long num){
        return ThreadLocalRandom.current().nextLong(num)+20;
    }

    /**
     * 生成随机日期
     * @param //startDate
     * @param //endDate
     * @return
     */
    private Date generateRandomDate(long startTime,Long endTime){
//        long startTime = startDate.getTime();
//        long endTime = endDate.getTime();
        long neededTime = ThreadLocalRandom.current().nextLong(startTime,endTime);
        Date neededDate = new Date(neededTime);
        return neededDate;
    }
    @Override
    public boolean isExist(User user) {
        User u = userDAO.queryByNameAndPassword(user);
        if(u != null){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        //        long oneDay = TimeUnit.DAYS.toMillis(1);
        //        long now = new Date().getTime();
        //        long startTime = now - oneDay * 365 * 5;
        //        for(int i = 0 ; i < 2000 ; i++){
        //            long neededTime = ThreadLocalRandom.current().nextLong(startTime,now);
        //            Date randomDate =  new Date(neededTime);
        //            long randomTime = randomDate.getTime();
        //            if(randomTime > now){
        //                System.out.println("第"+i+"个日期大于当前日期："+randomDate);
        //            }
        //        }
        System.out.println("test begin");
        long time1 = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("start date:" + sdf.format(time1));
        try {
            System.out.println("");
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long time2 = System.currentTimeMillis();
        System.out.println("end date:" + sdf.format(time2));
//        String dateStr = sdf.format(time2-time1);
        System.out.println(time2-time1);

    }
}
