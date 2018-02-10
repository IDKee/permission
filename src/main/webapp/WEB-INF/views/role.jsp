<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>角色</title>
    <jsp:include page="/common/backend_common.jsp" />
    <link rel="stylesheet" href="/ztree/zTreeStyle.css" type="text/css">
    <link rel="stylesheet" href="/assets/css/bootstrap-duallistbox.min.css" type="text/css">
    <script type="text/javascript" src="/ztree/jquery.ztree.all.min.js"></script>
    <script type="text/javascript" src="/assets/js/jquery.bootstrap-duallistbox.min.js"></script>
    <style type="text/css">
        .bootstrap-duallistbox-container .moveall, .bootstrap-duallistbox-container .removeall {
            width: 50%;
        }
        .bootstrap-duallistbox-container .move, .bootstrap-duallistbox-container .remove {
            width: 49%;
        }
    </style>
</head>
<body class="no-skin" youdao="bind" style="background: white">
<input id="gritter-light" checked="" type="checkbox" class="ace ace-switch ace-switch-5"/>
<div class="page-header">
    <h1>
        角色管理
        <small>
            <i class="ace-icon fa fa-angle-double-right"></i>
            维护角色与用户, 角色与权限关系
        </small>
    </h1>
</div>
<div class="main-content-inner">
    <div class="col-sm-3">
        <div class="table-header">
            角色列表&nbsp;&nbsp;
            <a class="green" href="#">
                <i class="ace-icon fa fa-plus-circle orange bigger-130 role-add"></i>
            </a>
        </div>
        <div id="roleList">
            <!--角色列表-->
        </div>
    </div>
    <div class="col-sm-9">
        <div class="tabbable" id="roleTab">
            <ul class="nav nav-tabs">
                <li class="active">
                    <a data-toggle="tab" href="#roleAclTab">
                        角色与权限
                    </a>
                </li>
                <li>
                    <a data-toggle="tab" href="#roleUserTab">
                        角色与用户
                    </a>
                </li>
            </ul>
            <div class="tab-content">
                <div id="roleAclTab" class="tab-pane fade in active">
                    <ul id="roleAclTree" class="ztree"></ul>
                    <button class="btn btn-info saveRoleAcl" type="button">
                        <i class="ace-icon fa fa-check bigger-110"></i>
                        保存
                    </button>
                </div>

                <div id="roleUserTab" class="tab-pane fade" >
                    <div class="row">
                        <div class="box1 col-md-6">待选用户列表</div>
                        <div class="box1 col-md-6">已选用户列表</div>
                    </div>
                    <select multiple="multiple" size="10" name="roleUserList" id="roleUserList" >
                    </select>
                    <div class="hr hr-16 hr-dotted"></div>
                    <button class="btn btn-info saveRoleUser" type="button">
                        <i class="ace-icon fa fa-check bigger-110"></i>
                        保存
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="dialog-role-form" style="display: none;">
    <form id="roleForm">
        <table class="table table-striped table-bordered table-hover dataTable no-footer" role="grid">
            <tr>
                <td><label for="roleName">名称</label></td>
                <td>
                    <input type="text" name="name" id="roleName" value="" class="text ui-widget-content ui-corner-all">
                    <input type="hidden" name="id" id="roleId"/>
                </td>
            </tr>
            <tr>
                <td><label for="roleStatus">状态</label></td>
                <td>
                    <select id="roleStatus" name="status" data-placeholder="状态" style="width: 150px;">
                        <option value="1">可用</option>
                        <option value="0">冻结</option>
                    </select>
                </td>
            </tr>
            <td><label for="roleRemark">备注</label></td>
            <td><textarea name="remark" id="roleRemark" class="text ui-widget-content ui-corner-all" rows="3" cols="25"></textarea></td>
            </tr>
        </table>
    </form>
