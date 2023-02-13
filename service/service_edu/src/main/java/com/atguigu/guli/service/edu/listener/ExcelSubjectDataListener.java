package com.atguigu.guli.service.edu.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.excel.ExcelSubjectData;
import com.atguigu.guli.service.edu.mapper.SubjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ExcelSubjectDataListener extends AnalysisEventListener<ExcelSubjectData> {

    private SubjectMapper subjectMapper;

    /**
     * 遍历每一行数据
     * @param excelSubjectData
     * @param analysisContext
     */
    @Override
    public void invoke(ExcelSubjectData excelSubjectData, AnalysisContext analysisContext) {
        log.info("解析到一条记录:{}",excelSubjectData);
        //处理读取出来的数据
        String levelOneTitle = excelSubjectData.getLevelOneTitle(); //一级标题
        String levelTwoTitle = excelSubjectData.getLevelTwoTitle(); //二级标题
        log.info("levelOneTitle: {}",levelOneTitle);
        log.info("levelTwoTitle: {}",levelTwoTitle);

        //判断数据是否存在
        Subject subjectLevelOne = this.getLevelOneByTitle(levelOneTitle);
        String parentId = null;
        if(subjectLevelOne == null){
            //组装一级类别
            Subject subject = new Subject();
            subject.setParentId("0")
                    .setTitle(levelOneTitle);
            //存入数据库
            subjectMapper.insert(subject);
            parentId = subject.getId();
        }else{
            parentId = subjectLevelOne.getId();
        }

        //判断数据是否存在
        Subject subjectTwoByTitle = this.getLevelTwoByTitle(levelTwoTitle, parentId);
        if(subjectTwoByTitle == null){
            //组装二级类别
            Subject subject = new Subject();
            subject.setTitle(levelTwoTitle)
                    .setParentId(parentId);
            subjectMapper.insert(subject);
        }

    }

    /**
     * 所有数据的收尾工作
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("全部数据解析完成");
    }

    /**
     * 查询数据中是否有一级分类
     * @param title
     * @return
     */
    private Subject getLevelOneByTitle(String title){
        return this.getLevelTwoByTitle(title,"0");
    }

    /**
     * 根据分类的名称和父id查询数据是否存在
     * 二级分类
     * @param title
     * @param parentId
     * @return
     */
    private Subject getLevelTwoByTitle(String title,String parentId){
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id",parentId); //一级分类
        return subjectMapper.selectOne(queryWrapper);
    }
}
