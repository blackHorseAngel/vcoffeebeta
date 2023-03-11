package com.vcoffeebeta.service;

import com.vcoffeebeta.VcoffeebetaApplication;
import com.vcoffeebeta.domain.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(classes = VcoffeebetaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CompanyServiceImplTest {

    @Autowired
    private CompanyService companyService;

    @Test
    void isCompanyExist() {
    }

    @Test
    void insertCompany() throws InterruptedException {
        /*Company company = new Company();
        company.setCompanyName("A00");
        company.setCompanyNickName("a00");
        company.setCompanyPhoneNumber("111111111110");
        company.setCompanyAddress("aa00");
        company.setCreated("admin");
        company.setCreatedTime(new Date());
        company.setModified("admin");
        company.setModifiedTime(new Date());
        boolean flag = companyService.insertCompany(company);
        System.out.println(flag);*/
        Thread.sleep(15000);
        long startTime = System.currentTimeMillis();
        System.out.println("批量插入公司开始：" + startTime);
        for(int i = 0 ; i < 834 ; i++){
            Company company = new Company();
            company.setCompanyName("COMPANY"+i);
            company.setCompanyNickName("company"+i);
            String phoneNumber = handlePhoneNumber(i);
            company.setCompanyPhoneNumber(phoneNumber);
            company.setCompanyAddress("company"+i+i);
            company.setCreated("admin");
            company.setCreatedTime(new Date());
            company.setModified("admin");
            company.setModifiedTime(new Date());
            boolean flag = companyService.insertCompany(company);
            if(!flag){
                System.out.println("第" + i + "条记录插入失败");
                break;
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("批量插入公司结束：" + endTime);
        long second = (endTime-startTime)/1000;
        System.out.println("本次插入一共耗时：" + second);
    }
    private String handlePhoneNumber(int num){
        String index = "0101111111";
        String companyPhoneNumber =index + num;
        while(companyPhoneNumber.length() > 11){
            index = index.substring(0,11-String.valueOf(num).length());
            companyPhoneNumber = index + num;
        }
        return companyPhoneNumber;
    }

    @Test
    void query() {
    }

    @Test
    void queryForAmount() {
    }

    @Test
    void findAllCompany() {
    }

    @Test
    void queryById() {
    }

    @Test
    void updateCompany() {
    }

    @Test
    void deleteCompany() {
    }

    @Test
    void batchDeleteCompany() {
    }
}