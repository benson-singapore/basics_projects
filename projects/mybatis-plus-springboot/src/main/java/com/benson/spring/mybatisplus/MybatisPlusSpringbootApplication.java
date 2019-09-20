package com.benson.spring.mybatisplus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主方法
 *
 * @author zhangby
 * @date 20/9/19 2:58 pm
 */
@SpringBootApplication
@MapperScan("com.benson.spring.mybatisplus.mapper")
public class MybatisPlusSpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisPlusSpringbootApplication.class, args);
    }

}
