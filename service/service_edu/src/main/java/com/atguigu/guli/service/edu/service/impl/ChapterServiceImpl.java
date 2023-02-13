package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.service.edu.entity.Chapter;
import com.atguigu.guli.service.edu.entity.Video;
import com.atguigu.guli.service.edu.entity.vo.ChapterVo;
import com.atguigu.guli.service.edu.entity.vo.VideoVo;
import com.atguigu.guli.service.edu.mapper.ChapterMapper;
import com.atguigu.guli.service.edu.mapper.VideoMapper;
import com.atguigu.guli.service.edu.service.ChapterService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter> implements ChapterService {

    @Autowired
    private VideoMapper videoMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeChapterById(String id) {
        //删除课时
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chapter_id",id);
        videoMapper.delete(queryWrapper);
        //删除课程章节
        return this.removeById(id);
    }

    @Override
    public List<ChapterVo> nestedList(String courseId) {
        // 组装章节列表： List<ChapterVo>

        //方案1：效率低 1+n sql
        // 获取章节列表信息 List<ChapterVo>
        // 遍历List<Chapter>{通过chapterid查询List<Video>}

        //方案2： 效率高 1+1sql
        //通过course_id 获取章节列表信息：List<Chapter> sql
        LambdaQueryWrapper<Chapter> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chapter::getCourseId,courseId);
        queryWrapper.orderByAsc(Chapter::getSort,Chapter::getId);
        List<Chapter> chapterList = baseMapper.selectList(queryWrapper);
        //通过course_id 查询List<Video> sql
        LambdaQueryWrapper<Video> videoQueryWrapper = new LambdaQueryWrapper<>();
        videoQueryWrapper.eq(Video::getCourseId,courseId);
        queryWrapper.orderByAsc(Chapter::getSort,Chapter::getId);
        List<Video> videoList = videoMapper.selectList(videoQueryWrapper);
        //组装chapterVo列表
        List<ChapterVo> chapterVoList = new ArrayList<>();
        for(Chapter chapter:chapterList){
            ChapterVo chapterVo = new ChapterVo();
            chapterVo.setId(chapter.getId())
                    .setTitle(chapter.getTitle())
                    .setSort(chapter.getSort());
            List<VideoVo> videoVoList = new ArrayList<>();
            for(Video video : videoList){
                if(video.getCourseId().equals(chapter.getCourseId())
                        && video.getChapterId().equals(chapter.getId())) {
                    VideoVo videoVo = new VideoVo();
                    videoVo.setId(video.getId())
                            .setTitle(video.getTitle())
                            .setSort(video.getSort())
                            .setFree(video.getFree())
                            .setVideoSourceId(video.getVideoSourceId());
                    videoVoList.add(videoVo);
                }
            }
            chapterVo.setChildren(videoVoList);
            chapterVoList.add(chapterVo);
        }
        return chapterVoList;

    }
}
