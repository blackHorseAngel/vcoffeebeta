package com.vcoffeebeta.service;

import com.vcoffeebeta.VcoffeebetaApplication;
import com.vcoffeebeta.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SpringBootTest(classes = VcoffeebetaApplication.class) //webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Autowired
    private CompanyService companyService;

    @Test
    void loginByNameAndPassword() {
    }

    @Test
    void insertUser() {
       /* User user = new User();
        user.setPassword("123456");
        user.setCompanyId(1L);
        user.setCompanyName(companyService.queryById(1L).getCompanyName());
        user.setCreated("admin");
        user.setCreatedTime(new Date());
        user.setModified("admin");
        user.setModifiedTime(new Date());
        user.setConfirmPassword("123456");
        user.setUsername("employeeJ");
        user.setEmail("714680900@qq.com");
        user.setIsAdmin((byte) 0);
        user.setState(0);
        user.setTelephoneNumber("12345123458");
        user.setUserNumber("1100000001");
        boolean flag = userService.insertUser(user);
        System.out.println(flag);*/

        System.out.println("创建新用户结束： " + System.currentTimeMillis());

    }

    @Test
    void findAllUsers() {
    }

    @Test
    void queryForAmountByCompanyId() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void findById() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void batchDeleteUser() {
    }

    @Test
    void queryForList() {
    }

    @Test
    void queryForAmount() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void isExist() {
    }

    @Test
    void writeUserInfoToFile() {
        userService.writeUserInfoToFile(100);
    }

    @Test
    void insertUserFromFileToDb() {
        userService.insertUserFromFileToDb();
    }

    @Test
    void insertUserFromFileToDbNew() {
        userService.insertUserFromFileToDbNew();
    }

    @Test
    void writeUserInfoToFileNew() {
        userService.writeUserInfoToFileNew(100);
    }

    @Test
    void insertUserFromFileToDbNew2() {
        userService.insertUserFromFileToDbNew2();
    }
}