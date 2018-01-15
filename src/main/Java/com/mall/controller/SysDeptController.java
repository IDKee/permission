package com.mall.controller;

import com.mall.common.JsonData;
import com.mall.dto.DeptLevelDto;
import com.mall.param.DeptParam;
import com.mall.service.IDeptService;
import com.mall.service.ITreeService;
import com.mall.service.Impl.DeptServiceImpl;
import com.mall.service.Impl.TreeServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by 王乾 on 2018/1/1.
 */
@Controller
@RequestMapping("/sys/dept")
@Slf4j
public class SysDeptController {

    @Resource
    private IDeptService iDeptService;
    @Resource
    private ITreeService iTreeService;

    /**
     * 进图页面
     * @return
     */
    @RequestMapping("/dept.page")
    public ModelAndView page(){
        return  new ModelAndView("dept");
    }
    /**
     * 新增部门
     * @param param
     * @return
     */
    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData saveDept(DeptParam param){
        iDeptService.save(param);
        return  JsonData.success();
    }

    /**
     * 获取部门树的接口
     * @return
     */
    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData tree(){
        List<DeptLevelDto> dtoList = iTreeService.deptTree();
        return  JsonData.success(dtoList);
    }

    /**
     * 更新部门
     * @param param
     * @return
     */
    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(DeptParam param){
        iDeptService.update(param);
        return  JsonData.success();
    }

    /**
     * 删除部门
     * @param id
     * @return
     */
    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData delete(@RequestParam("id") int id) {
        iDeptService.delete(id);
        return JsonData.success();
    }


}
