package com.atguigu.vod.service.Impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.atguigu.commonutils.R;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.atguigu.vod.service.VodService;
import com.atguigu.vod.utils.ConstantVodUtils;
import com.atguigu.vod.utils.InitVodClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public class VodServiceImpl implements VodService {
    @Override
    public String uploadVideoAl(MultipartFile file) {

        try {
            //accessKeyId,accessKeySecret

            //fileName：上传文件原始名称
            String fileName = file.getOriginalFilename();

            //title：上传之后显示名称
            String title = fileName.substring(0,fileName.lastIndexOf("."));

            //inputStream：上传文件的输入流
            InputStream inputStream = file.getInputStream();

            UploadStreamRequest request = new UploadStreamRequest(ConstantVodUtils.ACCESSKEY_ID
                    , ConstantVodUtils.ACCESSKEY_SECRET
                    , title, fileName
                    , inputStream);

            UploadVideoImpl uploader = new UploadVideoImpl();
            UploadStreamResponse response = uploader.uploadStream(request);
            System.out.print("RequestId=" + response.getRequestId() + "\n");  //请求视频点播服务的请求ID
            String videoId = null;
            if (response.isSuccess()) {
                videoId = response.getVideoId();
            } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
                videoId = response.getVideoId();
            }
            return videoId;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void removeMoreAlyVideo(List videoIdList) {

        try {

            //初始化对象
            DefaultAcsClient client = InitVodClient.initVodClient(ConstantVodUtils.ACCESSKEY_ID,ConstantVodUtils.ACCESSKEY_SECRET);
            //船舰删除视频
            DeleteVideoRequest request = new DeleteVideoRequest();

            //voidIdList值转换成1，2，3
            String videoIds = StringUtils.join(videoIdList,",");
            request.setVideoIds(videoIds);

            client.getAcsResponse(request);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GuliException(20001,"删除视频失败");
        }


    }
}
