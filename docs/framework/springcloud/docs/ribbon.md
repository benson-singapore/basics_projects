![](./images/spring.png ':size=100') <span style="position: relative; top: -10px; font-weight: 600; color: #55b432;">RIBBON</span>

>`Spring Cloud Ribbon` 是一个基于 HTTP 和 TCP 的客户端负载均衡工具，它基于 Netflix Ribbon实现。 通过SpringCloud的封装，可以让我们轻松地将`面向服务`的REST模板请求,自动转换成`客户端负载均衡`的服务调用。

?> 此外，`Feign` 与`Zuul`中已经默认集成了Ribbon,在我们的服务之间凡是涉及调用的，都可以集成它并应用，从而使我们的调用链具备良好的伸缩性。

#### 构建maven父级pom文件

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.8.RELEASE</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <groupId>com.basics</groupId>
    <artifactId>spring-cloud-ribbon</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>spring-cloud-ribbon</name>
    <description>SpringCloud 负载均衡组件 Ribbon</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <packaging>pom</packaging>

    <modules>
        <module>eureka-server</module>
        <module>client-service-one</module>
        <module>client-service-two</module>
    </modules>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
```

#### 创建Eureka注册中心服务

?> eureka 注册中心配置参考 [springcloud-eureka](/framework/springcloud/springcloud?id=springcloud-eureka)

#### 创建服务工程 client-one

- 添加项目依赖

``` xml
  <dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
	</dependencies>
```

> 增加eureka客户端的依赖 spring-cloud-starter-netflix-eureka-client。

- 修改系统配置文件

``` yaml
server:
  port: 8080

spring:
  application:
    name: client-service-one

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    ip-address: true
```

- 修改启动项增加访问接口

``` java
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class ClientServiceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientServiceOneApplication.class, args);
    }

    @GetMapping("/test")
    public Object test() {
        return "this is client-service-one";
    }

}
```

?>  `@EnableDiscoveryClient` 启用eureka客户端模式。

#### 创建Ribbon工程 client-two

- 添加项目依赖

``` xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    </dependency>
</dependencies>
```

> 增加ribbon依赖 `spring-cloud-starter-netflix-ribbon`。

- 修改系统配置信息

``` yaml
server:
  port: 8081

spring:
  application:
    name: client-service-two

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    ip-address: true
```

- 增加eureka启动项，同时增加RestTemplate注入Bean，声明该方式用于负载均衡。

``` java
@EnableDiscoveryClient
@SpringBootApplication
public class ClientServiceTwoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientServiceTwoApplication.class, args);
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

- 添加controller，调用客户端接口

``` java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ClientController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public Object test() {
        return restTemplate.getForObject("http://CLIENT-SERVICE-ONE/test",String.class);
    }
}
```

#### 启动测试

?> 先启动注册中心 `eureka`，然后分别启动`client-service-one`、`client-service-two`。<br><br>
启动成功后访问：[http://localhost:8761](http://localhost:8761)，会发现注册中心两个服务都已经注册成功。<br><br>
![](./images/WX20190924-145520.png)<br><br>
测试调用ribbon服务，可以直接调用到client-one：http://localhost:8081/test

#### 示例代码

!> 代码参考地址：[spring-cloud-ribbon](https://github.com/zhangbiy/basics_projects/tree/master/projects/springcloud/spring-cloud-ribbon)
