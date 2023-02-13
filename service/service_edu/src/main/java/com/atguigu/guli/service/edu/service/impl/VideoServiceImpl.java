package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.feign.VodVideoFeign;
import com.atguigu.guli.service.edu.mapper.VideoMapper;
import com.atguigu.guli.service.edu.service.VideoService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程视频 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Service
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {

    @Autowired
    private VodVideoFeign vodVideoFeign;


    @Override
    public void remoeVideoByIds(Wrapper<Video> wrapper) {
        List<Video> videoList = this.list(wrapper);
        StringBuffer videoIds = new StringBuffer();
        int size = videoList.size();
        for(int i =0;i < size ;i++) {
            if (null == videoList.get(i).getVideoSourceId()) {
                continue;
            }
            videoIds.append(videoList.get(i).getVideoSourceId());
            if(i == size -1 || i % 20 == 19){
                log.info("开始执行批量删除阿里云视频");
                vodVideoFeign.deleteVideoById1(videoIds.toString());
                videoIds = new StringBuffer();
            }else if(i % 20 < 19){
                videoIds.append(",");
            }
        }
    }
}
