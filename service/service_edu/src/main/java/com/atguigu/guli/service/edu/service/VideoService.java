package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.Video;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 课程视频 服务类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
public interface VideoService extends IService<Video> {

    void remoeVideoByIds(Wrapper<Video> wrapper);


}
