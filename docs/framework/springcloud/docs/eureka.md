
# SpringCloud-Eureka

![](../images/spring.png ':size=130') <span style="position: relative; top: -10px; font-size:24px; font-weight: 600; color: #55b432;">EUREKA</span>

- [简介](/framework/springcloud/docs/eureka?id=简介)
- [添加依赖](/framework/springcloud/docs/eureka?id=添加依赖)
- [添加系统配置文件](/framework/springcloud/docs/eureka?id=添加系统配置文件)
- [添加eureka启动类](/framework/springcloud/docs/eureka?id=添加eureka启动类)
- [启动项目](/framework/springcloud/docs/eureka?id=启动项目)
- [示例代码](/framework/springcloud/docs/eureka?id=示例代码)

#### 简介

`Eureka`”来源于古希腊词汇，意为“发现了”。在软件领域， Eureka 是 Netflix公司开源的一个服务注册与发现的组件，和其他 Netflix 公司的服务组件(例如：负载均衡、熔 断器、网关等) 一起，被 SpringCloud社区整合为 SpringCloudNetflix模块。<br>
和 `Consul`、`Zookeeper`类似，Eureka是一个用于服务注册和发现的组件， 最开始主要应用于亚马逊公司旗下的云计算服务平台AWS。Eureka分为 EurekaServer和 EurekaClient , Eureka Server 为 Eureka 服务注册中心，Eureka Client 为 Eureka 客户端 。

**在 Spring Cloud 中，可选择 Consul、 Zookeeper 和 Eureka 作为服务注册和发现的组件，那为什么要选择 Eureka 呢?**

首先`Eureka`完全开源，是Netflix公司的开源产品，经历了Netflix公司的生产环境的考验，以及 3 年时间的不断法代，在功能和性能上都非常稳定，可以放心使用。其次，Eureka是 SpringCloud首选推荐的服务注册与发现组件，与SpringCloud其他组件 可以无缝对接。<br>
最后，`Eureka` 和其他组件，比如负载均衡组件 `Ribbon`、 熔断器组件`Hystrix`、熔断器监控组件 `Hystrix Dashboard` 组件、熔断器聚合监控 `Turbine` 组件，以及网关 `Zuul` 组件相互配合 ， 能够很容易实现服务注册、负载均衡、熔断和智能路由等功能 。 这些组件都是由 Netflix 公司开源的，一起被称为 Netflix OSS 组件。 Netflix OSS 组件由Spring Cloud 整合为 Spring Cloud Netflix组件，它是Spring Cloud构架微服务的核心组件，也是基础组件。

<h1>Eureka 实战<h1>

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

!> 代码参考地址：[spring-cloud-eureka](https://gitlab.com/zhangbiyu/basics_project/tree/master/projects/springcloud/spring-cloud-eureka)
