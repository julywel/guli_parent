package com.atguigu.guli.service.edu.mapper;

import com.atguigu.guli.service.edu.entity.CourseCollect;
import com.atguigu.guli.service.edu.entity.vo.CourseCollectVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 课程收藏 Mapper 接口
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Repository
public interface CourseCollectMapper extends BaseMapper<CourseCollect> {


    List<CourseCollectVo> selectListByMemberId(String memberId);
}
