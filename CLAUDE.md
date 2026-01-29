# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

**筱锋通用公共仓库 (general-utils)** - 多模块基础工具库，为 Spring Boot 应用提供统一的工具类、配置管理和增强功能。

- **当前版本**: 2.0.0-beta1
- **Java 版本**: 17
- **构建工具**: Maven
- **许可证**: MIT

## 模块架构

本项目采用分层模块设计，依赖关系清晰：

```
bamboo-base (纯基础)
    ↓
    ├─→ bamboo-mvc (Spring MVC 适配)
    ├─→ bamboo-webflux (Spring WebFlux 适配)
    ├─→ bamboo-triple (Dubbo Triple 协议)
    └─→ bamboo-notify (通知服务)
```

### bamboo-base（纯基础模块）
**定位**: 零 Web 框架依赖的纯 Java 工具库

**核心功能**:
- `BaseResponse<E>` - 通用响应数据模型（支持 context 链路追踪和 duration 性能监控）
- `ErrorCode` - 统一错误码枚举
- 异常类体系: `BusinessException`, `RequestException` 等
- 工具类: `SnowflakeUtil`, `GeneSnowflakeUtil`, `EncryptUtil`, `UuidUtil`, `ConvertUtil`, `PasswordUtil`, `IpUtil` 等
- ID 生成器: `SnowflakeIdGenerator`, `UuidV7Generator` 等
- 常量类: `StringConstant`, `RegexConstant`
- Spring Boot 自动配置支持（通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`）

**重要**: 所有依赖都是 `provided` 作用域，由用户项目提供实际依赖。

### bamboo-mvc（MVC 适配模块）
**定位**: Spring MVC 同步环境的增强工具

**核心功能**:
- `ResultUtil` - 返回 `ResponseEntity<BaseResponse<T>>`，自动注入 context 和 duration
- `ContextHolder` - 基于 `ThreadLocal` 的上下文管理
- `HeaderUtil` - HttpServletRequest 工具
- `ExceptionHandler` - 全局异常处理
- `Filter` - 请求过滤器
- `AOP` 切面 - 日志、权限等

**重要**: 使用 `ThreadLocal` 管理请求上下文，适用于传统的阻塞式 Servlet 容器。

### bamboo-webflux（WebFlux 适配模块）
**定位**: Spring WebFlux 响应式环境的增强工具

**核心功能**:
- `ResultUtil` - 返回 `Mono<ResponseEntity<BaseResponse<T>>>`，响应式风格
- `ContextHolder` - 基于 `Reactor Context` 的上下文管理
- `WebExceptionHandler` - 响应式全局异常处理
- `WebFilter` - 响应式过滤器

**重要**: 使用 `Reactor Context` 管理请求上下文，完全异步非阻塞，不依赖 ThreadLocal。

### bamboo-triple（Triple 协议模块）
**定位**: Dubbo Triple 协议增强

### bamboo-notify（通知服务模块）
**定位**: 通知服务基础结构

## 常用命令

### 构建与测试

```bash
# 清理并编译整个项目
mvn clean compile

# 运行所有测试
mvn test

# 运行单个模块测试
mvn test -pl bamboo-base

# 运行单个测试类
mvn test -Dtest=SnowflakeUtilTest -pl bamboo-base

# 打包（跳过测试）
mvn clean package -DskipTests

# 安装到本地仓库
mvn clean install -DskipTests
```

### 发布到 Maven 中央仓库

```bash
# 完整的发布流程（清理、验证、签名、部署）
mvn clean deploy -P release

# 只构建不部署（检查 GPG 签名）
mvn clean verify
```

**注意**: 发布需要配置 GPG 签名和 Sonatype 凭证。

## 代码规范

### 1. `this.` 使用规则（严格遵守）
- ✅ **调用内部方法或继承的方法时**: 必须使用 `this.`
  ```java
  this.processData();  // 正确
  ```

- ❌ **访问成员变量时**: 禁止使用 `this.`
  ```java
  private String name;
  System.out.println(name);  // 正确
  System.out.println(this.name);  // 错误！不要这样写
  ```

### 2. Optional 使用规范
在性能影响可忽略的情况下，优先使用 `Optional` 进行优雅判空，增强可读性：
```java
Optional.ofNullable(data)
    .map(Item::getValue)
    .orElse(defaultValue);
