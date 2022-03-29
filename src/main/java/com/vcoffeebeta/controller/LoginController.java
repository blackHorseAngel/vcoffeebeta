package com.vcoffeebeta.controller;

import com.alibaba.fastjson.JSONObject;
import com.vcoffeebeta.domain.Result;
import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.UserService;
import com.vcoffeebeta.util.Validation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 登录控制模块
 * @author zhangshenming
 * @date 2022/1/11 18:18
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping(value = "/vcoffee/")
public class LoginController {

    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping(value = "login")
    @ResponseBody
    public Result login(@RequestBody User requestUser, HttpServletRequest request){
        log.info("进入login的Controller层");
        boolean flag = userService.isExist(requestUser);
        log.info("-------------------------"+flag+"------------------------------------");
        if(!flag){
            return new Result(ResultCodeEnum.NONEXIST.getCode(),ResultCodeEnum.NONEXIST.getMessage());
        }
        User user = null;
        try{
             user = userService.loginByNameAndPassword(requestUser);
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
        HttpSession session = request.getSession();
        session.setAttribute("user",user);
        log.info("session:"+ JSONObject.toJSONString(session));
        return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
    }

    @CrossOrigin
    @PostMapping(value = "register")
    @ResponseBody
    public Result register(@RequestBody User requestUser){
        log.info("进入register的Controller层");
        log.info("---------------------------------------"+requestUser.toString()+"-------------------------------");
        boolean validateUsername = userService.isExist(requestUser);
        if(validateUsername){
            return new Result(ResultCodeEnum.USERNAMEEXIST.getCode(),ResultCodeEnum.USERNAMEEXIST.getMessage());
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
        requestUser.setCreated(requestUser.getUsername());
        requestUser.setModified(requestUser.getUsername());
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
    @CrossOrigin
    @PostMapping(value = "logout")
    @ResponseBody
    public Result logout(HttpServletRequest request){
        log.info("进入logout方法");
        HttpSession session = request.getSession();
        log.info("session:"+JSONObject.toJSONString(session));
        User user = (User) session.getAttribute("user");
        log.info("登录的user对象："+JSONObject.toJSONString(user));
        session.removeAttribute("user");
        return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
    }

    @GetMapping(value = "logging")
    public String logging(){
        log.info("测试vue");
        StringBuilder builder = new StringBuilder();
        builder.append("<html>");
        builder.append("<head>");
        builder.append("<meta charset=utf-8>");
        builder.append("<title>vcoffeeui</title>");
        builder.append("<script src=\"../../js/vue.js\">");
        builder.append("</script>");
        builder.append("</head>");
        builder.append("<body>");
        builder.append("<div id='app'>");
        builder.append("<h1>message:{{ message }}</h1>");
        builder.append("</div>");
        builder.append("<script type=\"text/javascript\">");
        builder.append("var vm = new Vue({");
        builder.append("el:'#app',");
        builder.append("data:{");
        builder.append("message:\"hello world!\"");
        builder.append("}");
        builder.append("})");
        builder.append("</script>");
        builder.append("</body>");
        builder.append("</html>");
        return builder.toString();
    }
}