# 运行配置

本项目使用 Nacos 共享配置与 JVM 环境变量。仓库中的 `bootstrap.yml` 保留服务名、端口和业务开关；环境相关的地址、密码和密钥由部署者提供。

## Nacos 与基础设施

| 环境变量 | 默认值 | 用途 |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | `local` | Spring 环境名称 |
| `NACOS_SERVER_ADDR` | `localhost:8848` | Nacos 地址 |
| `NACOS_NAMESPACE` | 空，使用 public | 配置与服务发现的命名空间 ID |
| `NACOS_GROUP` | `DEFAULT_GROUP` | 应用配置与服务发现分组 |
| `NACOS_USERNAME` / `NACOS_PASSWORD` | 空 | Nacos 认证信息 |
| `ELASTICSEARCH_URIS` | `http://localhost:9200` | 搜索服务地址 |
| `ELASTICSEARCH_USERNAME` / `ELASTICSEARCH_PASSWORD` | 空 | 搜索服务认证信息 |

每个服务读取其 `bootstrap.yml` 中列出的 Data ID；共享配置默认使用 `DEFAULT_GROUP`。若修改分组，请同时为 `shared-configs` 中的条目显式设置对应 `group`。

| Data ID | 配置内容 |
| --- | --- |
| `shared-spring.yaml` | 公共 Spring 设置 |
| `shared-mybatis.yaml` | 数据源、MyBatis-Plus 与分页相关设置 |
| `shared-redis.yaml` | Redis 连接与连接池 |
| `shared-mq.yaml` | RabbitMQ 连接、消费者和重试设置 |
| `shared-feign.yaml` | Feign 超时、连接池和调用设置 |
| `shared-logs.yaml` | 日志级别与输出 |
| `shared-xxljob.yaml` | XXL-JOB 调度中心与执行器设置 |
| `shared-seata.yaml` | Seata 事务分组和服务端连接 |

以下片段展示最小连接配置，需分别保存到对应 Data ID。它们不能替代数据库表结构、队列规划或完整业务配置。

### MySQL：`shared-mybatis.yaml`

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${tj.jdbc.database}?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: ${MYSQL_USERNAME}
    password: ${MYSQL_PASSWORD}
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  configuration:
    map-underscore-to-camel-case: true
```

`${tj.jdbc.database}` 来自各服务自己的 `bootstrap.yml`，例如 `tj_user`、`tj_course`、`tj_learning`。仓库不包含建表、初始化权限和账号数据，需要自行准备匹配实体与 Mapper 的 SQL。

### Redis：`shared-redis.yaml`

```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
```

### RabbitMQ：`shared-mq.yaml`

```yaml
spring:
  rabbitmq:
    host: ${RABBITMQ_HOST:localhost}
    port: ${RABBITMQ_PORT:5672}
    virtual-host: ${RABBITMQ_VHOST:/}
    username: ${RABBITMQ_USERNAME}
    password: ${RABBITMQ_PASSWORD}
    publisher-confirm-type: correlated
    publisher-returns: true
    template:
      mandatory: true
```

### XXL-JOB：`shared-xxljob.yaml`

```yaml
tj:
  xxl-job:
    access-token: ${XXL_JOB_ACCESS_TOKEN:}
    admin:
      address: ${XXL_JOB_ADMIN_ADDRESS}
    executor:
      app-name: ${spring.application.name}
      port: ${XXL_JOB_EXECUTOR_PORT:0}
      log-path: ${XXL_JOB_LOG_PATH:./logs/xxl-job}
      log-retention-days: 7
```

Seata 的事务分组映射、注册中心和服务端地址需与实际部署一致。其余共享配置请根据业务需求补齐；检查服务启动日志，确认没有缺失的 Data ID 或未解析的占位符。

## JWT 密钥库

认证服务使用 RSA 密钥库签发 Token。使用 JDK 自带的 `keytool` 创建自己的密钥库，并在交互提示中设置密码：

```bash
keytool -genkeypair -alias tianji -keyalg RSA -keysize 2048 -storetype JKS -keystore tianji.jks -validity 3650
```

将密钥库保存在仓库外，通过下列环境变量提供给认证服务：

| 环境变量 | 内容 |
| --- | --- |
| `JWT_KEY_STORE` | Spring 资源地址，例如 `file:/opt/tianji/secrets/tianji.jks` |
| `JWT_KEY_ALIAS` | 密钥别名，默认 `tianji` |
| `JWT_KEY_STORE_PASSWORD` | 密钥库密码 |
| `JWT_KEY_PASSWORD` | 私钥密码；按生成密钥时的设置填写 |

Windows 路径示例：`file:/D:/secrets/tianji.jks`。不要把密钥库复制到 `src/main/resources` 后提交。

## 云服务与支付

以下变量由实际启用的服务读取。无默认值的 `${NAME}` 必须提供；部分第三方 SDK 会在服务启动时初始化，不能仅通过不访问页面来省略其凭据。

| 服务 | 环境变量 |
| --- | --- |
| 腾讯云媒资 | `TENCENT_APP_ID`、`TENCENT_SECRET_ID`、`TENCENT_SECRET_KEY`、`TENCENT_COS_BUCKET`、`TENCENT_VOD_URL_KEY` |
| 腾讯云视频处理 | `TENCENT_VOD_PROCEDURE`，需对应账号内的处理流程 |
| 阿里云短信 | `ALI_SMS_ACCESS_ID`、`ALI_SMS_ACCESS_SECRET` |
| 支付回调 | `PAY_NOTIFY_HOST`，外部可访问的支付服务路由地址 |
| 支付宝 | `ALIPAY_APP_ID`、`ALIPAY_PRIVATE_KEY`、`ALIPAY_PUBLIC_KEY` |
| 微信支付 | `WECHAT_APP_ID`、`WECHAT_MCH_ID`、`WECHAT_MCH_SERIAL_NO`、`WECHAT_PRIVATE_KEY_PATH`、`WECHAT_API_V3_KEY` |

平台证书、私钥和商户参数应由自己的账号生成。回调域名需按平台要求配置。集成测试也应使用独立测试账号。

包含真实外部操作的测试默认关闭。短信测试需设置 `RUN_SMS_INTEGRATION_TESTS=true` 与 `SMS_TEST_PHONE`；用户写入测试需设置 `RUN_USER_INTEGRATION_TESTS=true`、`TEST_STUDENT_PHONE_BASE` 与 `TEST_STUDENT_PASSWORD`；支付测试需设置 `RUN_PAYMENT_INTEGRATION_TESTS=true`、上述支付凭据及 `ALIPAY_TEST_ORDER_NO`、`ALIPAY_TEST_REFUND_NO_1`、`ALIPAY_TEST_REFUND_NO_2`。仅在独立测试环境中启用。

## 环境变量的加载方式

Spring Boot 不会自动读取项目根目录的 `.env`。后端变量需通过操作系统、IDE 运行配置、容器或进程管理器注入，再启动 JVM。例如 PowerShell：

```powershell
$env:NACOS_SERVER_ADDR = 'localhost:8848'
$env:NACOS_NAMESPACE = ''
java -jar tj-gateway/target/tj-gateway.jar
```

以上示例仅设置 Nacos 地址；其他变量仍须按所启动服务补齐。不要将实际凭据保存到版本控制中的脚本。

前端由 Vite 读取 `frontend/.env.local`，可参考 [`.env.example`](../frontend/.env.example)。`VITE_*` 会被编译进浏览器代码，不能存放密钥。
