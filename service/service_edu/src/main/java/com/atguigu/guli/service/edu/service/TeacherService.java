package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.Teacher;
import com.atguigu.guli.service.edu.entity.vo.TeacherQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 讲师 服务类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
public interface TeacherService extends IService<Teacher> {

    //根据查询条件进行分页
    IPage<Teacher> selectPage(Page<Teacher> teacherPage, TeacherQueryVo teacherQueryVo);

    //根据name模糊查询，得到列表name
    List<Map<String, Object>> selectNameList(String key);

    //删除头像
    boolean removeAvatarById(String id);

    Map<String,Object> selectTeacherInfById(String id);

    List<Teacher> selectHotTeacher();
}
