package com.vcoffeebeta.controller;

import com.vcoffeebeta.domain.Result;
import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.UserService;
import com.vcoffeebeta.util.Validation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.Date;

/**
 * 登录控制模块
 * @author zhangshenming
 * @date 2022/1/11 18:18
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/vcoffee/")
public class LoginController {

    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping(value = "login")
    @ResponseBody
    public Result login(@RequestBody User requestUser,HttpSession session){
        log.info("进入login的Controller层");
        String username = requestUser.getUsername();
        username = HtmlUtils.htmlEscape(username);
        String password = requestUser.getPassword();
        boolean flag = userService.isExist(username);
        log.info("-------------------------"+flag+"------------------------------------");
        if(!flag){
            return new Result(ResultCodeEnum.NONEXIT.getCode(),ResultCodeEnum.NONEXIT.getMessage());
        }
        User user = null;
        try{
             user = userService.loginByNameAndPassword(username,password);
            log.info(user.toString());
        }catch(Exception e){
            log.error("登录时根据用户名和密码查找信息报错",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.INTERNALERROR.getCode(),ResultCodeEnum.INTERNALERROR.getMessage());
        }
        if (null == user) {
            log.info("-------------------------"+0+"--------------------------");
            return new Result(ResultCodeEnum.PASSWORDERROR.getCode(),ResultCodeEnum.PASSWORDERROR.getMessage());
         }
        log.info("--------------------------------"+user.toString()+"-------------------------------");
        session.setAttribute("user",user);
        return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
    }

    @CrossOrigin
    @PostMapping(value = "register")
    @ResponseBody
    public Result register(@RequestBody User requestUser,HttpSession session){
        log.info("进入register的Controller层");
        log.info("---------------------------------------"+requestUser.toString()+"-------------------------------");
        String username = requestUser.getUsername();
        username = HtmlUtils.htmlEscape(username);
        boolean validateUsername = userService.isExist(username);
        if(validateUsername){
            return new Result(ResultCodeEnum.USERNAMEEXIT.getCode(),ResultCodeEnum.USERNAMEEXIT.getMessage());
        }
        String password = requestUser.getPassword();
        String confirmPassword = requestUser.getConfirmPassword();
        boolean validatePassword = Validation.validatePassword(password,confirmPassword);
        if(!validatePassword){
            return new Result(ResultCodeEnum.PASSWORDCONFIRMERROR.getCode(),ResultCodeEnum.PASSWORDCONFIRMERROR.getMessage());
        }
        String telephoneNumber = requestUser.getTelephoneNumber();
        boolean validateTelephoneNumber = Validation.validateTelephoneNumber(telephoneNumber);
        if(!validateTelephoneNumber){
            return new Result(ResultCodeEnum.PHONENUMBERERROR.getCode(),ResultCodeEnum.PHONENUMBERERROR.getMessage());
        }
        String email =requestUser.getEmail();
        boolean validateEmail = Validation.validateEmail(email);
        if(!validateEmail){
            return new Result(ResultCodeEnum.EMAILERROR.getCode(),ResultCodeEnum.EMAILERROR.getMessage());
        }
        requestUser.setIsAdmin((byte) 1);
        requestUser.setCreatedTime(new Date());
        requestUser.setModifiedTime(new Date());
        boolean flag = false;
        try{
            flag = userService.insertUser(requestUser);
        }catch(Exception e){
            log.error("注册新用户报错",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.INTERNALERROR.getCode(),ResultCodeEnum.INTERNALERROR.getMessage());
        }
        if(!flag){
            return new Result(ResultCodeEnum.INSERTUSERERROR.getCode(),ResultCodeEnum.INSERTUSERERROR.getMessage());
        }
        return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
    }
}