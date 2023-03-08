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
    void insertCompany() {
        Company company = new Company();
        company.setCompanyName("A00");
        company.setCompanyNickName("a00");
        company.setCompanyPhoneNumber("111111111110");
        company.setCompanyAddress("aa00");
        company.setCreated("admin");
        company.setCreatedTime(new Date());
        company.setModified("admin");
        company.setModifiedTime(new Date());
        boolean flag = companyService.insertCompany(company);
        System.out.println(flag);
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