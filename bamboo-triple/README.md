# Bamboo Triple 模块

## 概述

本模块提供 Dubbo Triple 协议的统一请求响应封装，适用于所有基于 Dubbo Triple 协议的 RPC 服务。

## 模块结构

```
bamboo-triple/
└── src/main/java/com/xlf/utility/triple/
    ├── annotations/              # 注解定义
    │   └── TripleRequestCheck.java  # 请求校验注解
    ├── aspect/                   # 切面处理
    │   └── TripleRequestCheckAspect.java  # 请求校验切面
    ├── interceptor/              # 拦截器目录（预留）
    ├── TripleRequest.java        # 请求基类
    ├── TripleResponse.java       # 响应类
    └── TripleResult.java         # 响应工具类
```

## 核心类说明

### 1. TripleRequest
Triple 协议统一请求基类，所有基于 Dubbo Triple 协议的 RPC 请求都应该继承此基类。

**主要字段**：
- `ctx`: 上下文信息（用于链路追踪、身份验证等）
- `timestamp`: 请求时间戳（毫秒）
- `source`: 请求来源标识
- `version`: API版本号

**主要方法**：
- `init(String source)`: 初始化请求参数
- `validate()`: 验证请求参数的完整性（抽象方法，需要子类实现）
- `getSummary()`: 获取请求摘要信息（抽象方法，需要子类实现）

### 2. TripleResponse<T>
Triple 协议统一响应格式，所有基于 Dubbo Triple 协议的 RPC 服务都应该使用此响应格式。

**主要字段**：
- `context`: 请求上下文UUID（用于链路追踪）
- `success`: 响应是否成功
- `code`: 响应状态码
- `message`: 响应消息
- `data`: 响应数据
- `duration`: 接口执行耗时（毫秒，用于性能监控）
- `timestamp`: 响应时间戳（毫秒）

### 3. TripleResult
Triple 协议统一响应工具类，提供便捷的方法用于构建基于 TripleResponse 的成功与失败响应。

**主要方法**：
- `success(T data)`: 创建成功响应（带数据）
- `success()`: 创建成功响应（无数据）
- `success(T data, String message)`: 创建成功响应（带自定义消息）
- `error(ErrorCode errorCode)`: 创建失败响应（使用错误码）
- `error(ErrorCode errorCode, String errorMessage)`: 创建失败响应（带详细错误信息）
- `error(ErrorCode errorCode, String errorMessage, T data)`: 创建失败响应（带数据和详细错误信息）
- `error(Exception exception)`: 创建失败响应（系统异常）

### 4. TripleRequestCheck
Triple Request 参数校验注解，用于标记需要进行 TripleRequest 参数校验的 Dubbo 服务方法或类。

**主要属性**：
- `message`: 自定义错误消息
- `errorCode`: 指定校验失败时的错误码（默认为 ErrorCode.PARAMETER_INVALID）

### 5. TripleRequestCheckAspect
Triple Request 参数校验切面处理器，通过拦截标记了 @TripleRequestCheck 注解的方法或类，自动对 TripleRequest 参数执行校验逻辑。

## 使用示例

### 1. 定义请求类

```java
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest extends TripleRequest {

    private String username;
    private String email;

    @Override
    public boolean validate() {
        return username != null && !username.trim().isEmpty()
            && email != null && !email.trim().isEmpty();
    }

    @Override
    public String getSummary() {
        return String.format("UserRequest{username='%s', email='%s'}", username, email);
    }
}
```

### 2. 定义服务实现

```java
@Service
public class UserServiceImpl implements UserService {

    @TripleRequestCheck
    public TripleResponse<UserInfo> getUser(UserRequest request) {
        // request 会自动调用 request.validate() 进行校验
        // 只有校验通过才会执行到这里
        UserInfo userInfo = userService.findByUsername(request.getUsername());
        return TripleResult.success(userInfo);
    }

    @TripleRequestCheck(message = "用户查询参数不完整")
    public TripleResponse<List<UserInfo>> listUsers(UserRequest request) {
        List<UserInfo> users = userService.listAll();
        return TripleResult.success(users);
    }
}
```

### 3. 客户端调用

```java
// 初始化请求
UserRequest request = UserRequest.builder()
    .username("test")
    .email("test@example.com")
    .build()
    .init("client-app");

// 调用服务
TripleResponse<UserInfo> response = userService.getUser(request);

// 处理响应
if (response.getSuccess()) {
    UserInfo userInfo = response.getData();
    System.out.println("用户信息: " + userInfo);
} else {
    System.err.println("请求失败: " + response.getMessage());
}
```

## 依赖说明

本模块依赖以下模块：
- `bamboo-base`: 提供 ErrorCode 枚举和 BusinessException 异常类
- `bamboo-mvc`: 提供 ContextHolder 上下文管理器

## 注意事项

1. **请求校验**：使用 @TripleRequestCheck 注解时，确保 TripleRequest 子类实现了 validate() 方法
2. **上下文管理**：ContextHolder 需要在请求开始时初始化，在请求结束时清理
3. **错误处理**：校验失败时会抛出 BusinessException，上层需要正确处理
4. **日志记录**：TripleResult 会自动记录请求和响应的日志信息
5. **链路追踪**：通过 context 字段可以追踪请求的完整链路

## 迁移说明

本模块从 `awaken-base` 项目的 `awaken-sdk-mvc` 模块迁移而来，主要变更：

1. **包名调整**：从 `com.awaken.base.sdk.mvc.triple.*` 调整为 `com.xlf.utility.triple.*`
2. **依赖调整**：
   - ErrorCode 从 `com.awaken.base.ErrorCode` 调整为 `com.xlf.utility.ErrorCode`
   - BusinessException 从 `com.awaken.base.app.exception.BusinessException` 调整为 `com.xlf.utility.exception.BusinessException`
   - ContextHolder 从 `com.awaken.base.sdk.mvc.holder.ContextHolder` 调整为 `com.xlf.utility.mvc.holder.ContextHolder`
3. **版本号处理**：移除了 StringConstant.SYSTEM_VERSION 依赖，使用固定版本号 "1.0.0"
4. **代码规范**：遵循用户的代码规范，调用内部方法时使用 `this.` 前缀

## 版本历史

- v1.1.6-SNAPSHOT: 从 awaken-base 迁移，完成包名和依赖调整
