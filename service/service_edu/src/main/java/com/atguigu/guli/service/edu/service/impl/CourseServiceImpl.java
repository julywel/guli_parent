package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.edu.entity.*;
import com.atguigu.guli.service.edu.entity.form.CourseInfoForm;
import com.atguigu.guli.service.edu.entity.vo.*;
import com.atguigu.guli.service.edu.feign.OssFileFeign;
import com.atguigu.guli.service.edu.mapper.*;
import com.atguigu.guli.service.edu.service.CourseService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 课程 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-04
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {

    @Autowired
    private CourseDescriptionMapper courseDescriptionMapper;

    @Autowired
    private OssFileFeign ossFileFeign;

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private ChapterMapper chapterMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CourseCollectMapper courseCollectMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    private static AtomicInteger viewCount = new AtomicInteger();

    @Override
    public String saveCourseInfo(CourseInfoForm courseInfoForm) {

        //保存course
        Course course = new Course();
        //它会将courseInfoForm相对应的属性赋值给course
        BeanUtils.copyProperties(courseInfoForm,course);
        course.setStatus(Course.COURSE_DRAFT);
        //这里执行完毕后，mybatis会自动往course中回填一个id
        //由于将该表垂直分表，将描述的信息放入进edu_course_description中，在edu_course_description实体类中将主键类型设置为None
        //为了保持跟edu_course主键一样，所谓主键1对1关系，也就是用主键获取到同时course可以获取到description的东西，因为主键保持一致
        baseMapper.insert(course);

        //保存courseDescription
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription())
                //这里就可以获取回填的id了
                .setId(course.getId());
        courseDescriptionMapper.insert(courseDescription);


        return course.getId();
    }

    @Override
    public CourseInfoForm getCourseInfoById(String id) {

        CourseInfoForm courseInfoForm = new CourseInfoForm();
        //获取course中的值
        Course course = baseMapper.selectById(id);
        if(course == null){
            return null;
        }
        BeanUtils.copyProperties(course,courseInfoForm);

        //获取course-description
        CourseDescription courseDescription = courseDescriptionMapper.selectById(id);
        courseInfoForm.setDescription(courseDescription.getDescription());
        return courseInfoForm;
    }

    @Override
    public void updateCourseInfo(CourseInfoForm courseInfoForm) {

        //获取course对象
        Course course = new Course();
        BeanUtils.copyProperties(courseInfoForm,course);

        //获取coursedescription对象
        CourseDescription courseDescription = new CourseDescription();
        courseDescription.setDescription(courseInfoForm.getDescription())
                .setId(courseInfoForm.getId());
        baseMapper.updateById(course);
        courseDescriptionMapper.updateById(courseDescription);
    }

    @Override
    public IPage<CourseVo> selectPage(Long page, Long limit, CourseQueryVo courseQueryVo) {

        QueryWrapper<CourseVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("c.gmt_create");

        String title = courseQueryVo.getTitle();
        String teacherId = courseQueryVo.getTeacherId();
        String subjectId = courseQueryVo.getSubjectId();
        String subjectParentId = courseQueryVo.getSubjectParentId();

        //组合条件查询
        if(title != null && !"".equals(title)){
            queryWrapper.like("c.title",title);
        }
        if(teacherId != null && !"".equals(teacherId)){
            queryWrapper.eq("c.teacher_id",teacherId);
        }
        if(subjectId != null && !"".equals(subjectId)){
            queryWrapper.eq("c.subject_id",subjectId);
        }
        if(subjectParentId != null && !"".equals(subjectParentId)){
            queryWrapper.eq("c.subject_parent_id",subjectParentId);
        }
        //组装分页查询
        Page<CourseVo> courseVoPage = new Page<>(page,limit);

        //执行分页查询
        //只需要在mapper层传入封装好的分页组件即可,其它sql分页条件组装的过程由mp自动完成
        List<CourseVo> records = baseMapper.selectPageByCourseQueryVo(courseVoPage,queryWrapper);
        //将records设置到courseVoPage中
        return courseVoPage.setRecords(records);

    }

    @Override
    public boolean removeCoverById(String id) {
        Course course = baseMapper.selectById(id);
        if(course == null){
            return false;
        }
        String cover = course.getCover();
        if(cover == null){
            return false;
        }
        R r = ossFileFeign.removeFile(cover);
        return r.getSuccess();
    }

    /**
     * 数据库中外键约束的位置
     *  互联网分布式项目不允许使用外键与级联更新，一切设计级联的操作不要依赖数据库层，要在业务层进行解决
     *
     *  如果业务层解决级联删除功能
     *    那么先删除子表数据，再删除父表数据
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeCourseById(String id) {

        //根据courseId删除Video（课时）
        QueryWrapper<Video> videoQueryWrapper = new QueryWrapper<>();
        videoQueryWrapper.eq("course_id",id);
        videoMapper.delete(videoQueryWrapper);

        //根据courseId删除chapter（章节）
        QueryWrapper<Chapter> chapterQueryWrapper = new QueryWrapper<>();
        chapterQueryWrapper.eq("course_id",id);
        chapterMapper.delete(chapterQueryWrapper);

        //根据courseId删除comment（评论）
        QueryWrapper<Comment> commentQueryWrapper = new QueryWrapper<>();
        commentQueryWrapper.eq("course_id",id);
        commentMapper.delete(commentQueryWrapper);

        //根据courseId删除CourseCollect（课程收藏）
        QueryWrapper<CourseCollect> courseCollectQueryWrapper = new QueryWrapper<>();
        courseCollectQueryWrapper.eq("course_id",id);
        courseCollectMapper.delete(courseCollectQueryWrapper);

        //删除courseDescription(课程详情)
        courseDescriptionMapper.deleteById(id);

        return this.removeById(id);
    }

    @Override
    public CoursePublishVo getCoursePublishVoById(String id) {
        return baseMapper.selectCoursePublishVoById(id);
    }

    @Override
    public boolean publishCourseById(String id) {
        Course course = new Course();
        course.setId(id);
        course.setStatus(Course.COURSE_NORMAL);
        return this.updateById(course);
    }

    @Override
    public List<Course> webSelectList(WebCourseQueryVo webCourseQueryVo) {

        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();

        //查询已发布的课程
        queryWrapper.eq(Course::getStatus,Course.COURSE_NORMAL);

        String subjectId = webCourseQueryVo.getSubjectId();
        String subjectParentId = webCourseQueryVo.getSubjectParentId();
        String buyCountSort = webCourseQueryVo.getBuyCountSort();
        String gmtCreateSort = webCourseQueryVo.getGmtCreateSort();
        String priceSort = webCourseQueryVo.getPriceSort();

        if(!StringUtils.isEmpty(subjectParentId)){
            queryWrapper.eq(Course::getSubjectParentId,subjectParentId);
        }
        if(!StringUtils.isEmpty(subjectId)){
            queryWrapper.eq(Course::getSubjectId,subjectId);
        }
        if(!StringUtils.isEmpty(buyCountSort)){
            queryWrapper.orderByDesc(Course::getBuyCount);
        }
        if(!StringUtils.isEmpty(gmtCreateSort)){
            queryWrapper.orderByDesc(Course::getGmtCreate);
        }
        if(!StringUtils.isEmpty(priceSort)){
            queryWrapper.orderByDesc(Course::getPrice);
        }
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 获取课程信息并更新浏览量
     * @param id
     * @return
     */
    @Transactional
    @Override
    public WebCourseVo selectWebCourseVoById(String id) {
        Course course = baseMapper.selectById(id);
        //更新浏览数
        course.setViewCount(course.getViewCount()+1);
        baseMapper.updateById(course);
        //获取
        return baseMapper.selectWebCourseVoById(id);
    }

    @Cacheable(value = "index" ,key = "'selectHotCourse'")
    @Override
    public List<Course> selectHotCourse() {
        //获取热门前8门课程
        LambdaQueryWrapper<Course> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Course::getViewCount,Course::getBuyCount);
        queryWrapper.last("limit 8");
        List<Course> courseList = baseMapper.selectList(queryWrapper);
        return courseList;
    }

    /*第一种方式*/
//    @Override
//    public CourseDto getCourseDtoById(String courseId) {
//
//        CourseDto courseDto = new CourseDto();
//        //课程
//        Course course = baseMapper.selectById(courseId);
//
//        courseDto.setId(courseId)
//                .setTitle(course.getTitle())
//                .setCover(course.getCover())
//                .setPrice(course.getPrice());
//        //获取讲师姓名
//        Teacher teacher = teacherMapper.selectById(course.getTeacherId());
//        courseDto.setTeacherName(teacher.getName());
//        return courseDto;
//    }

    @Override
    public CourseDto getCourseDtoById(String courseId) {
        return baseMapper.selectCourseDtoById(courseId);
    }

    @Override
    public void updateBuyCountById(String id) {
        Course course = baseMapper.selectById(id);
        long buyCount = course.getBuyCount() + 1;
        course.setBuyCount(buyCount);
        baseMapper.updateById(course);
    }

}
