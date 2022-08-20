package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduComment;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.vo.TeacherQuery;
import com.atguigu.eduservice.service.EduCommentService;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 讲师 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-06-17
 */
@RestController
@RequestMapping("/eduservice/teacher")
@Api(description = "讲师管理类")
@CrossOrigin
public class EduTeacherController {


    @Autowired
    private EduTeacherService eduTeacherService;


    @ApiOperation("查询全部")
    @GetMapping("/findAll")
    public R selectAll(){
        List<EduTeacher> list = eduTeacherService.list(null);
        return R.ok().data("item",list);
    }

    @ApiOperation(value = "逻辑删除讲师")
    @DeleteMapping("{id}")
    public R removeTecher(@ApiParam(name = "id",value = "讲师ID",required = true)
                          @PathVariable String id){
        boolean flag = eduTeacherService.removeById(id);
        if(flag){
            return R.ok();
        }else {
            return R.error();
        }
}

    @GetMapping("/pageTeacher/{current}/{limit}")
    @ApiOperation("分页功能")
    public R pageTeacherCondition(@ApiParam(name = "current",value = "当前页码") @PathVariable long current,
                                  @ApiParam(name = "limit",value = "每页记录树") @PathVariable long limit){
        //船舰分页对象
        Page<EduTeacher> teacherPage = new Page<>(current,limit);
        eduTeacherService.page(teacherPage,null);
        //获取总记录数
        long total = teacherPage.getTotal();
        //获取查询到的数据
        List<EduTeacher> records = teacherPage.getRecords();
        return R.ok().data("total",total).data("rows",records);
    }


    @ApiOperation("多条件分页查询")
    @PostMapping("/pageTeacherCondition/{current}/{limit}")
    public R pageTeacherCondition(@PathVariable long current,
                                  @PathVariable long limit,
                                  @RequestBody(required = false) TeacherQuery teacherQuery){//requestBody需要post提交 require表示这个值可以没有

        Page<EduTeacher> page = new Page<>(current,limit);
        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        String name = teacherQuery.getName();
        Integer level = teacherQuery.getLevel();
        String begin = teacherQuery.getBegin();
        String end = teacherQuery.getEnd();
        if(!StringUtils.isEmpty(name)){
            wrapper.like("name",name);
        }

        if(!StringUtils.isEmpty(level)){
            wrapper.eq("level",level);
        }
        if (!StringUtils.isEmpty(begin)){
            wrapper.ge("gmt_create",begin);
        }
        if (!StringUtils.isEmpty(end)){
            wrapper.le("gmt_create",end);
        }
        wrapper.orderByDesc("gmt_create");
        eduTeacherService.page(page, wrapper);

        long total = page.getTotal();//获取到总记录数
        List<EduTeacher> records = page.getRecords();
        return R.ok().data("total",total).data("rows",records);
    }


    @PostMapping("/save")
    @ApiOperation("新增讲师")
    public R save(@RequestBody EduTeacher eduTeacher){
        boolean save = eduTeacherService.save(eduTeacher);
        if (save){
           return R.ok();
        }else{
           return R.error();
        }
    }

    @ApiOperation("根据讲师id查询")
    @GetMapping("/getTeacher/{id}")
    public R getTeacher(@PathVariable String id){
        EduTeacher teacher = eduTeacherService.getById(id);
        return R.ok().data("teacher",teacher);
    }

    @ApiOperation("修改讲师")
    @PostMapping("/updateTeacher")
    public R updateTeacher(@RequestBody EduTeacher eduTeacher){
        boolean b = eduTeacherService.updateById(eduTeacher);
        if (b){
            return R.ok();
        }else{
            return R.error();
        }
    }

}

