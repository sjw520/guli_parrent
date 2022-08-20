package com.atguigu.eduservice.service.impl;

import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.mapper.EduVideoMapper;
import com.atguigu.eduservice.service.EduVideoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-06-28
 */
@Service
public class EduVideoServiceImpl extends ServiceImpl<EduVideoMapper, EduVideo> implements EduVideoService {

    //住vodClient
    @Autowired
    private VodClient vodClient;

    @Override
    public void removeVideoByCourseId(String courseId) {
        //根据课程id查询所有的视频id
        QueryWrapper<EduVideo> wrapperVideo = new QueryWrapper<>();
        wrapperVideo.eq("course_id",courseId);
        //查询指定字段
        wrapperVideo.select("video_source_id");
        List<EduVideo> eduVideoList = baseMapper.selectList(wrapperVideo);
        //List<EduVideo>变成List<String>
        List<String> videoIds = new ArrayList<>();
        for (int i = 0; i < eduVideoList.size(); i++) {
            EduVideo eduVideo = eduVideoList.get(i);
            String videoSourceId = eduVideo.getVideoSourceId();
            if(!StringUtils.isEmpty(videoSourceId)){

                videoIds.add(videoSourceId);
            }
        }

        if(videoIds.size()>0){

            vodClient.deleteBatch(videoIds);
        }


        QueryWrapper<EduVideo> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);
        baseMapper.delete(wrapper);
    }
}
