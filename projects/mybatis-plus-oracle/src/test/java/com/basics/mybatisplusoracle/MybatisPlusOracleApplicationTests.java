package com.basics.mybatisplusoracle;

import com.basics.mybatisplusoracle.entity.Server;
import com.basics.mybatisplusoracle.service.IServerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MybatisPlusOracleApplicationTests {

    @Autowired
    IServerService serverService;

    @Test
    public void contextLoads() {
        List<Server> list = serverService.list();
        list.forEach(System.out::println);
    }

}
