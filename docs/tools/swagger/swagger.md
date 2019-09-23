# Swagger

![](./images/swagger_logo.png ':size=400')

- [1. swagger 安装](/tools/swagger/swagger?id=_1-swagger-安装)
  - [1.1-介绍](/tools/swagger/swagger?id=_11-介绍)
  - [1.2-springboot-集成-swagger](/tools/swagger/swagger?id=_12-springboot-集成-swagger)
- [2-swagger-注解](/tools/swagger/swagger?id=_2-swagger-注解)
  - [@api：用在请求的类上，说明该类的作用](/tools/swagger/swagger?id=api：用在请求的类上，说明该类的作用)
  - [@apioperation：用在请求的方法上，说明方法的作用](/tools/swagger/swagger?id=apioperation：用在请求的方法上，说明方法的作用)
  - [@apiimplicitparams：用在请求的方法上，包含一组参数说明](/tools/swagger/swagger?id=apiimplicitparams：用在请求的方法上，包含一组参数说明)
  - [@apiresponses：用于请求的方法上，表示一组响应](/tools/swagger/swagger?id=apiresponses：用于请求的方法上，表示一组响应)
  - [@apimodel：用于响应类上，表示一个返回响应数据的信息](/tools/swagger/swagger?id=apimodel：用于响应类上，表示一个返回响应数据的信息)
- [3-oauth20授权配置](/tools/swagger/swagger?id=_3-oauth20授权配置)
- [4-springcloud-多项目管理](/tools/swagger/swagger?id=_4-springcloud-多项目管理)

### 1. swagger 安装

#### 1.1 介绍

swagger是一个流行的API开发框架，这个框架以“开放API声明”（OpenAPI Specification，OAS）为基础， 对整个API的开发周期都提供了相应的解决方案，是一个非常庞大的项目（包括设计、编码和测试，几乎支持所有语言）。

Swagger 是一个规范和完整的框架，用于生成、描述、调用和可视化 RESTful 风格的 Web 服务。 总体目标是使客户端和文件系统作为服务器以同样的速度来更新。 文件的方法，参数和模型紧密集成到服务器端的代码，允许API来始终保持同步。Swagger 让部署管理和使用功能强大的API从未如此简单。


#### 1.2 springboot 集成 swagger

**第一步: 新建SpringBoot项目,引入依赖**

```xml
	<!--swagger-->
  <dependency>
      <groupId>io.springfox</groupId>
      <artifactId>springfox-swagger2</artifactId>
      <version>2.9.2</version>
  </dependency>
  <dependency>
      <groupId>io.springfox</groupId>
      <artifactId>springfox-swagger-ui</artifactId>
      <version>2.9.2</version>
  </dependency>
```

*上面两个依赖的作用*:

swagger2依然是依赖OSA规范文档，也就是一个描述API的json文件，而这个组件的功能就是帮助我们自动生成这个json文件， swagger-ui就是将这个json文件解析出来，用一种更友好的方式呈现出来。

**第二步:创建api**

```java
/**
 * @author admin
 */
@RestController
@RequestMapping("/api")
@Api(tags = "用户管理接口")
public class AjaxUserController {

    @ApiOperation(value = "获取用户详细信息", notes = "根据url的id来获取用户详细信息",produces = "application/json",response = User.class)
    @GetMapping(value = "/getUser/{id}",produces = "application/json")
    public Object getUser(@ApiParam(name = "id",value = "用户id",required = true) @PathVariable String id) {
        User user = Db.findFirst(User.class, "select * from t_user where id=?", id);
        return user;
    }
}

```

**配置Swagger2**

现在Swagger2还不能为我们生成API文档,因为我们还没有对它进行配置.
我们需要创建一个配置类,进行如下配置:

```java
@EnableSwagger2
@Configuration
public class Swagger2 {

    //是否开启swagger，正式环境一般是需要关闭的，可根据springboot的多环境配置进行设置
    @Value(value = "${swagger.enabled}")
    Boolean swaggerEnabled;
    @Value(value = "${group.version}")
    String version;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                // 是否开启
                .enable(swaggerEnabled).select()
                // 扫描的路径包
                .apis(RequestHandlerSelectors.basePackage("com.ins.group.controller"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(PathSelectors.any()).build().pathMapping("/");
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("团险API接口文档示例")
                .description("接口文档")
                .version(version)
                .build();
    }
}
```

**搞定**

现在我们要做的配置已经能满足一个生成API文档的基本要求了,让我们启动项目,访问:http://localhost/swagger-ui.html

