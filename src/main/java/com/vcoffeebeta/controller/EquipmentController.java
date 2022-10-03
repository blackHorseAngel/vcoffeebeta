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
import java.util.*;

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
    /**
     * 分页首页
     */
    public static final String FIRST = "first";
    /**
     * 分页最后一页
     */
    public static final String LAST = "last";

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
            boolean flag = equipmentService.insertEquipment(equipment);
            if(flag){
                return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage());
            }else{
                return new Result(ResultCodeEnum.INSERTEQUIPMENTERROR.getCode(),ResultCodeEnum.INSERTEQUIPMENTERROR.getMessage());
            }
        }catch(Exception e){
            log.error("equipmentController的insertEquipment内，新增设备报错",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.INSERTEQUIPMENTERROR.getCode(),ResultCodeEnum.INSERTEQUIPMENTERROR.getMessage());
        }
    }
    @CrossOrigin
    @RequestMapping(value = "/findAllEquipment")
    @ResponseBody
    public Result findAllEquipment(@RequestBody Equipment equipment){
        log.info("进入findAllEquipment方法：");
        try{
            int amount = equipmentService.queryForAmount();
            log.info("设备的数量amount: " + amount);
            Page page = handlePage(amount,equipment);
            if(page == null){
                return null;
            }
            PageHelper.startPage(Integer.parseInt(page.getCurrentPage()),page.getLimit());
            List<Equipment>equipmentList = equipmentService.findAllEquipment();
            for(Equipment e:equipmentList){
               Company c = companyService.queryById(e.getCompanyId());
               e.setCompanyName(c.getCompanyName());
            }
            log.info("全部设备的信息是：" + JSON.toJSONString(equipmentList));
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),equipmentList,page);
        }catch(Exception e){
            log.error("查询全部设备信息报错");
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYEQUIPMENTERROR.getCode(),ResultCodeEnum.QUERYEQUIPMENTERROR.getMessage());
        }
    }
    /**
     *
     * @author zhangshenming
     * @date 2022/10/3 16:02
     * @param amount, equipment
     * @return com.vcoffeebeta.domain.Page
     */
    private Page handlePage(int amount,Equipment equipment){
        Page page = new Page();
        page.setTotalCount(amount);
        Page p = equipment.getPage();
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
                int currentPage = Integer.parseInt(currentPageStr);
                page.setCurrentPage(currentPageStr);
                log.info("page对象中的全部属性值：" + JSONObject.toJSONString(page));

            }
        }else{
            return null;
        }
        return page;
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
      return new Result(ResultCodeEnum.TOUPDATECOMPANYERROR.getCode(),ResultCodeEnum.TOUPDATECOMPANYERROR.getMessage());
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
                return new Result(ResultCodeEnum.UPDATEEQUIPMENTERROR.getCode(),ResultCodeEnum.UPDATEEQUIPMENTERROR.getMessage());
            }
        }catch(Exception e){
            log.error("更新设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.UPDATEEQUIPMENTERROR.getCode(),ResultCodeEnum.UPDATEEQUIPMENTERROR.getMessage());
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
                return new Result(ResultCodeEnum.DELETEEQUIPMENTERROR.getCode(),ResultCodeEnum.DELETEEQUIPMENTERROR.getMessage());
            }
        }catch(Exception e){
            log.error("删除单个设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.DELETEEQUIPMENTERROR.getCode(),ResultCodeEnum.DELETEEQUIPMENTERROR.getMessage());
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
                return new Result(ResultCodeEnum.BATCHDELETEEQUIPMENTERROR.getCode(),ResultCodeEnum.BATCHDELETEEQUIPMENTERROR.getMessage());
            }
        }catch(Exception e){
            log.error("批量删除设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.BATCHDELETEEQUIPMENTERROR.getCode(),ResultCodeEnum.BATCHDELETEEQUIPMENTERROR.getMessage());
        }
    }
    @CrossOrigin
    @ResponseBody
    @RequestMapping(value = "queryEquipment")
    public Result queryEquipment(@RequestBody Equipment equipment){
        log.info("进入queryEquipment方法");
        log.info("前台传过来的equipment数据是：" + JSON.toJSONString(equipment));
        try{
            List<Equipment>equipmentList = equipmentService.queryForList(equipment);
            log.info("条件查询后的equipmentList数据是：" + JSON.toJSONString(equipmentList));
            for(Equipment e : equipmentList){
                Company c = companyService.queryById(e.getCompanyId());
                e.setCompanyName(c.getCompanyName());
            }
            int size = equipmentList.size();
            Page page = handlePage(size,equipment);
            if(page == null){
                return new Result(ResultCodeEnum.QUERYEQUIPMENTPAGEERROR.getCode(),ResultCodeEnum.QUERYEQUIPMENTPAGEERROR.getMessage());
            }
            PageHelper.startPage(Integer.parseInt(page.getCurrentPage()),page.getLimit());
            return new Result(ResultCodeEnum.SUCCESS.getCode(),ResultCodeEnum.SUCCESS.getMessage(),equipmentList,page);
        }catch (Exception e){
            log.error("条件查询设备信息报错,",e);
            e.printStackTrace();
            return new Result(ResultCodeEnum.QUERYEQUIPMENTBYOPTIONERROR.getCode(),ResultCodeEnum.QUERYEQUIPMENTBYOPTIONERROR.getMessage());
        }
    }
}
