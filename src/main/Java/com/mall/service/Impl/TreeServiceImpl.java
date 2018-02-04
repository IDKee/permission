package com.mall.service.Impl;

/**
 * Created by 王乾 on 2018/1/1.
 */

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mall.dao.SysAclMapper;
import com.mall.dao.SysAclModuleMapper;
import com.mall.dao.SysDeptMapper;
import com.mall.dto.AclDto;
import com.mall.dto.AclModuleLevelDto;
import com.mall.dto.DeptLevelDto;
import com.mall.model.SysAcl;
import com.mall.model.SysAclModule;
import com.mall.model.SysDept;
import com.mall.service.ICoreService;
import com.mall.service.ITreeService;
import com.mall.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 计算树 的方法
 */
@Service("iTreeService")
public class TreeServiceImpl implements ITreeService {

    @Resource
    private SysDeptMapper sysDeptMapper;
    @Resource
    private SysAclModuleMapper sysAclModuleMapper;
    @Resource
    private ICoreService iCoreService;
    @Resource
    private SysAclMapper sysAclMapper;

    /**
     * 权限点树
     * @param roleId
     * @return
     */
    public List<AclModuleLevelDto> roleTree(int roleId){

        //1. 当前用户已经分配的权限点
        List<SysAcl> userAclList = iCoreService.getCurrentUserAclList();
        //2. 当前角色已经分配的权限点
        List<SysAcl> roleAclList = iCoreService.getRoleAclList(roleId);
        //3. 取出所有的权限点
        List<SysAcl> allAclList = sysAclMapper.getAll();


        //获取此用户的权限点id集合，和此角色的权限点id集合
        //JDK1.8通过已经获取的acl对象集合来获取id的set集合
        //参考连接https://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/
        Set<Integer> userAclIdSet = userAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        Set<Integer> roleAclIdSet = roleAclList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());


