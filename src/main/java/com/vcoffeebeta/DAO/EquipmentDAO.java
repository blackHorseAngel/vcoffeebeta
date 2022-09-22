package com.vcoffeebeta.DAO;

import com.vcoffeebeta.domain.Equipment;
import com.vcoffeebeta.util.BaseDAO;
import org.springframework.stereotype.Repository;

/**
 * 设备DAO层
 * @author zhangshenming
 * @create 2022-09-22 09-08-23
 * @version 1.0
 */
@Repository
public interface EquipmentDAO extends BaseDAO {
    /**
     * 新增设备
     * @author zhangshenming
     * @date 2022/9/22 9:10
     * @param equipment
     * @return int
     */
    public int insertEquipment(Equipment equipment);
    /**
     * 查询设备数量
     * @author zhangshenming
     * @date 2022/9/22 9:53
     * @param
     * @return int
     */
    public int queryForAmount();
}
