package com.vcoffee.com.vcoffeebeta.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author zhangshenming
 * @date 2022/1/11 18:18
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping(value = "/vcoffee/")
public class LoginController {

    @RequestMapping(value = "loginByNameAndPassword")
    public String loginByNameAndPassword(@RequestParam(name = "name",required = true)String name, @RequestParam(name = "password",required = true) String password){
        log.info("进入loginByNameAndPassword");

        return "";
    }

  public static void main(String[] args) {
    //
      LoginController loginController = new LoginController();
      loginController.loginByNameAndPassword("admin","admin");
  }
}
