package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.CourseCollect;
import com.atguigu.guli.service.edu.entity.vo.CourseCollectVo;
import com.atguigu.guli.service.edu.mapper.CourseCollectMapper;
import com.atguigu.guli.service.edu.service.CourseCollectService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程收藏 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Service
public class CourseCollectServiceImpl extends ServiceImpl<CourseCollectMapper, CourseCollect> implements CourseCollectService {

    @Override
    public Boolean isCollectByCourseId(String courseId, String memberId) {
        //查询数据库是否存在
        LambdaQueryWrapper<CourseCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCollect::getCourseId,courseId);
        queryWrapper.eq(CourseCollect::getMemberId,memberId);
        Integer result = baseMapper.selectCount(queryWrapper);
        return result > 0;
    }

    @Override
    public boolean save(String courseId, String memberId) {
        if(this.isCollectByCourseId(courseId,memberId)){
            return true;
        }
        CourseCollect courseCollect = new CourseCollect();
        courseCollect.setCourseId(courseId)
                .setMemberId(memberId);
        int insert = baseMapper.insert(courseCollect);
        return insert > 0;
    }

    @Override
    public List<CourseCollectVo> getCourseCollectVoByMemberId(String memberId) {
        return baseMapper.selectListByMemberId(memberId);
    }

    @Override
    public boolean removeByCourseId(String courseId, String memberId) {
        LambdaQueryWrapper<CourseCollect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseCollect::getCourseId,courseId)
                .eq(CourseCollect::getMemberId,memberId);
        int delete = baseMapper.delete(queryWrapper);
        return delete > 0;
    }
}
