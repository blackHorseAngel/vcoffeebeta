package com.vcoffee.com.vcoffeebeta.domain;

import com.vcoffee.com.vcoffeebeta.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * loginService实现类
 * @author zhangshenming
 * @date 2022/1/11 22:10
 * @version 1.0
 */
@Service(value = "loginService")
@Slf4j
public class LoginServiceImpl implements LoginService {



    @Override
    public User loginByNameAndPassword(String name, String password) {
        log.info("进入loginByNameAndPassword的service层");
        User user= new User();
        return user;
    }

  public static void main(String[] args) {
    //
    LoginServiceImpl loginServiceImpl = new LoginServiceImpl();
    User user = loginServiceImpl.loginByNameAndPassword("admin","admin");
    System.out.println(user.toString());
  }
}
