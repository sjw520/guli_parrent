package com.atguigu.eduservice.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.excel.SubjectData;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.atguigu.eduservice.entity.subject.TwoSubject;
import com.atguigu.eduservice.listener.SubjectExcelListener;
import com.atguigu.eduservice.mapper.EduSubjectMapper;
import com.atguigu.eduservice.service.EduSubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.Subject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-06-25
 */
@Service
public class EduSubjectServiceImpl extends ServiceImpl<EduSubjectMapper, EduSubject> implements EduSubjectService {

    //添加课程分类
    @Override
    public void saveSubject(MultipartFile file,EduSubjectService eduSubjectService) {
        try{
            InputStream inputStream = file.getInputStream();
            EasyExcel.read(inputStream, SubjectData.class,new SubjectExcelListener(eduSubjectService)).sheet().doRead();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<OneSubject> getAllOneTwoSubject() {
        //查询一级分类
        QueryWrapper<EduSubject> wrapperOne = new QueryWrapper<>();
        wrapperOne.eq("parent_id","0");
        List<EduSubject> oneSubjectsList = baseMapper.selectList(wrapperOne);

        //查询二级分类
        QueryWrapper<EduSubject> wrapperTwo = new QueryWrapper<>();
        wrapperTwo.ne("parent_id","0");
        List<EduSubject> twoSubjectsList = baseMapper.selectList(wrapperTwo);

        //创建用于封装最后数据的集合
        List<OneSubject> finalSubjectList = new ArrayList<>();

        //封装一级分类
        //查询出来所有的一级分类list集合遍历，得到每个一级分类对象，获取每个一级分类对象值
        //封装到要求的最终list集合中
        for (int i = 0; i < oneSubjectsList.size(); i++) {
            //得到oneSubjectsList中每个eduSubject对象
            EduSubject oSubject = oneSubjectsList.get(i);

            OneSubject oneSubject = new OneSubject();
            //把eduSubject值复制到oneSubject中去【要求两个类的复制注入的属性名一致】
            BeanUtils.copyProperties(oSubject,oneSubject);

            //多个OneSubject放到finalSubjectList里面
            finalSubjectList.add(oneSubject);

            //在一级分类循环遍历查询所有的二级分类
            //创建list集合封装每个一级分类的二级分类
            List<TwoSubject> finalTwoSubjects = new ArrayList<>();

            for (int m = 0; m < twoSubjectsList.size(); m++) {
                //获取每个二级分类
                EduSubject eduSubject1 = twoSubjectsList.get(m);
                //判断二级分类parentid和一级分类id是否一样
                if(eduSubject1.getParentId().equals(oSubject.getId())){
                    //把tSubject值复制到TwoSubject，最终放在twoSubjectsList中
                    TwoSubject twoSubject = new TwoSubject();
                    BeanUtils.copyProperties(eduSubject1,twoSubject);
                    finalTwoSubjects.add(twoSubject);
                }
            }
            //把一级下面所有二级分类放到oneSubject里面
            oneSubject.setChildren(finalTwoSubjects);

        }


        return finalSubjectList;
    }
}
