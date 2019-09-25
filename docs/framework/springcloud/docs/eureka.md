
# SpringCloud-Eureka

![](../images/spring.png ':size=130') <span style="position: relative; top: -10px; font-size:24px; font-weight: 600; color: #55b432;">EUREKA</span>

- [添加依赖](/framework/springcloud/docs/eureka?id=添加依赖)
- [添加系统配置文件](/framework/springcloud/docs/eureka?id=添加系统配置文件)
- [添加eureka启动类](/framework/springcloud/docs/eureka?id=添加eureka启动类)
- [启动项目](/framework/springcloud/docs/eureka?id=启动项目)
- [示例代码](/framework/springcloud/docs/eureka?id=示例代码)

>Eureka”来源于古希腊词汇，意为“发现了”。在软件领域， Eureka 是 Netflix公司开源的一个服务注册与发现的组件，和其他 Netflix 公司的服务组件(例如：负载均衡、熔 断器、网关等) 一起，被 SpringCloud社区整合为 SpringCloudNetflix模块。


#### 添加依赖

``` xml
  <dependencies>
      <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
      </dependency>

      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-test</artifactId>
          <scope>test</scope>
      </dependency>
  </dependencies>

  <dependencyManagement>
      <dependencies>
          <dependency>
              <groupId>org.springframework.cloud</groupId>
              <artifactId>spring-cloud-dependencies</artifactId>
              <version>${spring-cloud.version}</version>
              <type>pom</type>
              <scope>import</scope>
          </dependency>
      </dependencies>
  </dependencyManagement>
```

#### 添加系统配置文件

``` yaml
# eureka config
server:
  port: 8761

spring:
  application:
    name: erueka-server

eureka:
  instance:
    hostname: localhost
    prefer-ip-address: true
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  server:
    wait-time-in-ms-when-sync-empty: 0
    enable-self-preservation: false
```

?> 在默认情况下， `Eureka Server` 会向自己注册，这时需要配置`register-with-eureka`和`fetch-registry`为false，防止自己注册自己。


#### 添加Eureka启动类

```java
@EnableEurekaServer
@SpringBootApplication
public class SpringCloudEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudEurekaApplication.class, args);
    }

}
```

#### 启动项目

?> 启动项目，访问：http://localhost:8761

#### 示例代码

!> 代码参考地址：[spring-cloud-eureka](https://github.com/zhangbiy/basics_projects/tree/master/projects/springcloud/spring-cloud-eureka)
