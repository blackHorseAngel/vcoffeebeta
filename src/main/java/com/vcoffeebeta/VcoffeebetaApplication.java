package com.vcoffeebeta;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.vcoffeebeta.domain")
@MapperScan(basePackages = "com.vcoffeebeta.DAO")
public class VcoffeebetaApplication {

    public static void main(String[] args) {
        SpringApplication.run(VcoffeebetaApplication.class, args);
    }

}
