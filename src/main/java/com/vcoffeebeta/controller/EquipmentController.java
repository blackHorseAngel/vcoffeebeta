package com.vcoffeebeta.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.vcoffeebeta.domain.*;
import com.vcoffeebeta.enums.ResultCodeEnum;
import com.vcoffeebeta.service.CompanyService;
import com.vcoffeebeta.service.EquipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
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
@RequestMapping(value = "/vcoffee/equipment/")
@Slf4j
public class EquipmentController {

    @Autowired
    private EquipmentService equipmentService;

    @Autowired
    private CompanyService companyService;

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
            int amount = equipmentService.queryForAmount();
            log.info("设备的数量amount: " + amount);
            Page page = new Page();
            page.setTotalCount(amount);
            String limitStr = pageMap.get("limit");
            int limit = Integer.parseInt(limitStr);
            page.setLimit(limit);
            int totalPage = amount/limit + 1;
            page.setTotalPage(totalPage);
            String currentPageStr = pageMap.get("currentPage");
            int currentPage = Integer.parseInt(currentPageStr);
            page.setCurrentPage(currentPage);
            log.info("page对象中的全部属性值：" + JSONObject.toJSONString(page));
            PageHelper.startPage(currentPage,limit);
            List<Equipment>equipmentList = equipmentService.findAllEquipment();
            for(Equipment e:equipmentList){
                Company c = companyService.queryById(e.getCompanyId());
                e.setCompanyName(c.getCompanyName());
            }
            log.info("全部设备的信息是：" + JSON.toJSONString(equipmentList));
//            PageInfo<Equipment>equipmentPageInfo = new PageInfo<>(equipmentList);
//            log.info("全部设备的分页信息是：" + JSON.toJSONString(equipmentPageInfo));
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),equipmentList,page);
        }catch(Exception e){
            log.error("查询全部设备信息报错");
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYEQUIPMENT.getCode(),ResultCodeEnum.QUERYEQUIPMENT.getMessage());
        }
    }

  @CrossOrigin
  @ResponseBody
  @RequestMapping(value = "toUpdateEquipment")
  public Result toUpdateEquipment(@RequestBody String jsonStr,HttpServletRequest request) {
    log.info("进入toUpdateEquipment方法");
    log.info("前台传过来的参数： " + jsonStr);
    try {
      Equipment e = JSON.parseObject(jsonStr, Equipment.class);
      log.info("转换后的e：" + e.toString());
      HttpSession session = request.getSession();
      session.setAttribute("equipmentId",e.getId());
      Equipment oldEquipment = equipmentService.findById(e.getId());
      log.info("查询出来的oldEquipment：" + oldEquipment);
      return new Result(ResultCodeEnum.SUCCESS.getCode(), ResultCodeEnum.SUCCESS.getMessage(), oldEquipment);

    } catch (Exception e) {
      log.error("跳转到更新设备页面有问题,", e);
      e.printStackTrace();
      return new Result(
          ResultCodeEnum.TOUPDATECOMPANYERROR.getCode(),
          ResultCodeEnum.TOUPDATECOMPANYERROR.getMessage());
    }
    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "updateEquipment")
    public Result updateEquipment(@RequestBody Equipment equipment,HttpServletRequest request){
        log.info("进入updateEquipment方法");
        try{
            HttpSession session = request.getSession();
            log.info("修改设备信息时获取session信息：" + JSONObject.toJSONString(session));
            User user = (User) session.getAttribute("user");
            long id = (long) session.getAttribute("equipmentId");
            equipment.setId(id);
            equipment.setModified(user.getUsername());
            equipment.setModifiedTime(new Date());
            boolean flag = equipmentService.updateEquipment(equipment);
            if(flag){
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),equipment);
            }else{
                return new Result(ResultCodeEnum.UPDATEEQUIPMENT.getCode(),ResultCodeEnum.UPDATEEQUIPMENT.getMessage());
            }
        }catch(Exception e){
            log.error("更新设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.UPDATEEQUIPMENT.getCode(),ResultCodeEnum.UPDATEEQUIPMENT.getMessage());
        }

    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "deleteEquipment")
    public Result deleteEquipment(@RequestBody Equipment equipment){
        log.info("进去deleteEquipment方法");
        log.info("前台传过来的数据是： " + equipment.toString());
        long id = equipment.getId();
        log.info("选定的要删除的equipment的id是： " + id);
        try{
            boolean flag = equipmentService.deleteEquipment(id);
            if(flag){
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
            }else{
                return new Result(ResultCodeEnum.DELETEEQUIPMENT.getCode(),ResultCodeEnum.DELETEEQUIPMENT.getMessage());
            }
        }catch(Exception e){
            log.error("删除单个设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.DELETEEQUIPMENT.getCode(),ResultCodeEnum.DELETEEQUIPMENT.getMessage());
        }
    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "batchDeleteEquipment")
    public Result batchDeleteEquipment(@RequestBody Map<String,List<String>>idMap){
        log.info("进入batchDeleteEquipment方法" + JSON.toJSONString(idMap));
        try{
            List<String> idList = idMap.get("ids");
            log.info("idList:" + JSON.toJSONString(idList));
            List<Long>ids = new ArrayList<>();
            for(String s : idList){
                long id = Long.parseLong(s);
                ids.add(id);
            }
            boolean flag = equipmentService.batchDeleteEquipment(ids);
            if(flag){
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
            }else{
                return new Result(ResultCodeEnum.BATCHDELETEEQUIPMENT.getCode(),ResultCodeEnum.BATCHDELETEEQUIPMENT.getMessage());
            }
        }catch(Exception e){
            log.error("批量删除设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.BATCHDELETEEQUIPMENT.getCode(),ResultCodeEnum.BATCHDELETEEQUIPMENT.getMessage());
        }
    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "queryEquipment")
    public Result queryEquipment(@RequestBody Equipment equipment,Map<String,String>pageMap){
        log.info("进入queryEquipment方法");
        log.info("前台传过来的equipment数据是：" + JSON.toJSONString(equipment));
        try{
            List<Equipment>equipmentList = equipmentService.queryForList(equipment);
            log.info("条件查询后的equipmentList数据是：" + JSON.toJSONString(equipmentList));
            for(Equipment e : equipmentList){
                Company c = companyService.queryById(e.getCompanyId());
                e.setCompanyName(c.getCompanyName());
            }
            Page page = new Page();
            int limit = 10;
            page.setLimit(limit);
            int totalCount = equipmentList.size();
            page.setTotalCount(totalCount);
            int totalPage = totalCount/limit + 1;
            page.setTotalPage(totalPage);
            if(pageMap != null){
                String currentPageStr = pageMap.get("currentPage");
                if(currentPageStr != null){
                    int currentPage = Integer.parseInt(currentPageStr);
                    page.setCurrentPage(currentPage);
                }else{
                    page.setCurrentPage(1);
                }
            }
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),equipmentList,page);
        }catch (Exception e){
            log.error("条件查询设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYEQUIPMENTBYOPTION.getCode(),ResultCodeEnum.QUERYEQUIPMENTBYOPTION.getMessage());
        }

    }

}
