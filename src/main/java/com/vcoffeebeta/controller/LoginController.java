package com.vcoffeebeta.controller;

import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private LoginService loginService;

    @RequestMapping(value = "loginByNameAndPassword")
    public String loginByNameAndPassword(@RequestParam(name = "name",required = true)String name, @RequestParam(name = "password",required = true) String password){
        log.info("进入loginByNameAndPassword的controller层");
        User user = loginService.loginByNameAndPassword(name,password);
        if(user.getName() == null){
            return "false";
        }
        return "true";
    }

  public static void main(String[] args) {
    //
      LoginController loginController = new LoginController();
      loginController.loginByNameAndPassword("admin","admin");
  }
}
