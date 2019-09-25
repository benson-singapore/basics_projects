
# SpringCloud-Feign

![](../images/spring.png ':size=130') <span style="position: relative; top: -10px;font-size:24px;font-weight: 600; color: #55b432;">FEIGN</span>

#### 简介

`Feign`是--个声明式的WebService客户端。它的出现使开发WebService客户端变得 很简单。使用Feign只需要创建一个接口加上对应的注解，比如: FeignClient 注解。Feign 有可插拔的注解，包括Feign 注解和JAX-RS注解。Feign也支持编码器和解码器，Spring Cloud Open Feign对Feign进行增强支持Spring MVC注解，可以像Spring Web-样使用 HttpMessageConverters等。

`Feign`是一-种声明式、模板化的HTTP客户端。在Spring Cloud中使用Feign,可以做到使 用HTTP请求访问远程服务，就像调用本地方法-样的， 开发者完全感知不到这是在调用远程 方法，更感知不到在访问HTtp请求。接下来介绍-下Feign的特性，具体如下:
- 可插拔的注解支持，包括Feign注解和JAX-RS注解。
- 支持可插拔的HTTP编码器和解码器。
- 支持`Hystrix`和它的Fallback。
- 支持`Ribbon`的负载均衡。
- 支持HTTP请求和响应的压缩。Feign是-一个声明式的WebService客户端，它的目的就是让Web Service 调用更加简单。它整合了Ribbon和Hystrix,从而不需要开发者针 对Feign对其进行整合。Feign 还提供了HTTP请求的模板，通过编写简单的接口和注 解，就可以定义好HTTP请求的参数、格式、地址等信息。Feign 会完全代理HTTP的 请求，在使用过程中我们只需要依赖注人Bean,然后调用对应的方法传递参数即可。
