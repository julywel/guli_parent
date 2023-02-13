package com.atguigu.guli.service.vod.service.impl;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.vod.service.VideoService;
import com.atguigu.guli.service.vod.util.AliyunVodSDKUtils;
import com.atguigu.guli.service.vod.util.VodProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VodProperties vodProperties;

    @Override
    public String uploadVideo(InputStream file,String originalFilename) {

        String title = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String keyid = vodProperties.getKeyid();
        String keysecret = vodProperties.getKeysecret();
        //上传流请求
        UploadStreamRequest request = new UploadStreamRequest(keyid,keysecret,title,originalFilename,file );

        /* 是否使用默认水印（可选），指定模板组ID时，根据模板组配置确定是否使用默认水印*/
        //request.setShowWaterMark(true);
        /* 自定义消息回调设置及上传加速设置（可选）, Extend为自定义扩展设置，MessageCallback为消息回调设置，AccelerateConfig为上传加速设置（上传加速功能需要先申请开通后才能使用）*/
        //request.setUserData("{\"Extend\":{\"test\":\"www\",\"localId\":\"xxxx\"},\"MessageCallback\":{\"CallbackType\":\"http\",\"CallbackURL\":\"http://example.aliyundoc.com\"},\"AccelerateConfig\":{\"Type\":\"oss\",\"Domain\":\"****Bucket.oss-accelerate.aliyuncs.com\"}}");
        /* 视频分类ID（可选） */
        //request.setCateId(0);
        /* 视频标签，多个用逗号分隔（可选） */
        //request.setTags("标签1,标签2");
        /* 视频描述（可选）*/
        //request.setDescription("视频描述");
        /* 封面图片（可选）*/
        //request.setCoverURL("http://cover.example.com/image_01.jpg");
        /* 通过模板组ID转码（可选）*/
        //request.setTemplateGroupId(vodProperties.getTemplateGroupId());
        /* 工作流ID（可选）*/
        //request.setWorkflowId("d4430d07361f0*be1339577859b0****");
        /* 存储区域（可选）*/
        //request.setStorageLocation("in-201703232118266-5sejd****.oss-cn-shanghai.aliyuncs.com");
        /* 开启默认上传进度回调 */
        // request.setPrintProgress(true);
        /* 设置自定义上传进度回调（必须继承 VoDProgressListener） */
        /*默认关闭。如果开启了这个功能，上传过程中服务端会在日志中返回上传详情。如果不需要接收此消息，需关闭此功能*/
        // request.setProgressListener(new PutObjectProgressListener());
        /* 设置应用ID*/
        //request.setAppId("app-100****");
        /* 点播服务接入点 */
        //request.setApiRegionId("cn-shanghai");
        /* ECS部署区域*/
        // request.setEcsRegionId("cn-shanghai");

        //上传实现
        UploadVideoImpl uploader = new UploadVideoImpl();
        //上传流响应
        UploadStreamResponse response = uploader.uploadStream(request);

        String videoId = response.getVideoId();
        if(StringUtils.isEmpty(videoId)){
           log.error("阿里云上传失败"+response.getCode()+"-"+response.getMessage());
           throw new GuliException(ResultCodeEnum.VIDEO_UPLOAD_ALIYUN_ERROR);
        }
        return videoId;
    }




    @Override
    public void deleteVideoByIds(String videoIds) {
        try {
            DefaultAcsClient client = AliyunVodSDKUtils.initVodClient(vodProperties.getKeyid(),
                    vodProperties.getKeysecret());
            DeleteVideoRequest request = new DeleteVideoRequest();
            request.setVideoIds(videoIds);
            DeleteVideoResponse response = new DeleteVideoResponse();
            response = client.getAcsResponse(request);
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.VIDEO_DELETE_ALIYUN_ERROR);
        }
    }

    /**
     * 获取播放凭证
     * @param videoSourceId
     * @return
     * @throws ClientException
     */
    @Override
    public String getPlayAuth(String videoSourceId) throws ClientException {
        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", vodProperties.getKeyid(), vodProperties.getKeysecret());
        IAcsClient client = new DefaultAcsClient(profile);


        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoSourceId);

        GetVideoPlayAuthResponse response = client.getAcsResponse(request);

        return response.getPlayAuth();
    }


}
