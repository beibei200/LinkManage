# 后台 API 文档

## 概述
本系统后端API涵盖用户认证、用户管理、链路管理等功能模块，所有接口均采用统一的JSON返回格式，除登录外均需携带JWT Token。

- **基础URL**: `http://localhost:8081`
- **认证方式**: JWT Token（请求头 `Authorization: Bearer <token>`）

---

## 认证模块

### 登录
- **接口地址**: `POST /api/auth/login`
- **请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "token": "jwt-token-string",
    "userInfo": {
      "id": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "email": "admin@example.com",
      "role": "ADMIN",
      "status": "ACTIVE",
      "createdAt": "2024-01-01T00:00:00",
      "lastLoginAt": "2024-06-18T10:30:00"
    }
  }
}
```
- **说明**: 登录成功返回token和用户信息，失败时返回401。

---

## 用户管理模块

### 获取用户列表
- **接口地址**: `GET /api/users`
- **请求参数**:
  - `page` (Integer, 默认1): 页码
  - `size` (Integer, 默认10): 每页大小
  - `username` (String, 可选): 用户名模糊查询
  - `role` (String, 可选): 角色筛选 (ADMIN/USER/OPERATOR)
  - `status` (String, 可选): 状态筛选 (ACTIVE/INACTIVE)
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "系统管理员",
        "email": "admin@example.com",
        "role": "ADMIN",
        "status": "ACTIVE",
        "createdAt": "2024-01-01T00:00:00",
        "lastLoginAt": "2024-06-18T10:30:00"
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

### 获取用户详情
- **接口地址**: `GET /api/users/{id}`
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "email": "admin@example.com",
    "role": "ADMIN",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T00:00:00",
    "lastLoginAt": "2024-06-18T10:30:00"
  }
}
```

### 创建用户
- **接口地址**: `POST /api/users`
- **请求体**:
```json
{
  "username": "newuser",
  "password": "password123",
  "nickname": "新用户",
  "email": "newuser@example.com",
  "role": "USER",
  "status": "ACTIVE"
}
```
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 2,
    "username": "newuser",
    "nickname": "新用户",
    "email": "newuser@example.com",
    "role": "USER",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T10:00:00",
    "lastLoginAt": null
  }
}
```

### 更新用户
- **接口地址**: `PUT /api/users/{id}`
- **请求体**: 同创建用户，字段可选
- **响应示例**: 同上

### 删除用户
- **接口地址**: `DELETE /api/users/{id}`
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

### 切换用户状态
- **接口地址**: `PUT /api/users/{id}/status`
- **响应示例**: 同获取用户详情

### 检查用户名是否存在
- **接口地址**: `GET /api/users/check-username?username=xxx`
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "exists": true
  }
}
```

---

## 链路管理模块

### 获取链路列表
- **接口地址**: `GET /api/links`
- **请求参数**:
  - `page` (Integer, 默认1): 页码
  - `size` (Integer, 默认10): 每页大小
  - `linkType` (String, 可选): 链路类型 (PRIMARY/SECONDARY)
  - `vpsProvider` (String, 可选): VPS商家
  - `purchaser` (String, 可选): 采购者
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "list": [
      {
        "id": 1,
        "linkType": "PRIMARY",
        "purchaseTime": "2024-01-15T10:30:00",
        "expireTime": "2025-01-15T10:30:00",
        "price": 299.99,
        "vpsProvider": "阿里云",
        "purchaser": "张三",
        "jumpCount": 1,
        "createdAt": "2024-01-15T10:30:00",
        "updatedAt": "2024-01-15T10:30:00",
        "deleted": 0
      }
    ],
    "total": 1,
    "page": 1,
    "size": 10,
    "pages": 1
  }
}
```

### 获取链路详情
- **接口地址**: `GET /api/links/{id}`
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "linkType": "PRIMARY",
    "purchaseTime": "2024-01-15T10:30:00",
    "expireTime": "2025-01-15T10:30:00",
    "price": 299.99,
    "vpsProvider": "阿里云",
    "purchaser": "张三",
    "jumpCount": 1,
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00",
    "deleted": 0
  }
}
```

### 新增链路
- **接口地址**: `POST /api/links`
- **请求体**:
```json
{
  "linkType": "PRIMARY",
  "purchaseTime": "2024-01-15T10:30:00",
  "expireTime": "2025-01-15T10:30:00",
  "price": 299.99,
  "vpsProvider": "阿里云",
  "purchaser": "张三",
  "jumpCount": 1
}
```
- **响应示例**: 同获取链路详情

### 更新链路
- **接口地址**: `PUT /api/links/{id}`
- **请求体**: 同新增链路，字段可选
- **响应示例**: 同获取链路详情

### 删除链路
- **接口地址**: `DELETE /api/links/{id}`
- **响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误/业务校验失败 |
| 401 | 未认证或认证失败 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 业务规则
1. **所有接口除登录外均需携带有效JWT Token**
2. **用户管理接口仅管理员可用**，链路管理部分操作需管理员或操作员权限
3. **用户名唯一性**，不能重复
4. **不能删除最后一个管理员用户**
5. **密码加密存储**，不返回明文密码
6. **链路类型仅支持 PRIMARY/SECONDARY**

---

## 测试账号
- 管理员：admin / admin123
- 测试用户：test / test123
- 操作员：operator / operator123

---

 