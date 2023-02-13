package com.atguigu.guli.service.statistics.controller;


import com.atguigu.guli.service.statistics.service.DailyService;
import com.atguigu.guli.common.base.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 网站统计日数据 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-30
 */
@Api(tags="统计分析管理")
//@CrossOrigin
@RestController
@RefreshScope //动态刷新nacos配置文件
@RequestMapping("/admin/statistics/daily")
public class DailyController {

    @Autowired
    private DailyService dailyService;

    @ApiOperation("生成统计记录")
    @PostMapping("create/{day}")
    public R createStatisticsByDay(
            @ApiParam("统计日期")
            @PathVariable String day) {
        dailyService.createStatisticsByDay(day);
        return R.ok().message("数据统计生成成功");
    }

    @ApiOperation("显示统计数据")
    @GetMapping("show-chart/{begin}/{end}")
    public R showChart(
            @ApiParam("开始时间") @PathVariable("begin") String begin,
            @ApiParam("结束时间") @PathVariable("end") String end){

        Map<String, Map<String, Object>> map = dailyService.getChartData(begin, end);
        return R.ok().data("chartData", map);
    }
}

