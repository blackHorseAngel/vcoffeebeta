package com.vcoffeebeta.service;

import com.vcoffeebeta.DAO.AccountDAO;
import com.vcoffeebeta.DAO.ConsumeDAO;
import com.vcoffeebeta.DAO.UserDAO;
import com.vcoffeebeta.domain.Account;
import com.vcoffeebeta.domain.Consume;
import com.vcoffeebeta.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 消费服务层实现类
 * @author zhangshenming
 * @date 2022/10/3 23:20
 * @version 1.0
 */
@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ConsumeServiceImpl implements ConsumeService{

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ConsumeDAO consumeDAO;

    @Autowired
    private AccountDAO accountDAO;

    @Override
    public boolean insertConsume(Consume consume) {
        log.info("进入consumeService的insertConsume方法内");
        int num = 0;
        try{
            num = consumeDAO.insert(consume);
            Account account = accountDAO.findByUserId(consume.getUserId());
            BigDecimal remaining = account.getRemaining();
            List<Consume>consumeList = consumeDAO.queryForList(consume);
            for(Consume c : consumeList){
                int type = c.getType();
                if(type == 0){
                    remaining = remaining.add(c.getAmount());
                }else if(type == 1){
                    remaining = remaining.subtract(c.getAmount());
                }else{
                    remaining = new BigDecimal(0);
                }
            }
            BigDecimal zero = new BigDecimal(0);
            if(remaining.compareTo(zero) > 0){
                account.setRemaining(remaining);
            }else{
                account.setRemaining(zero);
            }
            User u = (User) userDAO.findById(consume.getUserId());
            account.setModified(u.getUsername());
            account.setModifiedTime(new Date());
            int count = accountDAO.update(account);
            return (num > 0) && (count > 0) ? true : false;
        }catch (Exception e){
            log.error("新增消费信息报错",e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Consume> queryAllConsumes() {
        return consumeDAO.findAll();
    }

    @Override
    public int queryForAmount() {
        return consumeDAO.queryForAmount();
    }

    @Override
    public List<Consume> queryForList(Consume consume) {

        return consumeDAO.queryForList(consume);
    }

    @Override
    public int queryForAmountByUserId(long userId) {
        return consumeDAO.queryForAmountByUserId(userId);
    }
}
