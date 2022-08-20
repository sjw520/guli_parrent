package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.entity.EduTeacher;
import com.atguigu.eduservice.mapper.EduTeacherMapper;
import com.atguigu.eduservice.service.EduTeacherService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-06-17
 */
@Service
public class EduTeacherServiceImpl extends ServiceImpl<EduTeacherMapper, EduTeacher> implements EduTeacherService {

    @Override
    public Map<String, Object> getTeacherFrontList(Page<EduTeacher> pageTeacher) {

        QueryWrapper<EduTeacher> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        baseMapper.selectPage(pageTeacher,wrapper);

        //总记录数
        long total = pageTeacher.getTotal();
        //当前页
        long current = pageTeacher.getCurrent();
        //每页记录数
        long size = pageTeacher.getSize();
        //查询到的对象
        List<EduTeacher> teacherList = pageTeacher.getRecords();
        //总页数
        long pages = pageTeacher.getPages();
        //是否有上一页
        boolean hasPrevious = pageTeacher.hasPrevious();
        //是否有下一页
        boolean hasNext = pageTeacher.hasNext();


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
}
