# FengUtil
筱锋通用业务工具库

## 📦 模块说明

### bamboo-base（纯基础模块）
**定位**：零 Web 框架依赖的纯 Java 工具库

**包含内容**：
- ✅ BaseResponse（通用响应数据模型）
- ✅ ErrorCode（错误码枚举）
- ✅ 异常类（BusinessException、RequestException 等）
- ✅ 工具类（SnowflakeUtil、EncryptUtil、UuidUtil 等）
- ✅ 常量类（StringConstant、RegexConstant 等）
- ✅ ID 生成器（SnowflakeIdGenerator、UuidV7Generator 等）
- ✅ 数据模型（DTO、DO）

**不包含**：
- ❌ Web 框架相关代码（ResponseEntity、HttpServletRequest 等）
- ❌ AOP 切面实现
- ❌ Servlet API 依赖

### bamboo-mvc（MVC 适配模块）
**定位**：Spring MVC 同步环境的增强工具

**包含内容**：
- ✅ ResultUtil（同步 ResponseEntity 版本，自动注入 context 和 duration）
- ✅ HeaderUtil（HttpServletRequest 工具）
- ✅ ContextHolder（ThreadLocal 上下文管理）
- ✅ 异常处理器（ExceptionHandler）
- ✅ 过滤器（Filter）
- ✅ AOP 切面（Aspect）

### bamboo-webflux（WebFlux 适配模块）
**定位**：Spring WebFlux 响应式环境的增强工具

**包含内容**：
- ✅ ResultUtil（响应式 Mono 版本，自动注入 context 和 duration）
- ✅ ContextHolder（Reactor Context 管理）
- ✅ 异常处理器（WebExceptionHandler）
- ✅ 过滤器（WebFilter）

### bamboo-triple（Triple 协议模块）
**定位**：Dubbo Triple 协议增强

### bamboo-notify（通知服务模块）
**定位**：通知服务基础结构

## 🔗 模块依赖关系

```
bamboo-base (纯基础)
    ↓
    ├─→ bamboo-mvc (MVC 适配)
    ├─→ bamboo-webflux (WebFlux 适配)
    ├─→ bamboo-triple (Triple 协议)
    └─→ bamboo-notify (通知服务)
```

## 📝 版本信息

当前版本：2.0.0-beta1
