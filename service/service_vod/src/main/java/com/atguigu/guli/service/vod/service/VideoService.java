package com.atguigu.guli.service.vod.service;

import com.aliyuncs.exceptions.ClientException;

import java.io.InputStream;

public interface VideoService {

    // 上传文件 返回值返回videoId
    String uploadVideo(InputStream file,String originalFilename);

    void deleteVideoByIds(String videoIds);

    String getPlayAuth(String videoSourceId) throws ClientException;
}
