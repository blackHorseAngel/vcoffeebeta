package com.vcoffeebeta.controller;

import com.vcoffeebeta.domain.*;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.AccountService;
import com.vcoffeebeta.service.CompanyService;
import com.vcoffeebeta.service.EquipmentService;
import com.vcoffeebeta.service.UserService;
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

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EquipmentService equipmentService;

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
                Company company = companyService.queryById(u.getCompanyId());
                String equipmentIdStr = u.getEquipmentId();
                String[]equipmentIds = equipmentIdStr.split("|");
                StringBuilder builder = new StringBuilder();
                for(String s : equipmentIds){
                    long equipmentId = Integer.parseInt(s);
                    if(equipmentId == 0){
                        break;
                    }
                    Equipment equipment = equipmentService.findById(equipmentId);
                    builder.append(equipment.getEquipmentName());
                    builder.append("|");
                }
                String equipmentName = builder.toString();
                if(equipmentName.length() > 0){
                    equipmentName = equipmentName.substring(0,equipmentName.length() - 1);
                }
                account.setUserNumber(u.getUserNumber());
                account.setUsername(u.getUsername());
                account.setCompanyName(company.getCompanyName());
                account.setEquipmentName(equipmentName);
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),account);
            }
        }catch(Exception e){
            log.error("查询账户信息报错，",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYACCOUNTDETAILERROR.getCode(),ResultCodeEnum.QUERYACCOUNTDETAILERROR.getMessage());
        }

    }

  public static void main(String[] args) {
    //
      String s = "";
    System.out.println(s.length());
  }
}
