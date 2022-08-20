package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.entity.vo.CourseQuery;
import com.atguigu.eduservice.service.EduCourseService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;
import java.util.List;

/**
 * <p>
 * 课程 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-06-28
 */
@RestController
@RequestMapping("/eduservice/course")
@CrossOrigin
public class EduCourseController {


    @Autowired
    private EduCourseService eduCourseService;

    //课程列表 TODO
    @GetMapping("/getCourseList")
    public R getCourseList(){
        List<EduCourse> list = eduCourseService.list(null);
        return R.ok().data("list",list);
    }

    //分页
    @GetMapping("/pageCourse/{page}/{limit}")
    public R pageCourse(@PathVariable long page,@PathVariable long limit){
        Page<EduCourse> coursepage = new Page<>(page,limit);

        eduCourseService.page(coursepage,null);
        List<EduCourse> records = coursepage.getRecords();
        long total = coursepage.getTotal();


        return R.ok().data("total",total).data("records",records);
    }

    //多条件查询带分页
    @PostMapping("/pageCourseCondition/{page}/{limit}")
    public R pageCourseCondition(@PathVariable long page,
                                 @PathVariable long limit,
                                 @RequestBody(required = false) CourseQuery courseQuery){
        //创建分页对象
        Page<EduCourse> pageParam = new Page<>(page,limit);
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("title",courseQuery.getTitle());
        wrapper.eq("status",courseQuery.getStatus());
         //调用方法实现多条件分页查询
        eduCourseService.page(pageParam,wrapper);
        //获取查询到的记录
        List<EduCourse> records = pageParam.getRecords();
        //获取总记录数
        long total = pageParam.getTotal();
        return R.ok().data("total",total).data("rows",records);
    }


    @PostMapping("/addCourseInfo")
    public R addCourseInfo(@RequestBody CourseInfoVo courseInfoVo){

        String id = eduCourseService.saveCourseInfo(courseInfoVo);

        return R.ok().data("courseId",id);
    }

    //根据id查询课程基本信息
    @GetMapping("/getCourseInfo/{courseId}")
    public R getCourseInfo(@PathVariable String courseId){
        CourseInfoVo courseInfoVo = eduCourseService.getCourseInfo(courseId);
        return R.ok().data("courseInfoVo",courseInfoVo);
    }


    //修改课程信息
    @PostMapping("/updateCourseInfo")
    public R updateCourseInfo(@RequestBody CourseInfoVo courseInfoVo ){

        eduCourseService.updateCourseInfo(courseInfoVo);

        return R.ok();

    }

    //根据课程id查询课程确认信息
    @GetMapping("/getPublishCourseInfo/{id}")
    public R getPublishCourseInfo(@PathVariable String id){
        CoursePublishVo coursePublishVo =  eduCourseService.getPublishCourseInfo(id);
        return R.ok().data("publishCourse",coursePublishVo);
    }

    //课程最终发布
    @PostMapping("/publishCourse/{id}")
    public R publishCourse(@PathVariable String id){
        EduCourse eduCourse = new EduCourse();
        eduCourse.setId(id);
        eduCourse.setStatus("Normal");
        eduCourseService.updateById(eduCourse);
        return R.ok();
    }


    //删除课程
    @DeleteMapping("/{courseId}")
    public R deleteCourse(@PathVariable String courseId){
        eduCourseService.removeCourse(courseId);
     return R.ok();
    }
}

