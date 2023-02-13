package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.CourseCollect;
import com.atguigu.guli.service.edu.entity.vo.CourseCollectVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
public interface CourseCollectService extends IService<CourseCollect> {

    Boolean isCollectByCourseId(String courseId, String memberId);

    boolean save(String courseId, String memberId);

    List<CourseCollectVo> getCourseCollectVoByMemberId(String memberId);

    boolean removeByCourseId(String courseId, String memberId);
}
