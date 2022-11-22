package com.vcoffeebeta.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.vcoffeebeta.domain.*;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.AccountService;
import com.vcoffeebeta.service.CompanyService;
import com.vcoffeebeta.service.ConsumeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

/**
 * 消费控制层
 * @author zhangshenming
 * @date 2022/10/3 23:05
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/vcoffee/consume")
@Slf4j
public class ConsumeController {
    /**
     * 常数
     */
    public static final int TWO = 2;

    /**
     * 分页首页
     */
    public static final String FIRST = "first";
    /**
     * 分页最后一页
     */
    public static final String LAST = "last";

    @Autowired
    private ConsumeService consumeService;

    @CrossOrigin
    @RequestMapping(value = "insertConsume")
    @ResponseBody
    public Result insertConsume(@RequestBody Consume consume, HttpServletRequest request){
        log.info("进入consumeController的insertConsume方法");
        try{
            HttpSession session = request.getSession();
            User u = (User) session.getAttribute("user");
            long userId = u.getId();
            consume.setUserId(userId);
            consume.setAccountId(u.getAccountId());
            consume.setOperator(u.getUsername());
            consume.setOperateDate(new Date());
            int count = consumeService.queryForAmountByUserId(userId);
            count++;
            consume.setConsumeNumber(String.valueOf(count));
           boolean flag = consumeService.insertConsume(consume);
           if(flag){
               return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
           }else{
               return new Result(ResultCodeEnum.INSERTCONSUMEERROR.getCode(),ResultCodeEnum.INSERTCONSUMEERROR.getMessage());
           }
        }catch (Exception e){
            log.error("新增消费记录报错，",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.INSERTCONSUMEERROR.getCode(),ResultCodeEnum.INSERTCONSUMEERROR.getMessage());
        }
    }
    @CrossOrigin
    @RequestMapping(value = "queryAllConsumes")
    @ResponseBody
    public Result queryAllConsumes(@RequestBody ConsumeQuery consumeQuery,HttpServletRequest request){
        log.info("进入consumeController的queryAllConsumes方法");
        log.info("前台返回的page对象内容是： " + JSON.toJSONString(consumeQuery));
        try{
            HttpSession session = request.getSession();
            User u = (User) session.getAttribute("user");
            int amount = 0;
            if(u.getIsAdmin() == TWO){
               amount = consumeService.queryForAmount(consumeQuery);
                log.info("后台查询的所有消费记录数为：" + amount);
            }else{
                amount = consumeService.queryForAmountByUserId(u.getId());
                log.info("后台查询当前用户的所有消费记录数为：" + amount);
            }
            Page page = handlePage(amount,consumeQuery);
            if(page == null){
                return new Result(ResultCodeEnum.QUERYALLCONSUMESERROR.getCode(),ResultCodeEnum.QUERYALLCONSUMESERROR.getMessage());
            }
            PageHelper.startPage(Integer.parseInt(page.getCurrentPage()),page.getLimit());
            List<Consume>consumeList = consumeService.queryAllConsumes(consumeQuery);
            log.info("查询数据库之后返回的全部消费记录条数是： " + JSON.toJSONString(consumeList));
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),consumeList,page);
        }catch (Exception e){
            log.error("查询当前用户下所有消费记录报错，",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYALLCONSUMESERROR.getCode(),ResultCodeEnum.QUERYALLCONSUMESERROR.getMessage());
        }
    }
    /*@CrossOrigin
    @ResponseBody
    @RequestMapping(value = "queryConsume")
    public Result queryConsume(@RequestBody Consume consume,HttpServletRequest request){
        log.info("进入queryConsume方法");
        log.info("前台传过来的数据是" + JSON.toJSONString(consume));
        try{
          List<Consume>consumeList = consumeService.queryForList(consume);
          log.info("条件查询后的consumeList的数据是： " + JSON.toJSONString(consumeList));
          int size = consumeList.size();
          Page page = handlePage(size,consume);
          if(page == null){
             return new Result(ResultCodeEnum.QUERYCONSUMEPAGEERROR.getCode(),ResultCodeEnum.QUERYCONSUMEPAGEERROR.getMessage());
          }
          PageHelper.startPage(Integer.parseInt(page.getCurrentPage()),page.getLimit());
          return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),consumeList,page);
        }catch(Exception e){
            log.error("条件查询当前用户的消费记录报错",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYCONSUMEPAGEERROR.getCode(),ResultCodeEnum.QUERYCONSUMEPAGEERROR.getMessage());
        }
    }*/
    /**
     * 整理页码信息并返回
     * @author zhangshenming
     * @date 2022/10/4 17:11
     * @param amount, consume
     * @return com.vcoffeebeta.domain.Page
     */
    private Page handlePage(int amount, ConsumeQuery consumeQuery) {
       Page page = new Page();
       page.setTotalPage(amount);
       Page p = consumeQuery.getPage();
       int limit = p.getLimit();
       if(limit != 0){
           page.setLimit(limit);
           int totalPage = amount/limit + 1;
           page.setTotalPage(totalPage);
           String currentPageStr = p.getCurrentPage();
           if(currentPageStr == null){
               return null;
           }else{
               if(FIRST.equals(currentPageStr)){
                   currentPageStr = "1";
               }else if(LAST.equals(currentPageStr)){
                   currentPageStr = String.valueOf(totalPage);
               }
               page.setCurrentPage(currentPageStr);
               log.info("page对象中的全部属性值： " + JSON.toJSONString(page));
           }
       }
       return page;
    }
}
