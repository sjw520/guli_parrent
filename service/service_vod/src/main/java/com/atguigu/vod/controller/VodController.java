package com.atguigu.vod.controller;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.commonutils.R;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.vod.service.VodService;
import com.atguigu.vod.utils.ConstantVodUtils;
import com.atguigu.vod.utils.InitVodClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.Path;
import java.util.List;

@RequestMapping("/eduvod/video")
@RestController
@CrossOrigin
public class VodController {

    @Autowired
    private VodService vodService;

    //上传视频到阿里云
    @PostMapping("/uploadAliyunVideo")
    public R uploadAliyunVideo(MultipartFile file){
        //返回上传视频的id值
        String videoId = vodService.uploadVideoAl(file);
        return R.ok().data("videoId",videoId);
    }

    //根据视频id删除阿里云视频
    @DeleteMapping("removeAlyVideo/{id}")
    public R removeAlyVideo(@PathVariable String id){

        try {

            //初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESSKEY_ID,ConstantVodUtils.ACCESSKEY_SECRET);
            //船舰删除视频
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(id);

            client.getAcsResponse(request);

            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"删除视频失败");
        }

    }


    //删除多个阿里云视频
    //参数是多个视频id
    @DeleteMapping("/delete-batch")
    public R deleteBatch(@RequestParam("videoIdList") List<String> videoIdList){
        vodService.removeMoreAlyVideo(videoIdList);
        return  R.ok();
    }


    //根据视频id获取视频凭证
    @GetMapping("/getPlayAuth/{id}")
    public R getPlayAuth(@PathVariable String id){
        try{
            DefaultAcsClient client =
                    InitVodClient.initVodClient(ConstantVodUtils.ACCESSKEY_ID, ConstantVodUtils.ACCESSKEY_SECRET);
            //创建获取凭证request和response对象
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();

            //向request设置视频id
            request.setVideoId(id);

            //调用方法得到凭证
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);

            String playAuth = response.getPlayAuth();

            return R.ok().data("playAuth",playAuth);
        }catch (Exception e){
            throw new GuliException(20001,"获取凭证失败");
        }
    }


}
