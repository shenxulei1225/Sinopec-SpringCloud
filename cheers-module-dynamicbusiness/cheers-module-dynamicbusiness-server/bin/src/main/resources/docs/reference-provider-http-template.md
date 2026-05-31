# HTTP 引用 Provider 模板（ERP_USER）

## 1. Provider 基础信息

- `providerCode`: `ERP_USER`
- `providerType`: `HTTP`
- `semanticType`: `USER`
- `capabilityFlags`: `{"search":true,"batchGet":true,"validate":true}`

## 2. configJson 模板

```json
{
  "searchUrl": "http://erp-gateway/api/users/search",
  "batchGetUrl": "http://erp-gateway/api/users/batch-get",
  "validateUrl": "http://erp-gateway/api/users/validate",
  "idField": "id",
  "labelField": "name",
  "listPath": "data.rows",
  "token": "${ERP_PROVIDER_TOKEN}"
}
```

- `listPath` 用于兼容包裹结构响应（如 `data.list` / `data.rows` / `result.items`）
- 当响应本身是数组 `[]` 时，可不配置 `listPath`

## 3. 执行模型说明（为什么需要 Executor）

系统采用“**配置驱动 + 执行器承载**”双层模型：

- **Provider 配置**（数据库）负责声明：
  - 访问地址（`searchUrl` / `batchGetUrl` / `validateUrl`）
  - 结果映射（`idField` / `labelField` / `listPath`）
  - 认证参数（如 `token`）
- **Provider Executor**（代码）负责运行时行为：
  - 发起请求（GET/POST）
  - 处理异常、超时、容错
  - 解析多种响应结构
  - 统一输出 `ReferenceCandidateRespVO`

因此并非“每个 Provider 写一套代码”，而是：

- 大多数外部系统复用通用 `HttpReferenceProviderExecutor`
- 少量内部/特殊系统使用专用 Executor

## 4. 第三方字段解析与配置策略（避免配置爆炸）

针对第三方系统字段差异，不建议做大量逐字段定义，建议采用“三层渐进策略”：

1. **最小必配**（上线必需）
   - `idField`
   - `labelField`
   - `listPath`（仅当响应非数组时）
2. **可选增强**（按需）
   - 保留原始对象到 `meta`，前端按需展示扩展字段（如手机号、编码、组织）
3. **语义收敛**（长期治理）
   - 按 `semanticType`（USER/DEPT/EQUIPMENT...）制定统一展示约定
   - 新接入系统只做最小映射，不复制业务字段模型

### 4.1 推荐实践

- 新接入 Provider 时，优先只配置 `idField/labelField/listPath`
- 其余字段统一透传到 `meta`，避免为每个系统创建大量映射项
- 仅当前端强依赖某字段且命名差异大时，再补充轻量别名映射

## 5. 外部接口约定

### 5.1 search

- Method: `GET`
- Query: `keyword`, `pageNo`, `pageSize`
- Response:

```json
[
  {
    "id": "1001",
    "name": "张三",
    "mobile": "13800000000"
  }
]
```

### 5.2 batch-get

- Method: `POST`
- Body:

```json
{
  "ids": ["1001", "1002"]
}
```

- Response: 同 `search` 数组结构

### 5.3 validate

- Method: `POST`
- Body:

```json
{
  "id": "1001",
  "context": {}
}
```

- Response:

```json
{
  "valid": true,
  "reason": ""
}
```
