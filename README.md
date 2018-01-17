
# permission
权限管理系统（重要接口的解决方案）
=======
### 更新部门 "update.json"

1. 判断部门名称有没有相同的
2. 部门存在性检查
3. 更新前和更新后的对像进行对他的子部门进行更新（updateWithChild）
4. updateWithChild函数实现
5. 获得更新部门前后的level，如果改变了，它的子部门也得改变
6. 获取更新前部门的自部门的集合进行遍历
7. 得到新的以更新后部门前缀的level





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