package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduCourse;
import com.atguigu.eduservice.entity.EduCourseDescription;
import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.entity.frontvo.CourseFrontVo;
import com.atguigu.eduservice.entity.frontvo.CourseWebVo;
import com.atguigu.eduservice.entity.vo.CourseInfoVo;
import com.atguigu.eduservice.entity.vo.CoursePublishVo;
import com.atguigu.eduservice.mapper.EduCourseMapper;
import com.atguigu.eduservice.service.EduChapterService;
import com.atguigu.eduservice.service.EduCourseDescriptionService;
import com.atguigu.eduservice.service.EduCourseService;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.pkcs.ContentInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-06-28
 */
@Service
public class EduCourseServiceImpl extends ServiceImpl<EduCourseMapper, EduCourse> implements EduCourseService {

    //课程描述注入
    @Autowired
    private EduCourseDescriptionService eduCourseDescriptionService;

    //注入小节和章节service
    @Autowired
    private EduVideoService eduVideoService;

    @Autowired
    private EduChapterService eduChapterService;

    @Override
    public String saveCourseInfo(CourseInfoVo courseInfoVo) {

        //向课程表里面添加课程基本信息
        //courseInfoVo 转换成 EduCourse对象
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);
        int insert = baseMapper.insert(eduCourse);

        if(insert == 0){//表示添加失败
            throw new GuliException(20001,"添加课程信息失败");
        }

        //获取添加之后的课程id
        String id = eduCourse.getId();

        //想课程简介表里面添加课程简介
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        eduCourseDescription.setId(id);
        eduCourseDescriptionService.save(eduCourseDescription);

            return id;
    }

    @Override
    public CourseInfoVo getCourseInfo(String courseId) {
        //查询课程表
        EduCourse eduCourse = baseMapper.selectById(courseId);
        CourseInfoVo courseInfoVo = new CourseInfoVo();
        BeanUtils.copyProperties(eduCourse,courseInfoVo);
        //查询描述表
        EduCourseDescription courseDescription = eduCourseDescriptionService.getById(courseId);
        courseInfoVo.setDescription(courseDescription.getDescription());


        return courseInfoVo;
    }

    //修改课程信息
    @Override
    public void updateCourseInfo(CourseInfoVo courseInfoVo) {
        //修改课程表
        EduCourse eduCourse = new EduCourse();
        BeanUtils.copyProperties(courseInfoVo,eduCourse);

        int update = baseMapper.updateById(eduCourse);

        if(update == 0){
            throw new GuliException(20001,"修改信息失败");
        }

        //修改描述表
        EduCourseDescription eduCourseDescription = new EduCourseDescription();
        eduCourseDescription.setId(courseInfoVo.getId());
        eduCourseDescription.setDescription(courseInfoVo.getDescription());
        eduCourseDescriptionService.updateById(eduCourseDescription);


    }

    @Override
    public CoursePublishVo getPublishCourseInfo(String id) {

        CoursePublishVo publishCourseInfo = baseMapper.getPublishCourseInfo(id);
        return publishCourseInfo;
    }

    @Override
    public void removeCourse(String courseId) {
        eduVideoService.removeVideoByCourseId(courseId);

        eduChapterService.removeChapterByCourseId(courseId);

        eduCourseDescriptionService.removeById(courseId);

        int result = baseMapper.deleteById(courseId);
        if(result == 0 ){
            throw new GuliException(20001,"删除失败");
        }

    }

    @Override
    public Map<String, Object> getCourseFrontList(Page<EduCourse> pageParam, CourseFrontVo courseFrontVo) {
        QueryWrapper<EduCourse> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(courseFrontVo.getSubjectParentId())){   //一级分类
            wrapper.eq("subject_parent_id",courseFrontVo.getSubjectParentId());
        }

        if(!StringUtils.isEmpty(courseFrontVo.getSubjectId())){   //二级分类
            wrapper.eq("subject_id",courseFrontVo.getSubjectId());
        }

        if(!StringUtils.isEmpty(courseFrontVo.getBuyCountSort())){   //关注度
            wrapper.orderByDesc("buy_count");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getPriceSort())){//价格
            wrapper.orderByDesc("price");
        }
        if (!StringUtils.isEmpty(courseFrontVo.getGmtCreateSort())){//最新，创建时间
            wrapper.orderByDesc("gmt_create");
        }

        baseMapper.selectPage(pageParam,wrapper);

        //总记录数
        long total = pageParam.getTotal();
        //当前页
        long current = pageParam.getCurrent();
        //每页记录数
        long size = pageParam.getSize();
        //查询到的对象
        List<EduCourse> teacherList = pageParam.getRecords();
        //总页数
        long pages = pageParam.getPages();
        //是否有上一页
        boolean hasPrevious = pageParam.hasPrevious();
        //是否有下一页
        boolean hasNext = pageParam.hasNext();


        Map<String,Object> map = new HashMap<>();

        map.put("total",total);
        map.put("current",current);
        map.put("size",size);
        map.put("list",teacherList);
        map.put("hasPrevious",hasPrevious);
        map.put("hasNext",hasNext);
        map.put("pages",pages);

        return map;
    }

    @Override
    public CourseWebVo getBaseCourseInfo(String courseId) {
        return baseMapper.getBaseCourseInfo(courseId);
    }
}
