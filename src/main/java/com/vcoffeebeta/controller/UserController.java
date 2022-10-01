package com.vcoffeebeta.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.vcoffeebeta.domain.*;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.CompanyService;
import com.vcoffeebeta.service.EquipmentService;
import com.vcoffeebeta.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.RescaleOp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


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

    @Autowired
    private UserService userService;

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private CompanyService companyService;

    @CrossOrigin
    @RequestMapping(value = "insert")
    @ResponseBody
    public Result insertUser(@RequestBody User user, HttpServletRequest request){
        log.info("进入insertUser方法");
        try{
            HttpSession session = request.getSession();
            User u = (User) session.getAttribute("user");
            user.setCreated(u.getUsername());
            user.setModified(u.getUsername());
            user.setCreatedTime(new Date());
            user.setModifiedTime(new Date());
            long companyId = u.getCompanyId();
            List<Equipment>equipmentList;
            StringBuilder equipmentIdBuilder = new StringBuilder();
            if(companyId != 0){
                equipmentList = equipmentService.findAllEquipmentsByCompanyId(companyId);
                log.info("当前登录用户u所在公司下的所有的设备信息：" + JSON.toJSONString(equipmentList));
            }else{
                equipmentList = equipmentService.findAllEquipmentsByCompanyId(user.getCompanyId());
                log.info("新增用户所在公司下的所有的设备信息：" + JSON.toJSONString(equipmentList));
            }
            for(Equipment e : equipmentList){
                long equipmentId = e.getId();
                equipmentIdBuilder.append(equipmentId);
                equipmentIdBuilder.append(";");
            }
            String equipmentIdStr = equipmentIdBuilder.toString();
            equipmentIdStr = equipmentIdStr.substring(0,equipmentIdStr.length()-1);
            user.setEquipmentId(equipmentIdStr);
            boolean flag = userService.insertUser(user);
            equipmentIdBuilder.setLength(0);
            if(flag){
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
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "findAllUsers")
    public Result queryAllUsers(@RequestBody Map<String,String> pageMap,HttpServletRequest request){
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
                     amount = userService.queryForAmountByCompanyId(companyId);
                    log.info("当前用户是超级管理员，查询所有的用户数：" + amount);
                }else{
                    log.error("session用户信息中缺少companyId报错");
                    return new Result(ResultCodeEnum.SESSIONFORCOMPANYID.getCode(),ResultCodeEnum.SESSIONFORCOMPANYID.getMessage());
                }
            }else{
                 amount = userService.queryForAmountByCompanyId(companyId);
                log.info("当前用户是一般管理员。查询自己所在公司的用户数：" + amount);

            }
            Page page = new Page();
            page.setTotalCount(amount);
            String limitStr = pageMap.get("limit");
            int limit = Integer.parseInt(limitStr);
            page.setLimit(limit);
            int totalPage = amount/limit + 1;
            page.setTotalPage(totalPage);
            String currentPageStr = pageMap.get("currentPage");
            int currentPage = Integer.parseInt(currentPageStr);
            page.setCurrentPage(currentPage);
            log.info("page对象中的全部属性值：" + JSONObject.toJSONString(page));
            PageHelper.startPage(currentPage,limit);
            List<User>userList = userService.findAllUsers();
            for(User user : userList){
                Company c = companyService.queryById(u.getCompanyId());
                user.setCompanyName(c.getCompanyName());
                String equipmentIdStr = user.getEquipmentId();
                String[]equipmentIds = equipmentIdStr.split(";");
                StringBuilder equipmentNameBuilder = new StringBuilder();
                String equipmentNameStr = "";
                for(String idStr : equipmentIds){
                    long id = Long.parseLong(idStr);
                    Equipment e = equipmentService.findById(id);
                    equipmentNameBuilder.append(e.getEquipmentName());
                    equipmentNameBuilder.append(";");
                }
                equipmentNameStr = equipmentNameBuilder.toString();
                equipmentNameStr = equipmentNameStr.substring(0,equipmentNameStr.length() - 1);
                user.setEquipmentName(equipmentNameStr);
                equipmentNameBuilder.setLength(0);
            }
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
        }catch(Exception e){
            log.error("查询全部用户信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYALLUSERS.getCode(),ResultCodeEnum.QUERYALLUSERS.getMessage());
        }
    }
    @CrossOrigin
    @RequestMapping(value = "toUpdateUser")
    @ResponseBody
    public Result toUpdateUser(@RequestBody String jsonStr,HttpServletRequest request){
        //TODO
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
        log.info("进去batchDeleteUser方法" + JSON.toJSONString(idMap));
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
}
