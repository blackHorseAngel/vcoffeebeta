package com.vcoffeebeta.controller;

import com.vcoffeebeta.domain.Account;
import com.vcoffeebeta.domain.Result;
import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 账户控制层
 * @author zhangshenming
 * @date 2022/10/2 22:08
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping(value = "/vcoffee/account/")
public class AccountController{

    @Autowired
    private AccountService accountService;

    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "queryAccountDetail")
    public Result queryAccountDetail(HttpServletRequest request){
        log.info("进入queryAccountDetail方法");
        try{
            HttpSession session = request.getSession();
            User u = (User) session.getAttribute("user");
            long userId = u.getId();
            Account account = accountService.findByUserId(userId);
            if(account == null){
                return new Result(ResultCodeEnum.QUERYACCOUNTDETAILERROR.getCode(),ResultCodeEnum.QUERYACCOUNTDETAILERROR.getMessage());
            }else{
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),account);
            }
        }catch(Exception e){
            log.error("查询账户信息报错，",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYACCOUNTDETAILERROR.getCode(),ResultCodeEnum.QUERYACCOUNTDETAILERROR.getMessage());
        }

    }
}
