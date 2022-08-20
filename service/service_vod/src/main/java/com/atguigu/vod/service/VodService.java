package com.atguigu.vod.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VodService {
    String uploadVideoAl(MultipartFile file);

    void removeMoreAlyVideo(List videoIdList);
}
