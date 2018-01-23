# permission
![](https://img.shields.io/badge/License-Apache-green.svg)
![](https://img.shields.io/badge/star-1K-red.svg)
![](https://img.shields.io/badge/maven-v4.0.0-blue.svg)
![](https://img.shields.io/badge/springframework-v4.0.0-orange.svg)
权限管理系统(基于RBAC拓展模型，分布式权限管理系统)
=======

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

### 遇到的问题以及解决方案
1. 如何有效解决高并发，参数传递？
利用localThread，首先定义一个filter，拦截用户信息用RequstHolder处理把每个请求的请求信息和用户的信息放入一个localThread中，方便取值，每个用户有单独的线程保存自己的信息
```
public class RequestHolder {

    /**
     * 处理高并发的对象
     * 1. 每个线程是独立的
     * 2. 登陆的时候会把每个用户的信息和请求的信息放入线程里面
     * 3. 而在以后取用户信息可以直接取这里取
     */
    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<SysUser>();

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();

    public static void add(SysUser sysUser) {
        userHolder.set(sysUser);
    }

    public static void add(HttpServletRequest request) {
        requestHolder.set(request);
    }

    public static SysUser getCurrentUser() {
        return userHolder.get();
    }

    public static HttpServletRequest getCurrentRequest() {
        return requestHolder.get();
    }

    public static void remove() {
        userHolder.remove();
        requestHolder.remove();
    }
}
```
- ThreadLocal提供了保持对象的方法和避免参数传递的方便的对象访问方式。归纳了两点：
- 每个线程中都有一个自己的ThreadLocalMap类对象，可以将线程自己的对象保持到其中，各管各的，线程可以正确的访问到自己的对象。
- 将一个共用的ThreadLocal静态实例作为key，将不同对象的引用保存到不同线程的ThreadLocalMap中，然后在线程执行
的各处通过这个静态ThreadLocal实例的get()方法取得自己线程保存的那个对象，避免了将这个对象作为参数传递的麻烦。
- ThreadLocal的应用场合，我觉得最适合的是按线程多实例（每个线程对应一个实例）的对象的访问，并且这个对象很多地方都要用到。