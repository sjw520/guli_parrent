package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduservice.client.VodClient;
import com.atguigu.eduservice.entity.EduVideo;
import com.atguigu.eduservice.service.EduVideoService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 课程视频 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-06-28
 */
@RestController
@RequestMapping("/eduservice/video")
@CrossOrigin
public class EduVideoController {

    @Autowired
    private EduVideoService eduVideoService;

    @Autowired
    private VodClient vodClient;

    @PostMapping("/addVideo")
    public R addVideo(@RequestBody EduVideo eduVideo){
        eduVideoService.save(eduVideo);
        return R.ok();
    }

    //删除对应的阿里云视频
    @DeleteMapping("/{id}")
    public R deleteVideo(@PathVariable String id){
        EduVideo eduVideo = eduVideoService.getById(id);
        String videoSourceId = eduVideo.getVideoSourceId();
        if(!StringUtils.isEmpty(videoSourceId)){

            //根据视频id，远程调用实现视频删除
            R result = vodClient.removeAlyVideo(videoSourceId);
            if(result.getCode() == 20001){

                throw new GuliException(20001,"删除视频失败，熔断器");
            }
        }

        eduVideoService.removeById(id);
        return R.ok();
    }


    @GetMapping("/updateVideo/{id}")
    public R updateVideoById(@PathVariable String id){
        EduVideo update = eduVideoService.getById(id);
        return R.ok().data("update",update);
    }

    @PostMapping("/updateVideo")
    public R updateVideo(@RequestBody EduVideo eduVideo){
        eduVideoService.updateById(eduVideo);
        return R.ok();
    }

}

