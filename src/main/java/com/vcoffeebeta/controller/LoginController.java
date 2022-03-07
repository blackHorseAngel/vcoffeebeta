package com.vcoffeebeta.controller;

import com.vcoffeebeta.domain.Result;
import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;

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
        String username = requestUser.getUsername();
        username = HtmlUtils.htmlEscape(username);
        String password = requestUser.getPassword();
        User user = userService.loginByNameAndPassword(username,password);
        if (null == user) {
            return new Result(400);
         }
        session.setAttribute("user",user);
        return new Result(200);
    }

}