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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

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
                String equipmentIdStr = "";
                if(equipmentList.size() == 0){
                    user.setEquipmentId("0");
                }else{
                    for(Equipment e : equipmentList){
                         long equipmentId = e.getId();
                         equipmentIdStr = String.valueOf(equipmentId);
                    }
                    user.setEquipmentId(equipmentIdStr);
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
                    newUser.setModifiedTime(new Date().getTime());
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

    @Override
    public int insertBatchUser(List<User> userList) {
        return userDAO.insertBatch(userList);
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
                if(equipmentIdStr.length() == 0){
                    user1.setEquipmentName("");
                }else{
                    StringBuilder equipmentNameBuilder = new StringBuilder();
                    String equipmentNameStr = "";
                    String[]equipmentIds = equipmentIdStr.split("|");
                    for(int i = 0 ; i < equipmentIds.length ; i++){
                        if(equipmentIds[i].matches("\\d")){
                            long num = Integer.parseInt(equipmentIds[i]);
                            Equipment e = (Equipment) equipmentDAO.findById(num);
                            if(e == null){
                                break;
                            }
                            equipmentNameBuilder.append(e.getEquipmentName());
                            equipmentNameBuilder.append("|");
                        }
                    }
                    equipmentNameStr = equipmentNameBuilder.toString();
                    if(equipmentNameStr.length() != 0){
                        equipmentNameStr = equipmentNameStr.substring(0,equipmentNameStr.length() - 1);
                        user1.setEquipmentName(equipmentNameStr);
                        equipmentNameBuilder.setLength(0);
                    }else{
                        user1.setEquipmentName("");
                    }
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
    public int batchUpdate(List<User> list) {
        return userDAO.updateBatch(list);
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            log.info("条件查询后的userList数据是：" + JSON.toJSONString(userList));
            for(User u : userList){
                Company c = (Company) companyDAO.findById(u.getCompanyId());
                u.setCompanyName(c.getCompanyName());
                String equipmentIdStr = u.getEquipmentId();
                String[]equipmentIds = equipmentIdStr.split("|");
                StringBuilder builder = new StringBuilder();
                String equipmentNameStr = "";
                for (int i = 0 ; i < equipmentIds.length;i++){
                    long equipmentId = Integer.parseInt(equipmentIds[i]);
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
    public void writeUserInfoToFile(int num) {
        File file = new File("D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo.txt");
       //根据系统自动匹配换行符
        String separator = System.getProperty("line.separator");
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
                fw.write(separator);
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
        OutputStreamWriter osw = null;
        try {
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            fos = new FileOutputStream(file,true);
            oos = new ObjectOutputStream(fos);
//            osw = new OutputStreamWriter(fos,"utf-8");
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
            //100条 36ms
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
                         long modifiedTime = user.getModifiedTime();
                         long oldModifiedTime = oldUser.getModifiedTime();
                         if(modifiedTime - oldModifiedTime < 0){
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
                            long modifiedTime = user.getModifiedTime();
                            long oldModifiedTime = oldUser.getModifiedTime();
                            if(modifiedTime - oldModifiedTime < 0){
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
    public void insertUserFromFileToDbNew2() {
        String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo2.txt";
        FileInputStream fis = null;
        ObjectInputStream  ois = null;
            try {
                fis = new FileInputStream(fileName);
                ois = new ObjectInputStream(fis);
                int insertCount = 0;
                int updateCount = 0;
                int count  = userDAO.queryForAmountByCompanyId(1);
                long startTime = System.currentTimeMillis();
                User user = null;
                log.info("开始计时：" + startTime);
                while((user = (User) ois.readObject()) != null){
                    User oldUser = userDAO.findByUserNameAndCompanyId(user);
                    long modifiedTime = user.getModifiedTime();
                    if(oldUser != null){
                        long oldModifiedTime = oldUser.getModifiedTime();
                        if(modifiedTime - oldModifiedTime > 0){
                            int updateResult = userDAO.update(user);
                            if(updateResult > 0){
                                updateCount++;
//                                log.info("更新成功"+user.toString());
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
//                            log.info("插入成功"+user.toString());
                        }else{
                            log.error("插入失败" + user.toString());
                            //TODO 插入数据失败处理
                            break;
                        }
                    }
                }
                long endTime = System.currentTimeMillis();
                log.info("从文件到数据库的操作耗时：" + (endTime - startTime));
                //100条  516ms
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBatchUsersFromFileToDb() {
        String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo2.txt";
        FileInputStream fis = null;
        ObjectInputStream  ois = null;
        Map<String,Map<Long,User>>insertUserMap = new HashMap<>();
        Map<String,Map<Long,User>>updateUserMap = new HashMap<>();
        List<User>insertUserList = new ArrayList<>();
        List<User>updateUserList = new ArrayList<>();
        try {
            int count = userDAO.queryForAmountByCompanyId(1L);
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            User user = null;
            long startTime = System.currentTimeMillis();
            log.info("开始计时：" + startTime);
            while((user = (User) ois.readObject()) != null){
                String userName = user.getUsername();
                long companyId= user.getCompanyId();
                long modifiedTime = user.getModifiedTime();
                User oldUser = userDAO.findByUserNameAndCompanyId(user);
                /**
                 * 思路：如果数据库中没有user数据，判断insertUserMap中是否含有新读取的用户的用户名，
                 * 如果含有相同用户名，继续判断是否含有相同的公司id，
                 * 如果含有相同的公司id，从insertUserMap中拿到这个用户数据，比较两个用户的修改时间，修改时间较大者所对应的用户数据更新到insertUserMap中；
                 * 如果不含有相同的公司id，直接增加到insertUsermap中；
                 * 如果不含有相同用户名，直接增加到insertUsermap中；
                 * 如果数据库中有user数据，判断updateUserMap中是否含有新都区的用户的用户名，
                 * 如果含有相同的用户名，继续判断是否含有相同的公司id，
                 * 如果含有相同的公司id，从updateUserMap中拿到这个用户数据，比较三个用户的修改时间，修改时间较大者所对应的用户数据更新到updateUserMap中；
                 * 如果不含有相同的公司id，比较数据库中的user和新读取的user额度修改时间，较大者所对应的用户数据更新到updateUserMap中；
                 * 如果不含有相同的用户名，比较数据库中的user和新读取的user额度修改时间，较大者所对应的用户数据更新到updateUserMap中；
                 * 在新增和更新之前，分别将insertUserMap和updateUserMap中的用户数据转成List形式，分别调用userDAO的insertBatch和updateBatch方法
                 */
                if(oldUser == null){
                    if(insertUserMap.containsKey(userName)){
                        if(insertUserMap.get(userName).containsKey(companyId)){
                            User insertUser = insertUserMap.get(userName).get(companyId);
                            long insertUserModifiedTime = insertUser.getModifiedTime();
                            if(modifiedTime - insertUserModifiedTime > 0){
                                insertUserMap.get(userName).put(companyId,user);
                            }
                        }else{
                            insertUserMap.get(userName).put(companyId,user);
                        }
                    }else{
                        Map<Long,User>map = new HashMap<>();
                        map.put(companyId,user);
                        insertUserMap.put(userName,map);
                    }
                }else{
                    long oldUserModifiedTime = oldUser.getModifiedTime();
                    if(updateUserMap.containsKey(userName)){
                        if(updateUserMap.get(userName).containsKey(companyId)){
                            User updateUser = updateUserMap.get(userName).get(companyId);
                            long updateUserModifiedTime = updateUser.getModifiedTime();
                            long max = ((max = (modifiedTime > oldUserModifiedTime) ? modifiedTime : oldUserModifiedTime) > updateUserModifiedTime ? max : updateUserModifiedTime);
                            if(max == modifiedTime){
                                updateUserMap.get(userName).put(companyId,user);
                            }
                        }else{
                            if(modifiedTime - oldUserModifiedTime > 0){
                                updateUserMap.get(userName).put(companyId,user);
                            }
                        }
                    }else{
                        if(modifiedTime - oldUserModifiedTime > 0){
                            Map<Long,User>map = new HashMap<>();
                            map.put(companyId,user);
                            updateUserMap.put(userName,map);
                        }
                    }
                }
            }
            insertUserList = transferToUserList(insertUserList,insertUserMap);
            updateUserList = transferToUserList(updateUserList,updateUserMap);
            if(insertUserList.size() != 0){
                int insertNum = userDAO.insertBatch(insertUserList);
                log.info("批量新增成功的条数是：" + insertNum);
            }
            if(updateUserList.size() != 0){
                int updateNum = userDAO.updateBatch(updateUserList);
                log.info("批量更新成功的条数： " + updateNum);
            }
            long endTime = System.currentTimeMillis();
            log.info("从文件到数据库的操作耗时：" + (endTime - startTime));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertUserFromFileToDbByThread() {
        String fileName = "D:\\eclipseWorkspace\\vcoffeebeta\\src\\main\\resources\\datas\\userInfo2.txt";
        FileInputStream fis = null;
        ObjectInputStream  ois = null;
        User user = null;
//        Executors.newCachedThreadPool()
//        Future future = null;
        try{
            fis = new FileInputStream(fileName);
            ois = new ObjectInputStream(fis);
            /**
             * 创建线程池，核心线程数：2，最大线程数:10 存活时间（单位）：3 s 任务队列：ArrayBlockingQueue 长度2 线程工厂：默认 拒绝策略：默认
             */
            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 5, 3, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5), Executors.defaultThreadFactory(), new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    new ThreadPoolExecutor.CallerRunsPolicy();
                }
            });
            AtomicInteger insertCount = new AtomicInteger(0);
            AtomicInteger updateCount = new AtomicInteger(0);
            long startTime = System.currentTimeMillis();
            log.info("开始计时：" + startTime);
            while((user = (User) ois.readObject())!= null){
                User oldUser = userDAO.findByUserNameAndCompanyId(user);
                User finalUser = user;
//                log.info("准备开始运行线程池中的线程,用户名字：" + user.getUsername() +"，公司id："+ user.getCompanyId() + ",修改时间：" + user.getModifiedTime());
                threadPoolExecutor.submit(()->{
                    dealWithUser(finalUser,oldUser,insertCount,updateCount);
//                    log.info("当前正在执行任务的线程：" + threadPoolExecutor.getActiveCount());
//                    log.info("当前池中线程数（正在执行任务的+休眠的）：" + threadPoolExecutor.getPoolSize());
//                    log.info("当前任务数（运行的+排队的）：" + threadPoolExecutor.getTaskCount());
//                    log.info("已经执行完的任务数：" + threadPoolExecutor.getCompletedTaskCount());
                });
            }
//            if(threadPoolExecutor.getCompletedTaskCount() == threadPoolExecutor.getTaskCount()){
//            }
//            threadPoolExecutor.shutdown();
            while(threadPoolExecutor.getActiveCount() != 0){
                Thread.sleep(1);
            }
            long endTime = System.currentTimeMillis();
            log.info("耗时：" + (endTime - startTime));
            log.info("新增条数：" + insertCount);
            log.info("修改条数：" + updateCount);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }catch(Exception e){
            log.error("报错了" + user.toString());
            e.printStackTrace();
        }
    }
    /**
     * 多线程处理用户新增或修改
     *
     * @param finalUser
     * @param num1
     * @param num2
     * @return
     */
     private void dealWithUser(User finalUser, User oldUser,AtomicInteger num1, AtomicInteger num2)  {
         int count = userDAO.queryForAmountByCompanyId(1);
        long modifiedTime = finalUser.getModifiedTime();
        try{
            if(oldUser != null){
                long oldModifiedTime = oldUser.getModifiedTime();
                if(modifiedTime - oldModifiedTime > 0){
                    int updateResult = userDAO.update(finalUser);
                    if(updateResult > 0){
                        num2.getAndIncrement();
//                        log.info("更新成功" + finalUser.toString());
                    }else{
                        log.error("更新失败" + finalUser.toString());
                        //TODO 更新失败数据处理
                    }
                }
            }else{
                int insertResult = userDAO.insert(finalUser);
                if(insertResult > 0){
                    num1.getAndIncrement();
//                    log.info("插入成功"+finalUser.toString());
                }else{
                    log.error("插入失败" + finalUser.toString());
                    //TODO 插入数据失败处理
                }
            }

        }catch(Exception e){
            log.error("插入数据库报错了"+e);
            e.printStackTrace();
        }
     }
    private List<User> transferToUserList(List<User> list, Map<String, Map<Long, User>> map) {
        for(String userName:map.keySet()){
            for(long l:map.get(userName).keySet()){
                list.add(map.get(userName).get(l));
            }
        }
        return list;
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
        user.setModifiedTime(date.getTime());
        user.setConfirmPassword("123456");
        int num = random.nextInt(100);
        user.setUsername("employee" + num);
        user.setEmail("714680900@qq.com");
        user.setIsAdmin((byte) 0);
        user.setState(0);
        user.setTelephoneNumber("12345123458");
        //TODO 设备信息处理
        user.setEquipmentId("0");
        String userNumber = handleUsernumberForFile(random);
        user.setUserNumber(userNumber);
        long oneDay = TimeUnit.DAYS.toMillis(1);
        long now = date.getTime();
        long end = now + oneDay * 30 * 8;
        long neededTime = generateRandomDate(now,end);
        user.setModified(user.getUsername());
        user.setModifiedTime(neededTime);
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
    private long generateRandomDate(long startTime,Long endTime){
        long neededTime = (long) (startTime + (Math.random() * (endTime - startTime)));
        return neededTime;
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

    }
}
