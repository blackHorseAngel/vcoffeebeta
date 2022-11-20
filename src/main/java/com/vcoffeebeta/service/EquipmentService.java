package com.vcoffeebeta.service;

import com.vcoffeebeta.domain.Equipment;
import com.vcoffeebeta.domain.EquipmentQuery;

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
    int queryForAmount(EquipmentQuery equipmentQuery);
    /**
     * 查询全部设备信息
     * @author zhangshenming
     * @date 2022/9/22 13:41
     * @param
     * @return java.util.List<com.vcoffeebeta.domain.Equipment>
     */
    List<Equipment> findAllEquipment(EquipmentQuery equipmentQuery);
    /**
     * 根据id查询设备信息
     * @author zhangshenming
     * @date 2022/9/25 16:45
     * @param id
     * @return com.vcoffeebeta.domain.Equipment
     */
    Equipment findById(long id);
    /**
     * 更新设备信息
     * @author zhangshenming
     * @date 2022/9/25 17:04
     * @param equipment
     * @return boolean
     */
    boolean updateEquipment(Equipment equipment);
    /**
     * 删除设备信息
     * @author zhangshenming
     * @date 2022/9/25 17:19
     * @param id
     * @return boolean
     */
    boolean deleteEquipment(long id);
    /**
     * 批量删除设备信息
     * @author zhangshenming
     * @date 2022/9/25 17:34
     * @param ids
     * @return boolean
     */
    boolean batchDeleteEquipment(List<Long> ids);
    /**
     * 条件查询设备信息
     * @author zhangshenming
     * @date 2022/9/26 21:07
     * @param equipment
     * @return java.util.List<com.vcoffeebeta.domain.Equipment>
     */
    List<Equipment> queryForList(Equipment equipment);
    /**
     * 通过公司id，查询该公司名下的全部设备信息
     * @author zhangshenming
     * @date 2022/9/30 17:06
     * @param
     * @return java.util.List<com.vcoffeebeta.domain.Equipment>
     */
    List<Equipment> findAllEquipmentsByCompanyId(long companyId);
}
