![](./images/spring.png ':size=100') <span style="position: relative; top: -10px; font-weight: 600; color: #55b432;">RIBBON</span>

>`Spring Cloud Ribbon` 是一个基于 HTTP 和 TCP 的客户端负载均衡工具，它基于 Netflix Ribbon实现。 通过SpringCloud的封装，可以让我们轻松地将`面向服务`的REST模板请求,自动转换成`客户端负载均衡`的服务调用。

?> 此外，`Feign` 与`Zuul`中已经默认集成了Ribbon,在我们的服务之间凡是涉及调用的，都可以集成它并应用，从而使我们的调用链具备良好的伸缩性。

#### 添加依赖
