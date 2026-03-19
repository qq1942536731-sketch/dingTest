# 抢券活动管理后台

基于 **Java 8 + Spring Boot 2.7 + Thymeleaf + Spring Security + JPA + MySQL 8.0** 实现的管理后台，支持：

- 抢券活动创建、开始/结束/禁用状态控制
- 券库存总量控制与防超发
- 用户、角色、菜单权限管理
- 不同后台用户分配不同菜单权限

## 技术栈

- JDK 8
- Spring Boot 2.7.18
- Spring Security
- Spring Data JPA
- Thymeleaf
- MySQL 8.0

## 启动方式

1. 创建数据库：`coupon_admin`
2. 修改 `src/main/resources/application.yml` 中 MySQL 连接
3. 执行：

```bash
mvn spring-boot:run
```

应用启动后会自动执行 `schema.sql` 和 `data.sql` 初始化基础数据。

## 默认账号

- `admin / password`：超级管理员
- `ops / password`：运营人员
- `auditor / password`：审计人员

## 关键设计

### 活动启停

活动状态支持：`DRAFT`、`READY`、`RUNNING`、`ENDED`、`DISABLED`。

### 防超发

发券使用条件更新：仅当活动为 `RUNNING` 且 `issued_stock < total_stock` 时，数据库更新 `issued_stock = issued_stock + 1`。
如果更新条数为 0，则说明库存不足或活动未启动，直接拒绝发券。

### 权限模型

采用 RBAC：

- 用户 `sys_user`
- 角色 `sys_role`
- 菜单/权限 `sys_menu`
- 用户角色关系 `sys_user_role`
- 角色菜单关系 `sys_role_menu`

登录后：

- 左侧菜单按当前用户拥有的菜单权限动态渲染
- 页面访问由 Spring Security 权限标识控制
