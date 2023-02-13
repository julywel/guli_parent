package com.atguigu.guli.service.statistics.service.impl;

import com.atguigu.guli.common.base.result.R;
import com.atguigu.guli.service.statistics.entity.Daily;
import com.atguigu.guli.service.statistics.feign.UcenterMemberFeign;
import com.atguigu.guli.service.statistics.mapper.DailyMapper;
import com.atguigu.guli.service.statistics.service.DailyService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-30
 */
@Service
public class DailyServiceImpl extends ServiceImpl<DailyMapper, Daily> implements DailyService {

    @Autowired
    private UcenterMemberFeign ucenterMemberFeign;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createStatisticsByDay(String day) {

        //如果当日统计信息已存在，则删除记录
        LambdaQueryWrapper<Daily> dailyLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dailyLambdaQueryWrapper.eq(Daily::getDateCalculated,day);
        baseMapper.delete(dailyLambdaQueryWrapper);


        //远程获取了注册用户数的统计结果
        R r = ucenterMemberFeign.countRegisterNum(day);
        Integer registerNum = (Integer) r.getData().get("registerNum");

        int loginNum = RandomUtils.nextInt(100, 200);
        int videoNum = RandomUtils.nextInt(100,200);
        int courseNum = RandomUtils.nextInt(100,200);

        //创建统计数据对象
        Daily daily = new Daily();
        daily.setRegisterNum(registerNum)
                .setLoginNum(loginNum)
                .setVideoViewNum(videoNum)
                .setCourseNum(courseNum)
                .setDateCalculated(day);
        baseMapper.insert(daily);

    }

    @Override
    public Map<String, Map<String, Object>> getChartData(String begin, String end) {

        // 查四个数据
        Map<String,Map<String,Object>> map = new HashMap<>();
        //学员登录数统计
        Map<String, Object> login_num = this.getCharDataByType(begin, end, "login_num");
        //学生注册数统计
        Map<String, Object> register_num = this.getCharDataByType(begin, end, "register_num");
        //课程播放统计
        Map<String, Object> video_view_num = this.getCharDataByType(begin, end, "video_view_num");
        //每日新增课程数统计
        Map<String, Object> course_num = this.getCharDataByType(begin, end, "course_num");

        map.put("loginNum",login_num);
        map.put("registerNum",register_num);
        map.put("videoViewNum",video_view_num);
        map.put("courseNum",course_num);
        return map;
    }


    /**
     * 根据时间和要查询的列查询数据
     * @param begin
     * @param end
     * @param type 要查询的列名
     * @return
     */
    private Map<String,Object> getCharDataByType(String begin,String end,String type){

        Map<String,Object> map = new HashMap<>();

        List<String> xList = new ArrayList<>();//日期列表
        List<Integer> yList = new ArrayList<>();//数据列表

        QueryWrapper<Daily> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("date_calculated",type);
        queryWrapper.between("date_calculated",begin,end);

        List<Map<String, Object>> mapsData = baseMapper.selectMaps(queryWrapper);

        mapsData.forEach(data -> {
            String date_calculated = (String) data.get("date_calculated");
            xList.add(date_calculated);
            Integer count = (Integer) data.get(type);
            yList.add(count);
        });

        map.put("xData",xList);
        map.put("yData",yList);
        return map;
    }
}
