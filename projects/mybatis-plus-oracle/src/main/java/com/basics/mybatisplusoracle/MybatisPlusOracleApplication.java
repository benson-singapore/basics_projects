package com.basics.mybatisplusoracle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.basics.mybatisplusoracle.mapper")
public class MybatisPlusOracleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusOracleApplication.class, args);
    }

}
