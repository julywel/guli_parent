package com.atguigu.guli.service.trade.controller.api;


import com.atguigu.guli.common.base.util.JwtInfo;
import com.atguigu.guli.common.base.util.JwtUtils;
import com.atguigu.guli.service.trade.entity.Order;
import com.atguigu.guli.service.trade.service.OrderService;
import com.atguigu.guli.common.base.result.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 订单 前端控制器
 * </p>
 *
 * @author TJS
 * @since 2022-11-28
 */
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@Slf4j
@Api(tags = "网站订单管理")
//@CrossOrigin
@RequestMapping("/api/trade/order")
public class ApiOrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation("新增订单")
    @PostMapping("auth/save/{courseId}")
    public R save(
            @ApiParam(value = "课程id",required = true)
            @PathVariable("courseId") String courseId, HttpServletRequest request){
        JwtInfo jwtInfo= JwtUtils.getMemberIdByJwtToken(request);
        String orderId = orderService.saveOrder(courseId,jwtInfo.getId());
        return R.ok().data("orderId",orderId);
    }

    @ApiOperation("获取订单")
    @GetMapping("auth/get/{orderId}")
    public R get(@PathVariable String orderId, HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        Order order = orderService.getByOrderId(orderId, jwtInfo.getId());
        return R.ok().data("item", order);
    }

    @ApiOperation("判断课程是否购买")
    @GetMapping("auth/is-buy/{courseId}")
    public R isBuyByCourseId(@PathVariable String courseId,HttpServletRequest request){
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        boolean isBuy = orderService.isBuyByCourseId(courseId,jwtInfo.getId());
        return R.ok().data("isBuy",isBuy);
    }

    @ApiOperation("获取当前用户订单列表")
    @GetMapping("auth/list")
    public R list(HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        List<Order> orderList = orderService.selectByMemberId(jwtInfo.getId());
        return R.ok().data("items",orderList);
    }

    @ApiOperation("删除订单")
    @DeleteMapping("auth/remove/{orderId}")
    public R remove(
            @ApiParam("订单id") @PathVariable String orderId,
            HttpServletRequest request) {
        JwtInfo jwtInfo = JwtUtils.getMemberIdByJwtToken(request);
        boolean result = orderService.removeById(orderId,jwtInfo.getId());
        if(result){
            return R.ok().message("删除成功");
        }
        return R.error().message("删除失败");
    }

    @GetMapping("/query-pay-status/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo) {
        boolean result = orderService.queryPayStatus(orderNo);

        return result ? R.ok().message("支付成功") : R.setResult(ResultCodeEnum.PAY_RUN);//支付中

    }
}

