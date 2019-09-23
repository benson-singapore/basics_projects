# SpringCloud
> Spring Cloud 作为 Java语言的微服务框架 ，它依赖于 Spring Boot，有快速开发、持续交付和 容易部署等特点。

![](./images/SPRINGCLOUD.png ':size=170px') <span style="position: relative; top: -10px; font-size:20px; font-weight: 600; color: #55b432;">Spring Cloud</span>

- [springcloud-简介](/framework/springcloud/springcloud?id=springcloud-简介)
- [springcloud-eureka](/framework/springcloud/springcloud?id=springcloud-eureka)

### SpringCloud-简介

SpringCloud是一个基千SpringBoot实现的微服务架构开发 工具。 它为微服务架构中 涉及的 配置管理、 服务治理、 断路器、 智能路由、 微代理、 控制总线、 全局锁、 决策竞选、 分布式会话和集群状态管理等操作提供了 一 种简单的开发方式。

|  组件名称   | 所属项目  | 组件分类  |
|  :----:  | :----:  | :----:  |
|  Eureka  | spring-cloud-netflix  | 注册中心  |
|  zuul  | spring-cloud-netflix  | 第一代网关  |
|  Sidecar  | spring-cloud-netflix  | 多语言  |
|  Ribbon  | spring-cloud-netflix  | 负载均衡  |
|  Hystrix  | spring-cloud-netflix  | 熔断器  |
|  Turbine  | spring-cloud-netflix  | 集群监控  |
|  Feign  | spring-cloud-openfeign  | 声明式HTTP客户端  |
|  Consul  | spring-cloud-consul  | 注册中心  |
|  Gateway  | spring-cloud-gatewayl  | 第二代网关  |
|  Sleuth  | spring-cloud-seluth  | 链路追踪  |
|  Config  | spring-cloud-config  | 配置中心  |
|  Bus  | spring-cloud-bus | 总线  |
|  Pipeline  | spring-cloud-pipelines | 部署管道  |
|  Dataflow  | spring-cloud-dataflow | 数据处理  |


### SpringCloud-Eureka

![](./images/spring.png ':size=100') <span style="position: relative; top: -10px; font-weight: 600; color: #55b432;">EUREKA</span>

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
