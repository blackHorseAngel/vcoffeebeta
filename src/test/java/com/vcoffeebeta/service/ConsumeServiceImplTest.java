package com.vcoffeebeta.service;

import com.vcoffeebeta.VcoffeebetaApplication;
import com.vcoffeebeta.domain.Consume;
import com.vcoffeebeta.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Date;

@SpringBootTest(classes = VcoffeebetaApplication.class)
class ConsumeServiceImplTest {

    @Autowired
    private ConsumeService consumeService;
    @Autowired
    private UserService userService;

    @Test
    void insertConsume() {
        Consume consume = new Consume();
        long userId = 19;
        consume.setUserId(userId);
        User user = userService.findById(userId);
        long accountId = user.getAccountId();
        consume.setAccountId(accountId);
        String userName = user.getUsername();
        consume.setOperator(userName);
        consume.setOperateDate(new Date());
        consume.setType((byte) 1);
        consume.setAmount(new BigDecimal(3));
        int count = consumeService.queryForAmountByUserId(userId);
        count++;
        consume.setConsumeNumber(String.valueOf(count));
        boolean flag = consumeService.insertConsume(consume);
        System.out.println(flag);
    }

    @Test
    void queryAllConsumes() {
    }

    @Test
    void queryForAmount() {
    }

    @Test
    void queryForList() {
    }

    @Test
    void queryForAmountByUserId() {
    }
}