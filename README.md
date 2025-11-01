# 苍穹外卖

## 项目介绍

苍穹外卖是一个基于Spring Boot的餐饮外卖系统，包含管理端和用户端微信小程序。系统实现了用户下单、商家接单、商品管理、订单管理等功能。

## 技术栈

### 后端
- Spring Boot
- MyBatis
- Redis
- JWT
- MySQL
- Swagger

### 前端
- 管理端：Vue.js + Element UI
- 用户端：微信小程序

## 项目结构

```
sky-take-out/
├── sky-common/        # 公共模块，包含工具类、常量等
├── sky-pojo/          # 实体类模块
├── sky-server/        # 服务端模块，包含控制器、服务、数据访问等
├── mp-weixin/         # 微信小程序端
└── project-rjwm-admin-vue-ts/ # 管理端前端项目
```

## 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 5.7+
- Redis 5.0+
- Node.js 12+
- 微信开发者工具

## 本地开发配置

由于配置文件包含敏感信息未推送到仓库，您需要在本地创建以下配置文件：

### 1. application.yml

在 `sky-server/src/main/resources/` 目录下创建 `application.yml` 文件，内容如下：

```yaml
server:
  port: 8080

spring:
  profiles:
    active: dev
  main:
    allow-circular-references: true
  datasource:
    druid:
      driver-class-name: ${sky.datasource.driver-class-name}
      url: jdbc:mysql://${sky.datasource.host}:${sky.datasource.port}/${sky.datasource.database}?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
      username: ${sky.datasource.username}
      password: ${sky.datasource.password}

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.sky.entity
  configuration:
    map-underscore-to-camel-case: true

logging:
  level:
    com:
      sky:
        mapper: debug
        service: info
        controller: info

sky:
  jwt:
    # 管理端JWT配置
    admin-secret-key: 自定义
    admin-ttl: 7200000
    admin-token-name: token
    # 用户端JWT配置
    user-secret-key: 自定义
    user-ttl: 7200000
    user-token-name: authentication
  alioss:
    endpoint: ${sky.alioss.endpoint}
    access-key-id: ${sky.alioss.access-key-id}
    access-key-secret: ${sky.alioss.access-key-secret}
    bucket-name: ${sky.alioss.bucket-name}
  redis:
    host: ${sky.redis.host}
    port: ${sky.redis.port}
    password: ${sky.redis.password}
    database: ${sky.redis.database}
  shop:
    address: 您的商店地址
```

### 2. application-dev.yml

在 `sky-server/src/main/resources/` 目录下创建 `application-dev.yml` 文件，内容如下：

```yaml
sky:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: sky_take_out
    username: 您的数据库用户名
    password: 您的数据库密码
  alioss:
    endpoint: 您的阿里云OSS终端节点
    access-key-id: 您的阿里云AccessKeyId
    access-key-secret: 您的阿里云AccessKeySecret
    bucket-name: 您的阿里云OSS存储桶名称
  redis:
    host: 您的Redis主机地址
    port: 6379
    password: 您的Redis密码（如果有）
    database: 0
  wechat:
    appid: 您的微信小程序appid
    secret: 您的微信小程序secret
```

## 启动项目

### 后端启动
1. 导入数据库脚本（脚本文件位于项目根目录的`sql`文件夹中）
2. 配置好application.yml和application-dev.yml
3. 运行SkyApplication.java启动后端服务

### 管理端启动
```bash
cd project-rjwm-admin-vue-ts
npm install
npm run serve
```

### 微信小程序启动
1. 使用微信开发者工具打开mp-weixin目录
2. 在工具中配置您的AppID
3. 编译运行小程序

## 功能特性

- 管理端：员工管理、分类管理、菜品管理、套餐管理、订单管理等
- 用户端：微信登录、商品浏览、购物车、下单、支付、订单查询等

## 注意事项

- 请确保Redis服务已启动
- 请确保MySQL服务已启动
- 微信小程序需要在微信开发者工具中调试
- 阿里云OSS需要自行配置