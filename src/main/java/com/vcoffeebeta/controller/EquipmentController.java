package com.vcoffeebeta.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.vcoffeebeta.domain.Equipment;
import com.vcoffeebeta.domain.Page;
import com.vcoffeebeta.domain.Result;
import com.vcoffeebeta.domain.User;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 设备controller
 * @author zhangshenming
 * @date 2022/9/21 11:21
 * @version 1.0
 */
@RestController
@RequestMapping(value = "/vcoffee/")
@Slf4j
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @CrossOrigin
    @PostMapping(value = "insertEquipment")
    @ResponseBody
    public Result insertEquipment(@RequestBody Equipment equipment, HttpServletRequest request){
        log.info("进入insertEquipment方法");
        try{
            HttpSession session = request.getSession();
            log.info("新增设备时获取的session信息：" + JSONObject.toJSONString(session));
            User user = (User) session.getAttribute("user");
            log.info("新增设备时获取session中的user信息：" + JSONObject.toJSONString(user));
            equipment.setCreated(user.getUsername());
            equipment.setModified(user.getUsername());
            equipment.setCreatedTime(new Date());
            equipment.setModifiedTime(new Date());
            boolean isSuccess = equipmentService.insertEquipment(equipment);
            if(isSuccess){
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
            }
        }catch(Exception e){
            log.error("新增设备报错",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.INSERTEQUIPMENT.getCode(),ResultCodeEnum.INSERTEQUIPMENT.getMessage());
        }
        return null;
    }
    @CrossOrigin
    @RequestMapping(value = "/findAllEquipment")
    @ResponseBody
    public Result findAllEquipment(@RequestBody Map<String,String> pageMap){
        log.info("进度findAllEquipment方法：" + JSON.toJSONString(pageMap));
        try{
//            int amount = equipmentService.queryForAmount();
//            log.info("设备的数量amount: " + amount);
//            Page page = new Page();
//            page.setTotalCount(amount);
//            String limitStr = pageMap.get("limit");
//            int limit = Integer.parseInt(limitStr);
//            page.setLimit(limit);
//            int totalPage = amount/limit + 1;
//            page.setTotalPage(totalPage);
//            String currentPageStr = pageMap.get("currentPage");
//            int currentPage = Integer.parseInt(currentPageStr);
//            page.setCurrentPage(currentPage);
//            log.info("page对象中的全部属性值：" + JSONObject.toJSONString(page));
//            PageHelper.startPage(currentPage,limit);
            List<Equipment>equipmentList = equipmentService.findAll();
            log.info("全部设备的信息是：" + JSON.toJSONString(equipmentList));
            PageInfo<Equipment>equipmentPageInfo = new PageInfo<>(equipmentList);
            log.info("全部设备的分页信息是：" + JSON.toJSONString(equipmentPageInfo));
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
        }catch(Exception e){
            log.error("查询全部设备信息报错");
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYEQUIPMENT.getCode(),ResultCodeEnum.QUERYEQUIPMENT.getMessage());
        }



    }
}
