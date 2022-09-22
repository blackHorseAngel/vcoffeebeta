package com.vcoffeebeta.service;

import com.vcoffeebeta.DAO.EquipmentDAO;
import com.vcoffeebeta.domain.Equipment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 设备服务层实现类
 * @author zhangshenming
 * @date 2022/9/22 9:05
 * @version 1.0
 */
@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Override
    public boolean insertEquipment(Equipment equipment) {
        int num = equipmentDAO.insertEquipment(equipment);
        return num >= 0 ? true : false;
    }

    @Override
    public int queryForAmount() {
        return equipmentDAO.queryForAmount();
    }

    @Override
    public List<Equipment> findAll() {
        return equipmentDAO.findAll();
    }
}
