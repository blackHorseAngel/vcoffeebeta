package com.vcoffeebeta.service;

import com.vcoffeebeta.VcoffeebetaApplication;
import com.vcoffeebeta.domain.Company;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@SpringBootTest(classes = VcoffeebetaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class CompanyServiceImplTest {

    @Autowired
    private CompanyService companyService;

    @Test
    void isCompanyExist() {
    }

    @Test
    @Transactional
    void insertCompany() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        System.out.println("插入公司开始：" + startTime);
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
        System.out.println("插入公司结束：" + endTime);
        long second = endTime-startTime;
        //TODO 一共耗时6s
        System.out.println("本次插入一共耗时：" + second);
    }
    private String handlePhoneNumber(int num){
        Random random = new Random();
        int number = random.nextInt(999999999);
        String companyPhoneNumber = String.valueOf(number);
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

    @Test
//    @Transactional
    void insertBatchCompany() {
        //每个companyList的提交时候的数量
        int size = 200;
        //用来记录总条数
        int count = 0;
        List<Company>companyList = new ArrayList<>();
        long start = System.currentTimeMillis();
        while(count < 850){
            while(companyList.size() < size){
                Company company = new Company();
                Date date = new Date();
                company.setCompanyName("COMPANY" + count);
                company.setCompanyNickName("company" + count);
                String phoneNumber = handlePhoneNumber(count);
                company.setCompanyPhoneNumber(phoneNumber);
                company.setCompanyAddress("company" + count + count);
                company.setCreated("admin");
                company.setCreatedTime(date);
                company.setModified("admin");
                company.setModifiedTime(date);
                companyList.add(company);
                count++;
            }
            int result= companyService.insertBatchCompany(companyList);
            System.out.println("新增成功数据result:" + result);
            companyList.clear();
        }
        long end = System.currentTimeMillis();
        System.out.println("创建公司耗时：" + (end - start));
    }
}