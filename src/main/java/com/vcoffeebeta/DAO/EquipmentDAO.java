package com.vcoffeebeta.DAO;

import com.vcoffeebeta.domain.Equipment;
import com.vcoffeebeta.util.BaseDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 设备DAO层
 * @author zhangshenming
 * @create 2022-09-22 09-08-23
 * @version 1.0
 */
@Repository
public interface EquipmentDAO extends BaseDAO {
    /**
     * @decription TODO
     * @author zhangshenming
     * @date 2022/9/30 17:08
     * @param
     * @return java.util.List<com.vcoffeebeta.domain.Equipment>
     */
    List<Equipment> findAllEquipmentsByCompanyId(long companyId);
}
