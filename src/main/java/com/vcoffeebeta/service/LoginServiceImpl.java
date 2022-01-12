package com.vcoffeebeta.service;

import com.vcoffeebeta.DAO.LoginDAO;
import com.vcoffeebeta.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * loginService实现类
 * @author zhangshenming
 * @date 2022/1/11 22:10
 * @version 1.0
 */
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginDAO loginDAO;

    @Override
    public User loginByNameAndPassword(String name, String password) {
        log.info("进入loginByNameAndPassword的service层");
        return loginDAO.queryByNameAndPassword(name,password);
    }

  public static void main(String[] args) {
    //
    LoginServiceImpl loginServiceImpl = new LoginServiceImpl();
    User user = loginServiceImpl.loginByNameAndPassword("admin","admin");
    System.out.println(user.toString());
  }
}
