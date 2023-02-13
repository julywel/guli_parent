package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.edu.entity.Course;
import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.atguigu.guli.service.edu.feign.OssFileFeign;
import com.atguigu.guli.service.edu.mapper.CourseMapper;
import com.atguigu.guli.service.edu.mapper.TeacherMapper;
import com.atguigu.guli.service.edu.service.TeacherService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements TeacherService {

    @Autowired
    private OssFileFeign ossFileFeign;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public IPage<Teacher> selectPage(Page<Teacher> pageParam, TeacherQueryVo teacherQueryVo) {
        //显示分页查询的列表
        //1、排序问题：按照sort字段排序
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        //2.分页查询
        if(teacherQueryVo == null){
            return baseMapper.selectPage(pageParam,queryWrapper);
        }
        //3.条件查询
        String name = teacherQueryVo.getName();
        Integer level = teacherQueryVo.getLevel();
        String begin = teacherQueryVo.getJoinDateBegin();
        String end = teacherQueryVo.getJoinDateEnd();

        if(!StringUtils.isEmpty(name)){
            queryWrapper.likeRight("name",name);
        }
        if(level != null){
            queryWrapper.eq("level",level);
        }
        if(!StringUtils.isEmpty(begin)){
            queryWrapper.ge("join_date",begin);
        }
        if(!StringUtils.isEmpty(end)){
            queryWrapper.le("join_date",end);
        }

        return baseMapper.selectPage(pageParam,queryWrapper);
    }

    @Override
    public List<Map<String, Object>> selectNameList(String key) {
        List<Map<String,Object>> names = new ArrayList<>();
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.select("name");
        queryWrapper.like("name",key);
        names = baseMapper.selectMaps(queryWrapper);

        return names;

    }


    @Override
    public boolean removeAvatarById(String id) {
        //根据id获取讲师头像的url
        Teacher teacher = baseMapper.selectById(id);
        String avatar;
        if(teacher != null) {
            avatar = teacher.getAvatar();
            if(!StringUtils.isEmpty(avatar)){
                R r = ossFileFeign.removeFile(avatar);
                return r.getSuccess();
            }
        }
        return false;
    }

    @Override
    public Map<String, Object> selectTeacherInfById(String id) {

        Teacher teacher = baseMapper.selectById(id);
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Course::getTeacherId,id);
        List<Course> courseList = courseMapper.selectList(queryWrapper);
        Map<String,Object> teacherInfo = new HashMap<>();
        teacherInfo.put("teacher",teacher);
        teacherInfo.put("courseList",courseList);
        return teacherInfo;
    }

    @Cacheable(value = "index",key = "'selectHotTeacher'")
    @Override
    public List<Teacher> selectHotTeacher() {
        LambdaQueryWrapper<Teacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Teacher::getSort);
        queryWrapper.orderByAsc(Teacher::getGmtCreate);
        queryWrapper.last("limit 4");
        List<Teacher> teachers = baseMapper.selectList(queryWrapper);
        return teachers;
    }
}
