package com.vcoffeebeta.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vcoffeebeta.DAO.*;
import com.vcoffeebeta.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
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
                List<Equipment> equipmentList = equipmentDAO.findAllEquipmentsByCompanyId(companyId);
                log.info("当前登录用户u所在公司下的所有的设备信息：" + JSON.toJSONString(equipmentList));
                //若新增用户所在的公司名下没有暂时没有设备，按0处理进行新增
                long equipmentIdSum = 0;
                if(equipmentList.size() == 0){
                    user.setEquipmentId(0);
                }else{
                    for(Equipment e : equipmentList){
                         long equipmentId = e.getId();
                         equipmentIdSum = equipmentIdSum * 10 + equipmentId;
                    }
                    user.setEquipmentId(equipmentIdSum);
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
                long equipmentId = user1.getEquipmentId();
                if(equipmentId == 0){
                    user1.setEquipmentName("");
                }else{
                    long equipmentIdNum = equipmentId%10;
                    StringBuilder equipmentNameBuilder = new StringBuilder();
                    String equipmentNameStr = "";
                    while(equipmentIdNum%10 != 0){
                        long num = equipmentIdNum % 10;
                        Equipment e = (Equipment) equipmentDAO.findById(num);
                        equipmentNameBuilder.append(e.getEquipmentName());
                        equipmentNameBuilder.append("|");
                        equipmentIdNum = equipmentIdNum / 10;
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
                long equipmentIds = u.getEquipmentId();
                StringBuilder builder = new StringBuilder();
                String equipmentNameStr = "";
                while(equipmentIds%10 != 0){
                    long equipmentId = equipmentIds%10;
                    Equipment e = (Equipment) equipmentDAO.findById(equipmentId);
                    builder.append(e.getEquipmentName());
                    builder.append("|");
                    equipmentIds = equipmentIds/10;
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
    public void writeUserInfoToFile(int num) {
        File file = new File("D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo.txt");
        try {
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileWriter fw = new FileWriter(file,true);
//            BufferedWriter bw = new BufferedWriter(fw);
            List<Company>companyList = companyDAO.findAll(new Company());
            Random random = new Random();
            long startTime = System.currentTimeMillis();
            System.out.println("开始时间： " + startTime);
            for(int i = 0 ; i < num ; i++){
                Company company =  getRandomCompany(companyList,random);
                User user = generateUser(company,random);
//                bw.append(JSON.toJSONString(user));
//                bw.flush();
//                bw.append(user.toString());
//                bw.newLine();
                fw.write(user.toString());
                fw.write("\r\n");
            }
//            bw.close();
            fw.close();
            long endTime = System.currentTimeMillis();
            System.out.println("结束时间：" + endTime);
            System.out.println("耗时：" + (endTime-startTime));
            //100条 64ms
        } catch (IOException e) {
            log.error("写用户信息文件报错",e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void writeUserInfoToFileNew(int num) {
        String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo2.txt";
        File file = new File(fileName);
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file,true);
            oos = new ObjectOutputStream(fos);
            List<Company>companyList = companyDAO.findAll(new Company());
            Random random = new Random();
            long startTime = System.currentTimeMillis();
            System.out.println("开始时间： " + startTime);
            for(int i = 0 ; i < num ; i++){
                Company company =  getRandomCompany(companyList,random);
                User user = generateUser(company,random);
                oos.writeObject(user);
            }
            oos.writeObject(null);
            oos.close();
            fos.close();
            long endTime = System.currentTimeMillis();
            System.out.println("结束时间：" + endTime);
            System.out.println("耗时：" + (endTime-startTime));
            //100条 49ms
        } catch (IOException e) {
            log.error("写用户信息文件报错",e);
            throw new RuntimeException(e);
        }
    }

    private Company getRandomCompany(List<Company>companyList,Random random) {
        return companyList.get(random.nextInt(companyList.size()));
    }

    @Override
    public void insertUserFromFileToDb() {
        String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo.txt";
        FileReader fr = null;
        BufferedReader  br = null;
        try {
             fr = new FileReader(fileName);
             br = new BufferedReader(fr);
             int insertCount = 0;
             int updateCount = 0;
             String jsonStr = "";
             while ((jsonStr = br.readLine()) != null){
                 User user = JSONObject.parseObject(jsonStr,User.class);

                     User oldUser = userDAO.queryByNameAndPassword(user);
                     if(oldUser != null){
                         Date modifiedTime = user.getModifiedTime();
                         Date oldModifiedTime = oldUser.getModifiedTime();
                         if(modifiedTime.after(oldModifiedTime)){
                             user.setId(oldUser.getId());
                             user.setUserNumber(handleUserNumber(user.getCompanyId()));
                             user.setEquipmentId(oldUser.getEquipmentId());
                             user.setEquipmentName(oldUser.getEquipmentName());
                             int updateResult = userDAO.update(user);
                             if(updateResult > 0){
                                 updateCount++;
//                                 log.info("更新成功" + jsonStr);
                             }else{
                                 log.error("更新失败" + jsonStr);
                                 //TODO 更新失败数据处理
                             }
                         }
                     }else{
                        int insertResult = userDAO.insert(user);
                        if(insertResult > 0){
                            insertCount++;
//                            log.info("插入成功" + jsonStr);
                        }else{
                            log.error("插入失败" + jsonStr);
                            //TODO 插入数据失败处理
                        }
                     }
             }
             log.info("实际插入的条数是：" + insertCount);
             log.info("实际修改的条数是：" + updateCount);
        } catch (FileNotFoundException e) {
            log.error("输入的文件不存在",e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("读文件异常",e);
            throw new RuntimeException(e);
        } catch(Exception e){
            log.error("其他报错",e);
            e.printStackTrace();
        }finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void insertUserFromFileToDbNew() {
            String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo.txt";
            FileReader fr = null;
            BufferedReader  br = null;
            try {
                fr = new FileReader(fileName);
                br = new BufferedReader(fr);
                int insertCount = 0;
                int updateCount = 0;
                String str = "";
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                Class userClass = User.class;
                User user = (User) userClass.getDeclaredConstructor().newInstance();
                Field[]fields = userClass.getDeclaredFields();
                long startTime = System.currentTimeMillis();
                log.info("解析文件数据兵插入数据库： " + startTime);
                while ((str = br.readLine()) != null){
                    str = str.substring(str.indexOf("(")+1,str.indexOf(")"));
                    String[]strArray = str.split(",");
                    for(int i = 0 ; i < strArray.length ; i++){
                        String stringArrayValue = strArray[i].substring(strArray[i].indexOf("=")+1);
                        String fieldName = fields[i].getName();
                        String fieldType = fields[i].getType().getSimpleName();
                            fields[i].setAccessible(true);
                            if("long".equals(fieldType)){
                                fields[i].set(user,Long.parseLong(stringArrayValue));
                            }else if("int".equals(fieldType)){
                                fields[i].set(user,Integer.parseInt(stringArrayValue));
                            }else if("Date".equals(fieldType)){
                                fields[i].set(user,sdf.parse(stringArrayValue));
                            }else if("byte".equals(fieldType)){
                                fields[i].set(user,Byte.valueOf(stringArrayValue));
                            }else{
                                fields[i].set(user,stringArrayValue);
                            }
                    }
                        User oldUser = userDAO.queryByNameAndPassword(user);
                        if(oldUser != null){
                            Date modifiedTime = user.getModifiedTime();
                            Date oldModifiedTime = oldUser.getModifiedTime();
                            if(modifiedTime.after(oldModifiedTime)){
                                user.setId(oldUser.getId());
                                user.setEquipmentId(oldUser.getEquipmentId());
                                user.setEquipmentName(oldUser.getEquipmentName());
                                int updateResult = userDAO.update(user);
                                if(updateResult > 0){
                                    updateCount++;
//                                    log.info("更新成功" + str);
                                }else{
                                    log.error("更新失败" + str);
                                    //TODO 更新失败数据处理
                                    break;
                                }
                            }
                        }else{
                            int insertResult = userDAO.insert(user);
                            if(insertResult > 0){
                                insertCount++;
//                                log.info("插入成功" + str);
                            }else{
                                log.error("插入失败" + str);
                                //TODO 插入数据失败处理
                                break;
                            }
                        }
            }
                long endTime = System.currentTimeMillis();
                log.info("从文件到数据库的操作耗时：" + (endTime - startTime));
                log.info("实际插入的条数是：" + insertCount);
                log.info("实际修改的条数是：" + updateCount);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertUserFromFileToDbNew2() {
        String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo2.txt";
        FileInputStream fis = null;
        ObjectInputStream  ois = null;
            try {
                fis = new FileInputStream(fileName);
                ois = new ObjectInputStream(fis);
                int insertCount = 0;
                int updateCount = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                long startTime = System.currentTimeMillis();
                User user = null;
                log.info("开始计时：" + startTime);
                while((user = (User) ois.readObject()) != null){
                    User oldUser = userDAO.findByUserNameAndCompanyId(user);
                    if(oldUser != null){
                        Date modifiedTime = user.getModifiedTime();
                        Date oldModifiedTime = oldUser.getModifiedTime();
                        if(modifiedTime.after(oldModifiedTime)){
                            user.setId(oldUser.getId());
                            user.setEquipmentId(oldUser.getEquipmentId());
                            user.setEquipmentName(oldUser.getEquipmentName());
                            int updateResult = userDAO.update(user);
                            if(updateResult > 0){
                                updateCount++;
//                                log.info("更新成功");
                            }else{
                                log.error("更新失败" + user.toString());
                                //TODO 更新失败数据处理
                                break;
                            }
                        }
                    }else{
                        int insertResult = userDAO.insert(user);
                        if(insertResult > 0){
                            insertCount++;
//                            log.info("插入成功");
                        }else{
                            log.error("插入失败" + user.toString());
                            //TODO 插入数据失败处理
                            break;
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                log.info("从文件到数据库的操作耗时：" + (endTime - startTime));
                log.info("实际插入的条数是：" + insertCount);
                log.info("实际修改的条数是：" + updateCount);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
    }

    private User generateUser(Company company,Random random){
        User user = new User();
        Date date = new Date();
        long companyId = company.getId();
        user.setPassword("123456");
        user.setCompanyId(companyId);
        user.setCompanyName(company.getCompanyName());
        user.setCreated("admin");
        user.setCreatedTime(date);
        user.setModified("admin");
        user.setModifiedTime(date);
        user.setConfirmPassword("123456");
        int num = random.nextInt(2000);
        user.setUsername("employee" + num);
        user.setEmail("714680900@qq.com");
        user.setIsAdmin((byte) 0);
        user.setState(0);
        user.setTelephoneNumber("12345123458");
        //TODO 设备信息处理
        user.setEquipmentId(0);
        String userNumber = handleUsernumberForFile(random);
        user.setUserNumber(userNumber);
        long oneDay = TimeUnit.DAYS.toMillis(1);
        long now = date.getTime();
        long end = now + oneDay * 30 * 8;
        Date neededDate = generateRandomDate(now,end);
        user.setModified(user.getUsername());
        user.setModifiedTime(neededDate);
        return user;
    }
    //TODO 从文件读取插入数据中的时候使用
    private String handleUserNumber(long companyId) {
        int userNum = userDAO.queryForAmountByCompanyId(companyId);
        userNum = userNum + 1;
        StringBuilder builder = new StringBuilder();
        builder.append(companyId);
        builder.append("");
        builder.append(userNum);
        for(int i = 0 ; i < 10-builder.length() ; i++){
            builder.append(0);
        }
        String userNumber = builder.toString();
        builder.setLength(0);
        return userNumber;
    }
    private String handleUsernumberForFile(Random random){
        return String.valueOf(random.nextInt(10000000));
    }
    /**
     * 生成[20,num+20)的随机数
     * @param num
     * @return
     */

    /**
     * 生成随机日期
     * @param //startDate
     * @param //endDate
     * @return
     */
    private Date generateRandomDate(long startTime,Long endTime){
        long neededTime = (long) (startTime + (Math.random() * (endTime - startTime)));
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

    /**
     * 测试FileWriter和BufferedWriter的缓存值
     * @author zhangshenming
     * @date 2023/03/16 18:10
     * @param
     * @return
     */
    private static void testFileWriterOrBufferedWriterFlush(){
        String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\testFileWriter.txt";
        File file = new File(fileName);
        FileWriter fw = null;
        BufferedWriter bw = null;
        int count = 0;
        try {
            if(file.exists()){
                file.delete();
            }
            file.createNewFile();
            fw = new FileWriter(file);
//            bw = new BufferedWriter(fw);
            while(file.length() <= 0){
                fw.write("a");
                count++;
            }
            //fileWriter:8193个a
            //bufferedWriter:16384个a
            System.out.println("一共写入了" + count + "个a自动刷新");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
            int a = 123;
        System.out.println(a%10);

    }
}
