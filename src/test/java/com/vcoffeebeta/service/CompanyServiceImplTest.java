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

import static org.junit.jupiter.api.Assertions.*;
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
//        Thread.sleep(15000);
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
//        String index = "0101111111";
//        String companyPhoneNumber =index + num;
//        while(companyPhoneNumber.length() > 11){
//            index = index.substring(0,11-String.valueOf(num).length());
//            companyPhoneNumber = index + num;
//        }
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

        /*List<Company> companyList = new ArrayList<>();
        //每次200条记录，834条记录耗时           1643ms  1689ms   1606ms
        //每次400条记录，834条记录耗时 2s204 ms  1700ms  1722ms  1667ms
        int step =200;
        int times = 840/step;
        int count = 0;
        long startTime = System.currentTimeMillis();
        System.out.println("批量插入公司开始：" + startTime);
        for(int k = 0  ; k < times ; k++){
            for(int i = 0 ; i < step ; i++){
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
            companyList.clear();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("批量插入公司结束：" + endTime);
        long second = endTime-startTime;
        System.out.println("本次插入一共耗时：" + second);
    */
        //每个companyList的提交时候的数量
        int size = 200;
        //用来记录总条数
        int count = 0;
        List<Company>companyList = new ArrayList<>();
        long start = System.currentTimeMillis();
//        StopWatch watch = new StopWatch();
//        watch.start();
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
//        watch.stop();
//        System.out.println("创建公司数据耗时：" + watch.prettyPrint());
    }
}