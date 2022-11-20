package com.vcoffeebeta.service;

import com.vcoffeebeta.DAO.EquipmentDAO;
import com.vcoffeebeta.domain.Equipment;
import com.vcoffeebeta.domain.EquipmentQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 设备服务层实现类
 * @author zhangshenming
 * @date 2022/9/22 9:05
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class EquipmentServiceImpl implements EquipmentService {

    @Autowired
    private EquipmentDAO equipmentDAO;

    @Override
    public boolean insertEquipment(Equipment equipment) {
        int num = equipmentDAO.insert(equipment);
        return num >= 0 ? true : false;
    }

    @Override
    public int queryForAmount(EquipmentQuery equipmentQuery) {
        return equipmentDAO.queryForAmount(equipmentQuery);
    }

    @Override
    public List<Equipment> findAllEquipment(EquipmentQuery equipmentQuery) {
        return equipmentDAO.findAll(equipmentQuery);
    }

    @Override
    public Equipment findById(long id) {
        return (Equipment) equipmentDAO.findById(id);
    }

    @Override
    public boolean updateEquipment(Equipment equipment) {
        int num = equipmentDAO.update(equipment);
        return num > 0 ? true : false;
    }

    @Override
    public boolean deleteEquipment(long id) {
        int num = equipmentDAO.deleteById(id);
        return num > 0 ? true : false;
    }

    @Override
    public boolean batchDeleteEquipment(List<Long> ids) {
        int num = 0;
        try{
            for(long id : ids){
                num = equipmentDAO.deleteById(id);
                if(num > 0){
                    continue;
                }else{
                    return false;
                }
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Equipment> queryForList(Equipment equipment) {
        return equipmentDAO.queryForList(equipment);
    }

    @Override
    public List<Equipment> findAllEquipmentsByCompanyId(long companyId) {
        return equipmentDAO.findAllEquipmentsByCompanyId(companyId);
    }
}
