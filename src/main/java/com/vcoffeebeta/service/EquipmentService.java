package com.vcoffeebeta.service;

import com.vcoffeebeta.domain.Equipment;

import java.util.List;

/**
 * 设备服务层接口
 * @author zhangshenming
 * @version 1.0
 * @create 2022-09-22 09-03-31
 */
public interface EquipmentService {
    /**
     * 新增设备
     * @author zhangshenming
     * @date 2022/9/22 9:04
     * @param equipment
     * @return boolean
     */
    boolean insertEquipment(Equipment equipment);
    /**
     * 查询设备总数量
     * @author zhangshenming
     * @date 2022/9/22 9:51
     * @param
     * @return int
     */
    int queryForAmount();
    /**
     * 查询全部设备信息
     * @author zhangshenming
     * @date 2022/9/22 13:41
     * @param
     * @return java.util.List<com.vcoffeebeta.domain.Equipment>
     */
    List<Equipment> findAll();
}