```

### 3. 代码风格
- 追求完美、优雅且高可读性的代码风格
- 所有公共 API 必须有完整的 JavaDoc 注释
- 使用 `@NotNull`, `@Nullable`, `@Contract` 等注解增强代码可读性
- 异常处理要具体，避免捕获过于宽泛的异常

### 4. 测试规范
- 在启动新测试服务前，先检查端口是否占用（假设用户正在运行则直接测试）
- BugFix 修复后，必须执行最终的代码逻辑检查或编译检查，确保无误

## Spring Boot 自动配置

项目使用 Spring Boot 3.x 的自动配置机制：

1. **配置文件位置**: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
2. **自动配置类**:
   - `bamboo-base`: `BaseCoreAutoConfiguration`, `CoreDatabaseAutoConfiguration`
   - `bamboo-mvc`: 通过 imports 文件导入
   - `bamboo-webflux`: 通过 imports 文件导入

3. **配置属性**:
   - `utility.base.snowflake.*` - 雪花 ID 生成器配置
   - `utility.base.*` - 其他基础配置

## 架构设计原则

### MVC vs WebFlux 的差异处理

本项目针对两种编程模型提供了不同的实现：

| 特性 | bamboo-mvc | bamboo-webflux |
|------|------------|----------------|
| 返回类型 | `ResponseEntity<BaseResponse<T>>` | `Mono<ResponseEntity<BaseResponse<T>>>` |
| 上下文管理 | `ThreadLocal` | `Reactor Context` |
| 异常处理 | `@ControllerAdvice` | `@WebExceptionHandler` |
| 过滤器 | `Filter` | `WebFilter` |
| 编程模型 | 阻塞式，命令式 | 非阻塞，响应式 |

**重要**: 当为用户项目提供支持时，根据项目类型（MVC 或 WebFlux）选择对应的模块。

### 依赖管理策略

所有模块的外部依赖都设置为 `provided` 作用域：
- 避免版本冲突
- 减小最终产物体积
- 由用户项目统一管理依赖版本

**例外**: `uuid-creator` 是唯一 compile 作用域的依赖（用于 UUID v7 生成）。

## CI/CD

- **GitHub Actions**: `.github/workflows/deploy_release.yaml`
- **触发条件**: 推送到 `master` 分支
- **流程**: 检出代码 → 设置 JDK 17 → Maven 构建 → 创建 GitHub Release

**Jenkins**: 支持 `Jenkinsfile` 部署流程。

## 版本管理

- 版本号格式: `major.minor.patch-beta1`
- 主分支: `master`
- 开发分支: `feature/*`
- 所有模块共享同一个版本号（在父 POM 中定义）

## 特殊注意事项

1. **Snowflake ID 配置**: 使用前需要配置数据中心 ID 和机器 ID（通过 `UtilityBaseProperties`）
2. **MyBatis-Plus 集成**: 提供了自定义的 `IdentifierGenerator` 实现
3. **异常处理**: 区分 `BusinessException`（业务异常）和 `RequestException`（请求异常）
4. **日志转换**: `MethodTypeConverter` 自定义 Logback 日志格式
5. **GPG 签名**: 发布到中央仓库需要 GPG 签名配置

## 常见开发场景

### 添加新的工具类
1. 在 `bamboo-base` 的 `utility` 包下创建
2. 确保是纯 Java 实现，无 Web 框架依赖
3. 添加完整的 JavaDoc 和单元测试

### 添加 MVC/WebFlux 增强
1. `bamboo-mvc`: 使用 `ThreadLocal` 管理上下文，返回 `ResponseEntity`
2. `bamboo-webflux`: 使用 `Reactor Context` 管理上下文，返回 `Mono`
3. 保持功能一致性，API 尽可能相似

### 修改自动配置
1. 修改对应的 `AutoConfiguration` 类
2. 更新 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 文件
3. 测试自动配置是否生效（使用 `@SpringBootTest`）
