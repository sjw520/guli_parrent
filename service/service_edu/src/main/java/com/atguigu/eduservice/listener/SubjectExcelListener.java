package com.atguigu.eduservice.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.service.EduSubjectService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class SubjectExcelListener extends AnalysisEventListener<SubjectData> {

    private EduSubjectService eduSubjectService;
    public SubjectExcelListener() {
    }

    public SubjectExcelListener(EduSubjectService eduSubjectService) {
        this.eduSubjectService = eduSubjectService;
    }


    @Override
    public void invoke(SubjectData subjectData, AnalysisContext analysisContext) {
        if(subjectData == null){
            throw new GuliException(20001,"文件数据为空");
        }
        //判断一级分类是否重复
        EduSubject eduSubject = this.existOneSubject(eduSubjectService, subjectData.getOneSubjectName());
        if(eduSubject == null){//没有相同的一级分类
            eduSubject = new EduSubject();
            eduSubject.setParentId("0");//设置一级分类的id值
            eduSubject.setTitle(subjectData.getOneSubjectName());
            eduSubjectService.save(eduSubject);//给数据库添加一级分类
        }

        String pid = eduSubject.getId();

        EduSubject existTwoSubject = this.existTwoSubject(eduSubjectService, subjectData.getTwoSubjectName(), pid);
        if(existTwoSubject == null){//没有相同的二级分类
            existTwoSubject = new EduSubject();
            existTwoSubject.setParentId(pid);//设置二级分类的id值
            existTwoSubject.setTitle(subjectData.getTwoSubjectName());
            eduSubjectService.save(existTwoSubject);
        }
    }

    //判断一级分类不能重复添加
    private EduSubject existOneSubject(EduSubjectService subjectService,String name){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id","0");
        EduSubject oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }

    //判断二级分类不能重复添加
    private EduSubject existTwoSubject(EduSubjectService subjectService,String name,String pid){
        QueryWrapper<EduSubject> wrapper = new QueryWrapper<>();
        wrapper.eq("title",name);
        wrapper.eq("parent_id",pid);
        EduSubject twoSubject = subjectService.getOne(wrapper);
        return twoSubject;
    }
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
