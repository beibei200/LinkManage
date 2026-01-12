# 链路管理系统 (Link Management System)

一个基于 Spring Boot + Vue 3 的前后端分离链路管理系统，支持多角色权限管理、链路信息管理、操作日志记录等功能。

## 📋 项目简介

链路管理系统是一个企业级的网络链路管理平台，用于管理和监控各类网络链路资源。系统采用前后端分离架构，提供了完善的用户权限管理、链路信息维护、数据统计分析等功能。

### 主要功能

- 🔐 **用户认证与授权**：基于 JWT 的无状态认证，支持多角色权限管理
- 👥 **用户管理**：完整的用户增删改查，支持角色分配和状态管理
- 🔗 **链路管理**：链路信息的全生命周期管理，支持分页查询和高级筛选
- 📊 **数据统计**：链路类型分布统计、最近链路展示等数据分析
- 📝 **操作日志**：完整的操作审计跟踪，记录关键操作
- 🎨 **现代化 UI**：基于 Element Plus 的响应式界面设计

## 🛠️ 技术栈

### 后端技术
- **框架**：Spring Boot 3.1.0
- **安全**：Spring Security + JWT (JJWT 0.11.5)
- **ORM**：MyBatis-Plus 3.5.3.1
- **数据库**：MySQL 8.0+
- **文档**：Knife4j 4.1.0 (OpenAPI 3.0)
- **工具库**：Hutool 5.8.20, Lombok
- **构建工具**：Maven

### 前端技术
- **框架**：Vue 3.4.19
- **UI 组件库**：Element Plus 2.5.6
- **状态管理**：Pinia 2.1.7
- **路由**：Vue Router 4.3.0
- **HTTP 客户端**：Axios 1.6.7
- **图表库**：ECharts 5.6.0
- **构建工具**：Vite 5.1.4

## 📦 项目结构

```
link_manage_system_1/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/beibei/linkmanagement/
│   │   │   │   ├── common/           # 通用类（Result、PageRequest等）
│   │   │   │   ├── config/           # 配置类（Security、CORS、JWT等）
│   │   │   │   ├── controller/       # 控制器层
│   │   │   │   ├── dto/              # 数据传输对象
│   │   │   │   ├── entity/           # 实体类
│   │   │   │   ├── mapper/           # MyBatis Mapper
│   │   │   │   ├── service/          # 业务逻辑层
│   │   │   │   └── util/             # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml   # 应用配置
│   │   │       └── db/init.sql       # 数据库初始化脚本
│   └── pom.xml                        # Maven 依赖配置
│
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                      # API 接口封装
│   │   ├── router/                   # 路由配置
│   │   ├── stores/                   # Pinia 状态管理
│   │   ├── views/                    # 页面组件
│   │   ├── App.vue                   # 根组件
│   │   └── main.js                   # 入口文件
│   ├── package.json                  # NPM 依赖配置
│   └── vite.config.js                # Vite 配置
│
├── BACKEND_API_DOCUMENTION.MD # 后端 API 文档
├── USER_MANAGEMENT_API.md      # 用户管理 API 文档
├── SYSTEM_DOCUMENT.md          # 系统功能说明文档
└── README.md                   # 项目说明文档
```

## 🚀 快速开始

### 环境要求

- **JDK**：17+
- **Maven**：3.6+
- **Node.js**：16+
- **MySQL**：8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE link_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p link_management < backend/src/main/resources/db/init.sql
```

3. 修改配置文件 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/link_management?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root      # 修改为你的数据库用户名
    password: your_pwd  # 修改为你的数据库密码
```

### 后端启动

1. 进入后端目录：
```bash
cd backend
```

2. 编译项目：
```bash
mvn clean install
```

3. 启动应用：
```bash
mvn spring-boot:run
```

后端服务将在 `http://localhost:8081` 启动

#### API 文档访问
- Knife4j 文档：http://localhost:8081/doc.html
- Swagger UI：http://localhost:8081/swagger-ui.html

### 前端启动

1. 进入前端目录：
```bash
cd frontend
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

4. 生产构建：
```bash
npm run build
```

## 👤 默认账户

系统启动后会自动初始化以下测试账户：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 拥有所有权限 |
| operator | operator123 | 操作员 | 可管理链路信息 |
| user | user123 | 普通用户 | 仅可查看数据 |

## 📚 功能说明

### 用户管理
- 用户列表查看（分页）
- 新增/编辑/删除用户
- 用户状态管理（启用/禁用）
- 角色分配（管理员/操作员/普通用户）
- 个人信息修改

### 链路管理
- 链路信息的增删改查
- 支持字段：
  - 链路类型
  - 购买时间/到期时间
  - 价格
  - VPS 商家
  - 采购者
  - 跳转次数
- 分页查询和筛选
- 链路类型分布统计
- 最近链路展示

### 权限控制
- **管理员**：所有功能的完整权限
- **操作员**：链路管理权限，用户查看权限
- **普通用户**：仅可查看链路信息

## 🔌 API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/me` - 获取当前用户信息

### 用户管理接口
- `GET /api/users` - 获取用户列表（分页）
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

### 链路管理接口
- `GET /api/links` - 获取链路列表（分页）
- `GET /api/links/{id}` - 获取链路详情
- `POST /api/links` - 创建链路
- `PUT /api/links/{id}` - 更新链路
- `DELETE /api/links/{id}` - 删除链路
- `GET /api/links/stats` - 获取链路统计数据

详细 API 文档请参考：
- [后端 API 文档](BACKEND_API_DOCUMENTION.MD)
- [用户管理 API 文档](USER_MANAGEMENT_API.md)

## 🔒 安全特性

- JWT Token 认证机制
- 密码 BCrypt 加密存储
- CORS 跨域配置
- SQL 注入防护（MyBatis-Plus）
- XSS 防护
- 接口权限控制
- 操作日志审计

## 📱 界面预览

系统提供以下页面：
- 登录页面
- 首页（数据概览）
- 用户管理
- 链路管理
- 个人中心

## 🔧 开发指南

### 后端开发
1. 遵循 RESTful API 设计规范
2. 使用 MyBatis-Plus 进行数据库操作
3. 统一返回格式使用 `Result<T>` 类
4. 异常统一由 `GlobalExceptionHandler` 处理
5. 使用 Lombok 简化代码

### 前端开发
1. 组件化开发，遵循 Vue 3 Composition API
2. 使用 Pinia 进行状态管理
3. API 调用统一通过 axios 封装
4. 路由配置使用懒加载
5. 遵循 Element Plus 设计规范

## 📝 TODO

- [ ] 添加链路导入导出功能
- [ ] 实现操作日志查询界面
- [ ] 增加数据可视化看板
- [ ] 添加邮件通知功能
- [ ] 实现链路到期提醒
- [ ] 添加批量操作功能
- [ ] 增加更多统计报表

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目仅供学习交流使用。

## 👨‍💻 作者

Beibei

## 📞 联系方式

如有问题或建议，请通过以下方式联系：
- 提交 Issue
- 发送邮件

---

**注意**：生产环境部署前请务必修改以下配置：
- 数据库密码
- JWT Secret Key
- 默认管理员密码

