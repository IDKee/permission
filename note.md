
# permission
权限管理系统（重要接口的解决方案）
=======
### roleTree 权限点树
1. 构造dto对象，AclModuleLevelDto(包括权限的集合和权限点的dto对象的集合)




### 更新部门 (update.json)

1. 判断部门名称有没有相同的
2. 部门存在性检查
3. 更新前和更新后的对像进行对他的子部门进行更新（updateWithChild）
4. updateWithChild函数实现
5. 获得更新部门前后的level，如果改变了，它的子部门也得改变
6. 获取更新前部门的自部门的集合进行遍历
7. 得到新的以更新后部门前缀的level

### updateWithChild(部门权限更新也要顾及到它下面的子部门)
1. 传入更新前的和更新后的信息
2. 取出前后的等级
3. 如果部门等级前缀更改了，子部门需要更新
    - 获取旧等级的所有部门
    - 遍历旧等级的所有部门
    - 获取旧等级部门的level
    - 如果旧的部门是旧的等级开头执行一下操作
    - 更改子部门的的前缀为新的前缀
    - 批量更新子部门

### 返回部门树接口实现
1. 从部门数据表中得到所有的部门,集合返回

    - 即 技术部,后端开发,前端开发,UI设计,产品部,客服部

2. 把部门的集合转化成DeptLevelDto的对象集合,DeptLevelDto相比SysDept多了一个List<DeptLevelDto>属性

3. 把DeptLevelDto的集合,传入deptListToTree函数

4. deptListToTree函数作用:

    - 得到两个集合 第一个 (0 - >技术部,产品部,客服部),已level为key,其符合此key的部门的集合为值
    - 第二个 (技术部,产品部,客服部)所有的根部门的节点集合rootList
    - 把得到的rootList的根据seq字段进行排序
    - 把排序号的(技术部,产品部,客服部),0,{(0 - >技术部,产品部,客服部),(0.1 - >后端,前端,Ui)}传入transformDeptTree

5. transformDeptTree函数作用:

    - 遍历技术部,产品部,客服部这些根部门,已遍历到技术部为例子id = 1
    - 得到nextLevel为 0.1
    - 得到0.1 的即 后端,前端,UI
    - 对后端,前端,UI排序
    - 给技术部 添加一个部门list属性,值为:后端,前端,UI
    - 把(后端,前端,UI),0.1,{(0 - >技术部,产品部,客服部),(0.1 - >后端,前端,Ui)} 传入
      transformDeptTree 进行递归

### 加载用户和分页信息

1. 比如点击技术部
2. 获取当前页，页面大小,请求接口
    ```
        /sys/user/page.json?deptId=1
    ```
3. 进入 /sys/user/page.json
    - 得到model里面有传进来得当前页，页面大小，和内部计算得偏移量
    - 传进来得部门id,部门下面有没有人(count)
    - 如果有人进行分页查询，返回查询后得PageResult(查询后得list,total=count)
    - 返回成功得json数据
4. 前端拿到带有用户list的数据做下面的操作
    - 如果此列表有人进行渲染
    - 没人显示为空
    - 把利用page.jsp分页组件进行加载分页
    - 模板引擎的小知识
    ```
      var view = {
                     "name": "Tater",
                     "bold": function () {
                         return function (text, render) {
                            return render(text) + "<br />";
                         }
                     }
                 }
           show(Mustache.render("{{#bold}}{{name}}{{/bold}}", view));
           结果:Tater
           原始字符串作为第一个参数，
           默认的解释器作为第二个参数
    ```
    ```
    var view = {
                      "beatles": [
                          { "firstname": "Johh", "lastname": "Lennon" },
                          { "firstname": "Paul", "lastname": "McCartney" }
                      ],
                      "name": function () {
                          return this.firstname + this.lastname;
                      }
                  };
         show(Mustache.render("{{#beatles}}{{name}}<br />{{/beatles}}", view));
         结果：
         JohhLennon
         PaulMcCartney
         
         Mustache会将
         数组中的值传递给你的函数，输出你函数返回的值。这里我们可以看到最外层是数组，只要在里面
         使用函数那么外层的数组就会作为这个函数的参数传递进去。
    ```
### 点击部门加载用户列表前端实现
1. 点击部门名字的到绑定的部门id
2. 调用渲染加载用户函数，把部门id传进去
3. 点击的部门更改样式
4. 加载用户列表
    - 获取需要加载的一页多少记录
    - 获取加载第几页
    - 调用接口
        ```
        /sys/user/page.json?deptId=1
        ```
    - 返回的数据进行调用函数渲染
    - 模板引擎进行参数处理
    - 有效还是无效不同的颜色
    - 渲染用户列表同时，初始化用户的点击事件函数
    - 渲染分页信息
    
### 部门添加前端实现
1. 点击部门添加的按钮，弹出dialog
2. 打开dialog的时候渲染当前所有部门的select，option组件(递归算法)
3. 单击添加按钮就行调用接口
    ```
       /sys/dept/save.json
    ```
    - 序列化后的数据进行请求
    - 请求成功更新列表，回调关闭diglog
### 部门编辑前端实现
1. 点击部门编辑的按钮，弹出dialog
2. 打开dialog的时候渲染当前所有部门的select，option组件
3. 从缓存中提取当前部门的信息回填
3. 单击编辑按钮就行调用接口
    ```
       /sys/dept/update.json
    ```
    - 序列化后的数据进行请求
    - 请求成功更新列表，回调关闭diglog
### 用户添加，编辑前端实现
- 同部门的添加编辑

### Loginfilter(对每个链接进行校验)
1. 获取当前session里面的值
2. 如果session没有存的用户，这转到登陆页面
3. 如果有此用户就把此用户信息,当前请求存到一个Threadlocal里面
    为什么使用线程局部变量?
    在并发编程的时候，成员变量如果不做任何处理其实是线程不安全的，各个线程都在操作同一个变量，显然是不行的，并且我们也知道volatile这个关键字也是不能保证线程安全的。那么在有一种情况之下，我们需要满足这样一个条件：变量是同一个，但是每个线程都使用同一个初始值，也就是使用同一个变量的一个新的副本。这种情况之下ThreadLocal就非常使用，比如说DAO的数据库连接，我们知道DAO是单例的，那么他的属性Connection就不是一个线程安全的变量。而我们每个线程都需要使用他，并且各自使用各自的。这种情况，ThreadLocal就比较好的解决了这个问题。
4. 以后用到用户的信息去RequestHolder里面去取，不用再经过controller里传了    
### 如何实现添加部门或者权限的时候select的展示
1. 遍历select的option组件



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


### 结构