        //遍历一次就可以遍历完 当前用户的和角色的共有的权限点
        List<AclDto> aclDtoList = Lists.newArrayList();
        //遍历当前用户的和角色的共有的权限点
        for (SysAcl acl : allAclList){
            AclDto dto = AclDto.adapt(acl);
            if (userAclIdSet.contains(acl.getId())) {
                dto.setHasAcl(true);
            }
            if (roleAclIdSet.contains(acl.getId())) {
                dto.setChecked(true);
            }
            aclDtoList.add(dto);
        }
        // 根据AclDto集合做成树的展示
        return aclListToTree(aclDtoList);
    }

    private List<AclModuleLevelDto> aclListToTree(List<AclDto> aclDtoList){
        if(CollectionUtils.isEmpty(aclDtoList)){
            return Lists.newArrayList();
        }
        //拿到以前的模块树
        List<AclModuleLevelDto> aclModuleLevelDtoList = aclModuleTree();

        Multimap<Integer, AclDto> moduleIdAclMap = ArrayListMultimap.create();
        //系统里所有有效的权限点构成的map
        for (AclDto dto : aclDtoList){
            if(dto.getStatus() == 1){
                moduleIdAclMap.put(dto.getAclModuleId(),dto);
            }
        }
        bindAclWithOrder(aclModuleLevelDtoList, moduleIdAclMap);
        return aclModuleLevelDtoList;
    }

    private void bindAclWithOrder(List<AclModuleLevelDto> aclModuleLevelDtoList, Multimap<Integer, AclDto> moduleIdAclMap){
        if(CollectionUtils.isEmpty(aclModuleLevelDtoList)){
            return;
        }
        for (AclModuleLevelDto dto : aclModuleLevelDtoList){
            List<AclDto> aclDtoList = (List<AclDto>)moduleIdAclMap.get(dto.getId());
            if (CollectionUtils.isNotEmpty(aclDtoList)) {
                Collections.sort(aclDtoList, aclSepComparator);
                dto.setAclList(aclDtoList);
            }
            bindAclWithOrder(dto.getAclModuleList(),moduleIdAclMap);
        }
    }
    /**
     * 返回权限模块的树
     * @return
     */
    public List<AclModuleLevelDto> aclModuleTree(){
        // 1. 取出所有的权限模块
        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModule();
        // 2. 定义树形结构
        List<AclModuleLevelDto> dtoList = Lists.newArrayList();
        // 3. 遍历取出的权限模块，并适配成dto对象，放到树形结构里面(此时每个list中的的dtoList是空的)
        for (SysAclModule aclModule : aclModuleList){
            dtoList.add(AclModuleLevelDto.adapt(aclModule));
        }
        return aclModuleListToTree(dtoList);
    }

    /**
     *根据当前结构适配出来一个权限模块树
     * @param dtoList
     * @return
     */
    private List<AclModuleLevelDto> aclModuleListToTree( List<AclModuleLevelDto> dtoList){
        if(CollectionUtils.isEmpty(dtoList)){
            return Lists.newArrayList();
        }
        //level -> [aclModule1,aclModule2,...]   Map<String,List<Object>>
        // Multimap数据结构的key是每个层级的标识，值可以是一个同一个层级下的模块列表
        Multimap<String,AclModuleLevelDto> levelAclModuleMap = ArrayListMultimap.create();
        List<AclModuleLevelDto> rootList = Lists.newArrayList();

        //如果是根节点,就加入rootList集合里面,
        for (AclModuleLevelDto dto : dtoList){
            levelAclModuleMap.put(dto.getLevel(),dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        //按找sel顺序进行排序
        Collections.sort(rootList,aclModuleSeqDtoComparator);

        //首层开始逐层生成树，转换成树形结构
        transformModuleTree(rootList,LevelUtil.ROOT,levelAclModuleMap);
        return rootList;
    }

    /**
     * 转换成树形结构,递归处理
     * @param dtoList
     * @param level
     * @param levelAclModuleMap
     */
    private void transformModuleTree(List<AclModuleLevelDto> dtoList,String level,Multimap<String,AclModuleLevelDto> levelAclModuleMap ){
        //注意: id为1的模块的level是0，但是level为0.1的是id为1的子模块
        for(int i = 0 ;i<dtoList.size();i++){
            AclModuleLevelDto dto = dtoList.get(i);
            String newtLevel = LevelUtil.calculateLevel(level,dto.getId());
            List<AclModuleLevelDto> tempList = (List<AclModuleLevelDto>)levelAclModuleMap.get(newtLevel);
            if(CollectionUtils.isNotEmpty(tempList)){
                Collections.sort(tempList,aclModuleSeqDtoComparator);
                //sysAclModuleLevelDto里面有一个存储此模块子模块的属性
                dto.setAclModuleList(tempList);
                transformModuleTree(tempList,newtLevel,levelAclModuleMap);
            }
        }
    }

    /**
     * 返回部门树
     * @return
     */
    public List<DeptLevelDto> deptTree(){
        List<SysDept> deptList = sysDeptMapper.getAllDept();

        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for(SysDept dept : deptList){
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }


    /**
     * 传入DeptLevelDto的list,返回一个树
     * @param deptLevelList
     * @return
     */

    private List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelList){
        if(CollectionUtils.isEmpty(deptLevelList)){
            return Lists.newArrayList();
        }
        //level -> [dept1,dept2,...]   Map<String,List<Object>>
        Multimap<String,DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();

        //如果是根节点,就加入rootList集合里面,技术部,产品部,研发部
        for (DeptLevelDto dto : deptLevelList){
            levelDeptMap.put(dto.getLevel(),dto);
            if (LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }

        // 按照seq从小到大排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        //递归生成树
        transformDeptTree(rootList,LevelUtil.ROOT,levelDeptMap);
        return rootList;
    }
    /**
     * 首层传入level是0的节点,leve = 0的所有部门传进来
     * level:0,0,all 0->0.1,0.2
     * level:0.1
     * level:0.2
     * @param deptLevelDtoList
     * @param level
     * @param levelDeptMap
     */

    private void transformDeptTree(List<DeptLevelDto> deptLevelDtoList,String level,Multimap<String,DeptLevelDto> levelDeptMap){
        for (int i = 0 ; i<deptLevelDtoList.size(); i++){
             //遍历该层的每个元素
            DeptLevelDto deptLevelDto = deptLevelDtoList.get(i);
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level,deptLevelDto.getId());
            //处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>)levelDeptMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList, deptSeqComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入到下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
            }
        }
    }


    private Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

    /**
     * 权限模块的比较器
     */
    private Comparator<AclModuleLevelDto> aclModuleSeqDtoComparator = new Comparator<AclModuleLevelDto>() {
        public int compare(AclModuleLevelDto o1, AclModuleLevelDto o2) {
            return o1.getSeq() - o2.getSeq();
        }
    };

   private Comparator<AclDto> aclSepComparator = new Comparator<AclDto>() {
       public int compare(AclDto o1, AclDto o2) {
           return o1.getSeq() - o2.getSeq();
       }
   };


}
