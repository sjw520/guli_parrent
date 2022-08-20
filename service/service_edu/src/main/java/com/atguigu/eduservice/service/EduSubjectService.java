package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduSubject;
import com.atguigu.eduservice.entity.subject.OneSubject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author sjw
 * @since 2022-06-25
 */
public interface EduSubjectService extends IService<EduSubject> {

    void saveSubject(MultipartFile file,EduSubjectService subjectService);
    //课程分类列表属性结构
    List<OneSubject> getAllOneTwoSubject();
}
