package com.atguigu.guli.service.edu.service;

import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.vo.SubjectVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.InputStream;
import java.util.List;

/**
 * <p>
 * 课程科目 服务类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
public interface SubjectService extends IService<Subject> {

    // 批量导入
    void batchImport(InputStream inputStream);

    // 嵌套查询
    List<SubjectVo> nestedList();
}
