# SpringBoot

> Springboot 日常笔记

## 读取系统配置文件

> springboot 配置文件，默认支持properties，yml 所以两者通用

- application.yml

``` yaml
# 自定义配置信息
grasp:
  # 中心服务
  ws: http://********
  # 需要推送的服务IP
  server:
    - ********
    - ********
  # url
  lotteryUrl:
      magNum: https://********
      daMaCai: https:/********
```

- GraspConfig.java

``` java
import cn.hutool.core.lang.Dict;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 服务配置信息 entity
 *
 * @author zhangby
 * @date 2/9/19 1:58 pm
 */
@Component
@ConfigurationProperties(prefix = "grasp")
@Data
public class GraspConfig {
    private String ws;
    private List<String> server;
    private Dict lotteryUrl;
}
```

?> @Data 注解：用的lombok，默认生成 set get方法


- 调用时

``` java
/**
 * 静态加载配置信息
 */
public static GraspConfig graspConfig = SpringContextUtil.getBean(GraspConfig.class);

/**
 * 动态加载
 */
@Autowired
private GraspConfig graspConfig;


/**
* 动态加载直接获取配置文件
*/
@Value("${grasp.lotteryUrl.magNum}")
private String url;

```
