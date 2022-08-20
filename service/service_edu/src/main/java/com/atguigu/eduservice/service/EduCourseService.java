package com.atguigu.eduservice.service;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.frontvo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontvo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author sjw
 * @since 2022-06-28
 */
public interface EduCourseService extends IService<EduCourse> {
    //添加课程基本信息
    String saveCourseInfo(CourseInfoVo courseInfoVo);
    //根据id查询课程基本信息
    CourseInfoVo getCourseInfo(String courseId);
    //修改课程基本信息
    void updateCourseInfo(CourseInfoVo courseInfoVo);

    CoursePublishVo getPublishCourseInfo(String id);

    void removeCourse(String courseId);

    Map<String, Object> getCourseFrontList(Page<EduCourse> pageCourse, CourseFrontVo courseFrontVo);

    CourseWebVo getBaseCourseInfo(String courseId);
}
