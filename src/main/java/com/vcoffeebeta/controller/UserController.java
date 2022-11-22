package com.vcoffeebeta.controller;

import com.alibaba.fastjson.*;
import com.github.pagehelper.PageHelper;
import com.vcoffeebeta.domain.*;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;


/**
 * 用户控制层
 * @author zhangshenming
 * @date 2022/9/26 23:07
 * @version 1.0
 */
@Controller
@RequestMapping(value = "/vcoffee/user/")
@Slf4j
public class UserController {
    /**
     * 分页首页
     */
    public static final String FIRST = "first";
    /**
     * 分页最后一页
     */
    public static final String LAST = "last";

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @CrossOrigin
    @RequestMapping(value = "insertUser")
    @ResponseBody
    public Result insertUser(@RequestBody User user, HttpServletRequest request){
        log.info("进入insertUser方法");
        try{
            user = handleUser(user,request);
            boolean flagForInsert = userService.insertUser(user);
            if(flagForInsert){

                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
            }else{
              return new Result(ResultCodeEnum.INSERTUSERERROR.getCode(),ResultCodeEnum.INSERTUSERERROR.getMessage());
            }
        }catch(Exception e){
            log.error("新增用户报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.INSERTUSERERROR.getCode(),ResultCodeEnum.INSERTUSERERROR.getMessage());
        }
    }
    /**
     * 新增前，对新的user对象进行赋值
     * @author zhangshenming
     * @date 2022/10/3 11:04
     * @param user, request
     * @return com.vcoffeebeta.domain.User
     */
    private User handleUser(User user,HttpServletRequest request){
        HttpSession session = request.getSession();
        User u = (User) session.getAttribute("user");
        //新增用户默认密码
        user.setPassword("123456");
        user.setCreated(u.getUsername());
        user.setModified(u.getUsername());
        user.setCreatedTime(new Date());
        user.setModifiedTime(new Date());
        return user;
    }


    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "findAllUsers")
    public Result queryAllUsers(@RequestBody UserQuery userQuery,HttpServletRequest request){
        log.info("进入queryAllUsers方法");
        try{
            HttpSession session = request.getSession();
            User u = (User) session.getAttribute("user");
            log.info("session中的用户信息是： " + JSON.toJSONString(u));
            long companyId = u.getCompanyId();
            int isAdmin = u.getIsAdmin();
            int amount = 0;
            if(companyId == 0){
                if(isAdmin == 2){
                    //超级管理员查询全部的用户数
                     amount = userService.queryForAmount(userQuery);
                    log.info("当前用户是超级管理员，查询所有的用户数：" + amount);
                }else{
                    //除了超级管理员之外，一般管理员和普通员工都必须有公司id
                    log.error("session用户信息中缺少companyId报错");
                    return new Result(ResultCodeEnum.SESSIONFORCOMPANYID.getCode(),ResultCodeEnum.SESSIONFORCOMPANYID.getMessage());
                }
            }else{
                //其他管理员查询的用户总数为其所在公司下的用户总数
                 amount = userService.queryForAmountByCompanyId(companyId);
                log.info("当前用户是一般管理员。查询自己所在公司的用户数：" + amount);
            }
            Page page = handlePage(amount,userQuery);
            if(page == null){
                return new Result(ResultCodeEnum.QUERYUSERPAGEERROR.getCode(),ResultCodeEnum.QUERYUSERPAGEERROR.getMessage());
            }
            PageHelper.startPage(Integer.parseInt(page.getCurrentPage()),page.getLimit());
            List<User>userList = userService.findAllUsers(userQuery);
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),userList,page);
        }catch(Exception e){
            log.error("查询全部用户信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYALLUSERS.getCode(),ResultCodeEnum.QUERYALLUSERS.getMessage());
        }
    }
    /**
     * 查询全部用户时候，处理分页page对象
     * @author zhangshenming
     * @date 2022/10/3 12:38
     * @param amount, user
     * @return com.vcoffeebeta.domain.Page
     */
    private Page handlePage(int amount,UserQuery userQuery){
        Page page = new Page();
        page.setTotalCount(amount);
        Page p = userQuery.getPage();
        int limit = p.getLimit();
        if(limit != 0){
            page.setLimit(limit);
            int totalPage = amount/limit + 1;
            page.setTotalPage(totalPage);
            String currentPageStr = p.getCurrentPage();
            if(currentPageStr == null){
                return null;
            }else{
                if(FIRST.equals(currentPageStr)){
                    currentPageStr = "1";
                }else if(LAST.equals(currentPageStr)){
                    currentPageStr = String.valueOf(totalPage);
                }
                int currentPage = Integer.parseInt(currentPageStr);
                page.setCurrentPage(currentPageStr);
                log.info("page对象中的全部属性值：" + JSONObject.toJSONString(page));
            }
        }else{
            return null;
        }
        return page;
    }
    @CrossOrigin
    @RequestMapping(value = "toUpdateUser")
    @ResponseBody
    public Result toUpdateUser(@RequestBody String jsonStr,HttpServletRequest request){
        log.info("进入toUpdateUser方法");
        log.info("前台传过来的参数：" + jsonStr);
        try{
            User u = JSON.parseObject(jsonStr,User.class);
            HttpSession session = request.getSession();
            long id = u.getId();
            session.setAttribute("oldUserId",id);
            User oldUser = userService.findById(id);
            log.info("查询出的oldUser的数据是：" + JSON.toJSONString(oldUser));
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),oldUser);
        }catch(Exception e){
            log.error("跳转到更新用户页面报错，",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.TOUPDATEUSER.getCode(),ResultCodeEnum.TOUPDATEUSER.getMessage());
        }

    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "updateUser")
    public Result updateUser(@RequestBody User user,HttpServletRequest request){
        log.info("进入updateUser方法");
        try{
          HttpSession session = request.getSession();
          log.info("修改用户信息的时候获取session信息：" + JSONObject.toJSONString(session));
          User sessionUser = (User) session.getAttribute("user");
          user.setModified(sessionUser.getUsername());
          user.setModifiedTime(new Date());
          long userId = (long) session.getAttribute("oldUserId");
          user.setId(userId);
          boolean flag = userService.updateUser(user);
          if(flag){
              return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),user);
          }else{
              return new Result(ResultCodeEnum.UPDATEUSER.getCode(),ResultCodeEnum.UPDATEUSER.getMessage());
          }
        }catch(Exception e){
            log.error("更新用户信息报错，",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.UPDATEUSER.getCode(),ResultCodeEnum.UPDATEUSER.getMessage());
        }
    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "deleteUser")
    public Result deleteUser(@RequestBody User user){
        log.info("进入deleteUser方法");
        log.info("前台传过来的数据是： " + user.toString());
        try{
            long id =user.getId();
            boolean flag = userService.deleteUser(id);
            if(flag){
                try{
                    boolean accountFlag = accountService.deleteByUserId(id);
                    if(!accountFlag){
                        return new Result(ResultCodeEnum.DELETEACCOUNTERROR.getCode(),ResultCodeEnum.DELETEACCOUNTERROR.getMessage());
                    }
                }catch (Exception e){
                    log.error("userController中deleteUser方法内，删除当前用户的账户报错，",e);
                    e.printStackTrace();
                    return new Result(ResultCodeEnum.DELETEACCOUNTERROR.getCode(),ResultCodeEnum.DELETEACCOUNTERROR.getMessage());
                }
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
            }else{
                return new Result(ResultCodeEnum.DELETEUSER.getCode(),ResultCodeEnum.DELETEUSER.getMessage());
            }
        }catch (Exception e){
            log.error("删除用户信息报错，",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.DELETEUSER.getCode(),ResultCodeEnum.DELETEUSER.getMessage());
        }
    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "batchDeleteUsers")
    public Result batchDeleteUser(@RequestBody Map<String,List<String>>idMap){
        log.info("进入batchDeleteUser方法" + JSON.toJSONString(idMap));
        try{
          List<String>idList = idMap.get("ids");
          log.info("idList:" + JSON.toJSONString(idList));
          List<Long>ids = new ArrayList<>();
          for(String s : idList){
              long id = Long.parseLong(s);
              ids.add(id);
          }
          boolean flag = userService.batchDeleteUser(ids);
          if(flag){
              return new Result(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage());
          }else{
              return new Result(ResultCodeEnum.BATCHDELETEUSER.getCode(),ResultCodeEnum.BATCHDELETEUSER.getMessage());
          }
        }catch(Exception e){
            log.error("批量删除用户报错",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.BATCHDELETEUSER.getCode(),ResultCodeEnum.BATCHDELETEUSER.getMessage());
        }
    }
    /*@CrossOrigin
    @ResponseBody
    @RequestMapping(value = "queryUser")
    public Result queryUser(@RequestBody User user){
       log.info("进入queryUser方法");
       log.info("前台传过来的user数据是：" + JSON.toJSONString(user));
       try{
           List<User>userList = userService.queryForList(user);
           int size = userList.size();
           Page page = handlePage(size,user);
           if(page == null){
               return new Result(ResultCodeEnum.QUERYUSERPAGEERROR.getCode(),ResultCodeEnum.QUERYUSERPAGEERROR.getMessage());
           }
           PageHelper.startPage(Integer.parseInt(page.getCurrentPage()),page.getLimit());
          return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),userList,page);
       }catch (Exception e){
           log.error("查询用户信息报错，",e);
           e.printStackTrace();
           return new Result(ResultCodeEnum.QUERYUSERERROR.getCode(),ResultCodeEnum.QUERYUSERERROR.getMessage());
       }
    }*/
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "findUserById")
    public Result findUserById(HttpServletRequest request){
        log.info("进入findUserById方法内");
        try{
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("user");
           user = userService.findById(user.getId());
           if(user == null){
              return new Result(ResultCodeEnum.QUERYUSERERROR.getCode(),ResultCodeEnum.QUERYUSERERROR.getMessage());
           }
           return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),user);
        }catch(Exception e){
            log.error("根据用户id查找用户报错",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYUSERERROR.getCode(),ResultCodeEnum.QUERYUSERERROR.getMessage());
        }
    }
}