</div>
<script id="roleListTemplate" type="x-tmpl-mustache">
<ol class="dd-list ">
    {{#roleList}}
        <li class="dd-item dd2-item role-name" id="role_{{id}}" href="javascript:void(0)" data-id="{{id}}">
            <div class="dd2-content" style="cursor:pointer;">
            {{name}}
            <span style="float:right;">
                <a class="green role-edit" href="#" data-id="{{id}}" >
                    <i class="ace-icon fa fa-pencil bigger-100"></i>
                </a>
                &nbsp;
                <a class="red role-delete" href="#" data-id="{{id}}" data-name="{{name}}">
                    <i class="ace-icon fa fa-trash-o bigger-100"></i>
                </a>
            </span>
            </div>
        </li>
    {{/roleList}}
</ol>
</script>

<script id="selectedUsersTemplate" type="x-tmpl-mustache">
{{#userList}}
    <option value="{{id}}" selected="selected">{{username}}</option>
{{/userList}}
</script>

<script id="unSelectedUsersTemplate" type="x-tmpl-mustache">
{{#userList}}
    <option value="{{id}}">{{username}}</option>
{{/userList}}
</script>

<script type="text/javascript">
    $(function () {
        //缓存住所有的role
        var roleMap = {};
        //上次点击的id
        var lastRoleId = -1;
        //表识先点击的tab
        var selectFirstTab = true; //是否选中的是第一个tap
        var hasMultiSelect = false;

        var roleListTemplate = $("#roleListTemplate").html();
        Mustache.parse(roleListTemplate);
        var selectedUsersTemplate = $("#selectedUsersTemplate").html();
        Mustache.parse(selectedUsersTemplate);
        var unSelectedUsersTemplate = $("#unSelectedUsersTemplate").html();
        Mustache.parse(unSelectedUsersTemplate);

        loadRoleList();

        // zTree
        <!-- 树结构相关 开始 -->
        var zTreeObj = []; //存储输的结构
        var modulePrefix = 'm_';//权限模块的前缀
        var aclPrefix = 'a_'; //权限点的前缀
        var nodeMap = {}; //所有系你先

        var setting = {
            //checkbox相关
            check: {
                // checkbox勾选框展示出来
                enable: true,
                // checkbox有一个disabled 是否做继承
                chkDisabledInherit: true,
                // checkbix 属性 ，p选中之后同时作用与父节点 s子节点
                chkboxType: {"Y": "ps", "N": "ps"}, //auto check 父节点 子节点
                // 每次点击check 都trigger相关的方法
                autoCheckTrigger: true
            },
            data: {
                simpleData: {
                    enable: true,
                    //顶级模块是0
                    rootPId: 0
                }
            },
            callback: {
                onClick: onClickTreeNode
            }
        };
        //点击节点 都要打开节点，展开
        function onClickTreeNode(e, treeId, treeNode) { // 绑定单击事件
            var zTree = $.fn.zTree.getZTreeObj("roleAclTree");
            zTree.expandNode(treeNode);
        }

        // 加载角色列表
        function loadRoleList() {
            // 请求接口加载角色列表
            $.ajax({
                url: "/sys/role/list.json",
                //成功
                success: function (result) {
                    if (result.ret) {
                        //渲染
                        var rendered = Mustache.render(roleListTemplate, {roleList: result.data});
                        $("#roleList").html(rendered);
                        //绑定点击事件
                        bindRoleClick();
                        //缓存获得的结果
                        $.each(result.data, function(i, role) {
                            roleMap[role.id] = role;
                        });
                    } else {
                        showMessage("加载角色列表", result.msg, false);
                    }
                }
            });
        }
        //渲染role列表的时候绑定事件
        function bindRoleClick() {
            //编辑角色
            $(".role-edit").click(function (e) {
                e.preventDefault();
                e.stopPropagation();
                var roleId = $(this).attr("data-id");
                $("#dialog-role-form").dialog({
                    model: true,
                    title: "修改角色",
                    open: function(event, ui) {
                        $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                        $("#roleForm")[0].reset();
                        var targetRole = roleMap[roleId];
                        //信息回填
                        if (targetRole) {
                            $("#roleId").val(roleId);
                            $("#roleName").val(targetRole.name);
                            $("#roleStatus").val(targetRole.status);
                            $("#roleRemark").val(targetRole.remark);
                        }
                    },
                    buttons : {
                        "修改": function(e) {
                            e.preventDefault();
                            //调用接口
                            updateRole(false, function (data) {
                                $("#dialog-role-form").dialog("close");
                            }, function (data) {
                                showMessage("修改角色", data.msg, false);
                            })
                        },
                        "取消": function () {
                            $("#dialog-role-form").dialog("close");
                        }
                    }
                })
            });
            //点击角色加载role，并且更改样式
            $(".role-name").click(function (e) {
               e.preventDefault();
               e.stopPropagation();
               var roleId = $(this).attr("data-id");
                //点击每个role调用的函数
               handleRoleSelected(roleId);
            });
        }
        //点击角色名称后，更改样式，加载角色
        function handleRoleSelected(roleId) {
            //点击过role了，取出以前的role的样式
            if (lastRoleId != -1) {
                var lastRole = $("#role_" + lastRoleId + " .dd2-content:first");
                lastRole.removeClass("btn-yellow");
                lastRole.removeClass("no-hover");
            }
            //当前点击的角色更改样式
            var currentRole = $("#role_" + roleId + " .dd2-content:first");
            currentRole.addClass("btn-yellow");
            currentRole.addClass("no-hover");
            lastRoleId = roleId;
            //tab栏第一个显示出来
            $('#roleTab a:first').trigger('click');
            if (selectFirstTab) {
                //加载角色权限的方法
                loadRoleAcl(roleId);
            }
        }
        //加载角色权限的方法
        function loadRoleAcl(selectedRoleId) {
            if (selectedRoleId == -1) {
                return;
            }
            $.ajax({
                url: "/sys/role/roleTree.json",
                data : {
                    roleId: selectedRoleId
                },
                type: 'POST',
                success: function (result) {
                    if (result.ret) {
                        //渲染角色树
                        renderRoleTree(result.data);
                    } else {
                        showMessage("加载角色权限数据", result.msg, false);
                    }
                }
            });
        }
        // 得到树 选中的id
        function getTreeSelectedId() {
            var treeObj = $.fn.zTree.getZTreeObj("roleAclTree");
            var nodes = treeObj.getCheckedNodes(true);
            var v = "";
            for(var i = 0; i < nodes.length; i++) {
                if(nodes[i].id.startsWith(aclPrefix)) {
                    v += "," + nodes[i].dataId;
                }
            }
            return v.length > 0 ? v.substring(1): v;
        }

        //渲染角色树，使用zTree插件渲染
        function renderRoleTree(aclModuleList) {
            //清空
            zTreeObj = [];
            //递归生成数据
            recursivePrepareTreeData(aclModuleList);
            for(var key in nodeMap) {
                zTreeObj.push(nodeMap[key]);
            }
            $.fn.zTree.init($("#roleAclTree"), setting, zTreeObj);
        }
        //递归生成数据，把zTreeObj处理完
        function recursivePrepareTreeData(aclModuleList) {
            // 准备 nodeMap
            if (aclModuleList && aclModuleList.length > 0) {
                //遍历aclModuleListDto的每一个对象
                $(aclModuleList).each(function(i, aclModule) {
                    // 当前节点是不是勾选
                    var hasChecked = false;
                    // 当前节点包含权限点，并且长度大于0
                    if (aclModule.aclList && aclModule.aclList.length > 0) {
                        // 遍历当前的权限点
                        $(aclModule.aclList).each(function(i, acl) {
                            // 把权限点写进tree对象里面
                            zTreeObj.push({
                                // 权限点的id
                                id: aclPrefix + acl.id,
                                // 哪个模块下面
                                pId: modulePrefix + acl.aclModuleId,
                                // 展示的名称
                                name: acl.name + ((acl.type == 1) ? '(菜单)' : ''),
                                chkDisabled: !acl.hasAcl,
                                // 是否点击
                                checked: acl.checked,
                                dataId: acl.id
                            });
                            if(acl.checked) {
                                hasChecked = true;
                            }
                        });
                    }
                    // 对权限模块进行处理
                    if ((aclModule.aclModuleList && aclModule.aclModuleList.length > 0) ||
                        (aclModule.aclList && aclModule.aclList.length > 0)) {
                        nodeMap[modulePrefix + aclModule.id] = {
                            id : modulePrefix + aclModule.id,
                            pId: modulePrefix + aclModule.parentId,
                            name: aclModule.name,
                            //是否允许打开
                            open: hasChecked
                        };
                        var tempAclModule = nodeMap[modulePrefix + aclModule.id];
                        while(hasChecked && tempAclModule) {
                            if(tempAclModule) {
                                nodeMap[tempAclModule.id] = {
                                    id: tempAclModule.id,
                                    pId: tempAclModule.pId,
                                    name: tempAclModule.name,
                                    open: true
                                }
                            }
                            tempAclModule = nodeMap[tempAclModule.pId];
                        }
                    }
                    recursivePrepareTreeData(aclModule.aclModuleList);
                });
            }
        }

        //新增角色
        $(".role-add").click(function () {
            $("#dialog-role-form").dialog({
                model: true,
                title: "新增角色",
                open: function(event, ui) {
                    $(".ui-dialog-titlebar-close", $(this).parent()).hide();
                    $("#roleForm")[0].reset();
                },
                buttons : {
                    "添加": function(e) {
                        e.preventDefault();
                        //调用新增的接口
                        updateRole(true, function (data) {
                            $("#dialog-role-form").dialog("close");
                        }, function (data) {
                            showMessage("新增角色", data.msg, false);
                        })
                    },
                    "取消": function () {
                        $("#dialog-role-form").dialog("close");
                    }
                }
            })
        });
        // 角色权限保存按钮的点击
        $(".saveRoleAcl").click(function (e) {
            e.preventDefault();
            if (lastRoleId == -1) {
                showMessage("保存角色与权限点的关系", "请现在左侧选择需要操作的角色", false);
                return;
            }
            $.ajax({
                url: "/sys/role/changeAcls.json",
                data: {
                    roleId: lastRoleId,
                    aclIds: getTreeSelectedId()
                },
                type: 'POST',
                success: function (result) {
                    if (result.ret) {
                        showMessage("保存角色与权限点的关系", "操作成功", false);
                    } else {
                        showMessage("保存角色与权限点的关系", result.msg, false);
                    }
                }
            });
        });
        //新增或者更新的高复用方法
        function updateRole(isCreate, successCallback, failCallback) {
            $.ajax({
                url: isCreate ? "/sys/role/save.json" : "/sys/role/update.json",
                data: $("#roleForm").serializeArray(),
                type: 'POST',
                success: function(result) {
                    if (result.ret) {
                        loadRoleList();
                        if (successCallback) {
                            successCallback(result);
                        }
                    } else {
                        if (failCallback) {
                            failCallback(result);
                        }
                    }
                }
            })
        }
        $("#roleTab a[data-toggle='tab']").on("shown.bs.tab", function(e) {
            if(lastRoleId == -1) {
                showMessage("加载角色关系","请先在左侧选择操作的角色", false);
                return;
            }
            if (e.target.getAttribute("href") == '#roleAclTab') {
                selectFirstTab = true;
                loadRoleAcl(lastRoleId);
            } else {
                selectFirstTab = false;
                loadRoleUser(lastRoleId);
            }
        });

        function loadRoleUser(selectedRoleId) {
            $.ajax({
                url: "/sys/role/users.json",
                data: {
                    roleId: selectedRoleId
                },
                type: 'POST',
                success: function (result) {
                    if (result.ret) {
                        var renderedSelect = Mustache.render(selectedUsersTemplate, {userList: result.data.selected});
                        var renderedUnSelect = Mustache.render(unSelectedUsersTemplate, {userList: result.data.unselected});
                        $("#roleUserList").html(renderedSelect + renderedUnSelect);

                        if(!hasMultiSelect) {
                            $('select[name="roleUserList"]').bootstrapDualListbox({
                                showFilterInputs: false,
                                moveOnSelect: false,
                                infoText: false
                            });
                            hasMultiSelect = true;
                        } else {
                            $('select[name="roleUserList"]').bootstrapDualListbox('refresh', true);
                        }
                    } else {
                        showMessage("加载角色用户数据", result.msg, false);
                    }
                }
            });
        }

        $(".saveRoleUser").click(function (e) {
            e.preventDefault();
            if (lastRoleId == -1) {
                showMessage("保存角色与用户的关系", "请现在左侧选择需要操作的角色", false);
                return;
            }
            $.ajax({
                url: "/sys/role/changeUsers.json",
                data: {
                    roleId: lastRoleId,
                    userIds: $("#roleUserList").val() ? $("#roleUserList").val().join(",") : ''
                },
                type: 'POST',
                success: function (result) {
                    if (result.ret) {
                        showMessage("保存角色与用户的关系", "操作成功", false);
                    } else {
                        showMessage("保存角色与用户的关系", result.msg, false);
                    }
                }
            });
        });
    });
</script>
</body>
</html>