> 当在使用springboot的时候如果，加入了自定义的拦截器可能会访问不到，这里需要去重写 WebMvcConfigurerAdapter 继承了的方法。
```java
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        super.addResourceHandlers(registry);
    }
```

### 2. swagger 注解

####  @Api：用在请求的类上，说明该类的作用**

```txt
	@Api：用在请求的类上，说明该类的作用
		tags="说明该类的作用"
		value="该参数没什么意义，所以不需要配置"
```

*示例：*

```java
@Api(tags="APP用户注册Controller")
```

####  @ApiOperation：用在请求的方法上，说明方法的作用**

```txt
	@ApiOperation："用在请求的方法上，说明方法的作用"
		value="说明方法的作用"
		notes="方法的备注说明"
```
*示例：*

```java
@ApiOperation(value="用户注册",notes="手机号、密码都是必输项，年龄随边填，但必须是数字")
```

####  @ApiImplicitParams：用在请求的方法上，包含一组参数说明**

```txt
	@ApiImplicitParams：用在请求的方法上，包含一组参数说明
	@ApiImplicitParam：用在 @ApiImplicitParams 注解中，指定一个请求参数的配置信息	    
	    name：参数名
	    value：参数的汉字说明、解释
	    required：参数是否必须传
	    paramType：参数放在哪个地方
	        · header --> 请求参数的获取：@RequestHeader
	        · query --> 请求参数的获取：@RequestParam
	        · path（用于restful接口）--> 请求参数的获取：@PathVariable
	        · body（不常用）
	        · form（不常用）	   
	    dataType：参数类型，默认String，其它值dataType="Integer"	   
	    defaultValue：参数的默认值
```
*示例：*

```java
@ApiImplicitParams({
	@ApiImplicitParam(name="mobile",value="手机号",required=true,paramType="form"),
	@ApiImplicitParam(name="password",value="密码",required=true,paramType="form"),
	@ApiImplicitParam(name="age",value="年龄",required=true,paramType="form",dataType="Integer")
})

```

#### @ApiResponses：用于请求的方法上，表示一组响应**

```txt
	@ApiResponses：用于请求的方法上，表示一组响应
		@ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
		    code：数字，例如400
		    message：信息，例如"请求参数没填好"
		    response：抛出异常的类
```
*示例：*

```java
@ApiOperation(value = "select1请求",notes = "多个参数，多种的查询参数类型")
@ApiResponses({
	@ApiResponse(code=400,message="请求参数没填好"),
	@ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
})
```

#### @ApiModel：用于响应类上，表示一个返回响应数据的信息**

```txt
	@ApiModel：用于响应类上，表示一个返回响应数据的信息
			（这种一般用在post创建的时候，使用@RequestBody这样的场景，
			请求参数无法使用@ApiImplicitParam注解进行描述的时候）
	@ApiModelProperty：用在属性上，描述响应类的属性
```
*示例：*

```java
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description= "返回响应数据")
public class RestMessage implements Serializable{

	@ApiModelProperty(value = "是否成功")
	private boolean success=true;
	@ApiModelProperty(value = "返回对象")
	private Object data;
	@ApiModelProperty(value = "错误编号")
	private Integer errCode;
	@ApiModelProperty(value = "错误信息")
	private String message;

	/* getter/setter */
}
```

### 3. oauth2.0授权配置

- 修改swagger配置

``` java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * swagger config
 *
 * @author zhangby
 * @date 2019-05-14 15:08
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    /**
     * Determine if swagger is on
     */
    @Value(value = "${swagger.enabled}")
    Boolean swaggerEnabled;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                // swagger is on
                .enable(swaggerEnabled).select()
                // Scanned path package
                .apis(RequestHandlerSelectors.basePackage("com.way.pacific.modules"))
                .paths(PathSelectors.any()).build().pathMapping("/")
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList = new ArrayList();
        apiKeyList.add(new ApiKey("Authorization", "Authorization", "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Pacific API DOC")
                .description("")
                .version("1.0.0")
                .build();
    }
}
```

### 4. springcloud 多项目管理

``` java
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {

    @Override
    public List<SwaggerResource> get() {
        List resources = new ArrayList<>();
        resources.add(swaggerResource("USER_SERVICE 用户服务", "/api/base/v2/api-docs", "2.0"));
        resources.add(swaggerResource("LOG_SERVICE 日志服务", "/api/log/v2/api-docs", "2.0"));
        resources.add(swaggerResource("PRODUCT_SERVICE 产品服务", "/api/pro/v2/api-docs", "2.0"));
        resources.add(swaggerResource("OPERATE_SERVICE 运营服务", "/api/oper/v2/api-docs", "2.0"));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location, String version) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion(version);
        return swaggerResource;
    }
}
```
