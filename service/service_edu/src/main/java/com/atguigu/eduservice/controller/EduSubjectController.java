package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.atguigu.eduservice.service.EduSubjectService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-06-25
 */
@Api("课程分类管理类")
@RestController
@RequestMapping("/eduservice/subject")
@CrossOrigin
public class EduSubjectController {

    @Autowired
    private EduSubjectService eduSubjectService;
    @PostMapping("/addsubject")
    public R addSubject(MultipartFile file){
        eduSubjectService.saveSubject(file,eduSubjectService);

        return R.ok();
    }

    @GetMapping("/getAllSubject")
    public R getAllSubject(){
        List<OneSubject> list = eduSubjectService.getAllOneTwoSubject();
        return R.ok().data("list",list);
    }

}

