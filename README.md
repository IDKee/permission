# permission
[![](https://img.shields.io/badge/JDK-1.8-brightgreen.svg)]()
[![](https://img.shields.io/hexpm/l/plug.svg)]()
[![](https://img.shields.io/badge/maven-v4.0.0-blue.svg)](http://maven.apache.org/)
[![](https://img.shields.io/badge/springframework-v4.0.0-orange.svg)](http://spring.io/projects)
[![](https://img.shields.io/badge/developer-WAng91An-red.svg)](https://github.com/WAng91An)
=============================================================================================

### Java权限管理系统功能列表

#####  部门模块
- 新增部门
- 更新部门
- 删除部门
- 部门树形结构展示

##### 用户模块
- 新增用户
- 更新用户
- 部门分页获取用户列表并展示

##### 权限模块
- 新增权限模块
- 更新权限模块
- 删除权限模块
- 权限模块树形结构展示

##### 权限点模块
- 新增权限点
- 更新权限点
- 根据权限模块分页获取权限点列表并展示
##### 角色模块
- 新增角色
- 更新角色
- 删除角色
- 获取角色列表并展示
##### 权限关系维护
- 角色-权限树形结构列表
- 更新角色-权限关系
##### 用户关系维护
- 获取指定角色已分配用户列表
- 获取指定角色未分配用户列表
- 更新角色-用户关系
##### 日志模块
- 带查询条件分页展示权限日志
- 根据权限日志撤销之前的操作
##### Redis缓存模块
- Redis缓存用户权限
- Redis缓存系统权限
- 已缓存权限清理
##### 其他
- 在切面判断是否允许当前用户访问某个URL
- 查询指定用户已分配的权限（树形结构）
- 查询指定权限被哪些人拥有

### 核心技术栈
项目框架：Spring/Spring MVC/Mybatis/Redis

基础工具：Maven/Tomcat/MySQL/JDK1.8

前端技术：jQuery/Bootstrap/Mustache/zTree/Duallistbox

其他技术：Java高级技术/Guava/Druid/ Jackson

### 此系统优点
1. 利用localThread进行高并发处理
2. 利用邮件发送进行注册账号
3. 采用MD5加密方式
4. 日志处理
5. lombok工具的使用
6. validator进行参数校验


### 项目资源

获取海量项目资源关注公众号：Web项目聚集地
![](https://github.com/WAng91An/permission/blob/master/二维码.jpg)

回复： 权限系统 获取sql文件

