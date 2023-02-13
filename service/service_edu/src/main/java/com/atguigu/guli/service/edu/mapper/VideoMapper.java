package com.atguigu.guli.service.edu.mapper;

import com.atguigu.guli.service.edu.entity.Video;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 课程视频 Mapper 接口
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Repository
public interface VideoMapper extends BaseMapper<Video> {


}
