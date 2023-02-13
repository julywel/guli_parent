package com.atguigu.guli.service.trade.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.service.base.dto.CourseDto;
import com.atguigu.guli.service.base.dto.MemberDto;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.trade.entity.PayLog;
import com.atguigu.guli.service.trade.feign.EduCourseFeign;
import com.atguigu.guli.service.trade.feign.UcenterMemberFeign;
import com.atguigu.guli.service.trade.mapper.PayLogMapper;
import com.atguigu.guli.service.trade.service.OrderService;
import com.atguigu.guli.service.trade.entity.Order;
import com.atguigu.guli.service.trade.mapper.OrderMapper;
import com.atguigu.guli.service.trade.util.OrderNoUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author TJS
 * @since 2022-11-28
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private EduCourseFeign eduCourseFeign;

    @Autowired
    private UcenterMemberFeign ucenterMemberFeign;

    @Autowired
    private PayLogMapper payLogMapper;

    @Override
    public String saveOrder(String courseId, String memberId) {

        //查询当前用户是否已有当前课程的订单
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getCourseId,courseId);
        queryWrapper.eq(Order::getMemberId,memberId);
        Order orderExist = baseMapper.selectOne(queryWrapper);
        if(orderExist != null){
            return orderExist.getId(); //如果订单已存在，则直接返回订单id
        }

        //查询课程信息
        CourseDto courseDto = eduCourseFeign.getCourseDtoById(courseId);
        if(courseDto == null){
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }
        //查询会员信息
        MemberDto memberDto = ucenterMemberFeign.getMemberDtoByMemberId(memberId);
        if(memberDto == null){
            throw new GuliException(ResultCodeEnum.PARAM_ERROR);
        }

        //创建订单
        Order order = new Order();
        order.setOrderNo(OrderNoUtils.getOrderNo()) //订单号
                .setCourseId(courseDto.getId())
                .setCourseTitle(courseDto.getTitle())
                .setCourseCover(courseDto.getCover())
                .setTeacherName(courseDto.getTeacherName())
                .setMemberId(memberDto.getId())
                .setMobile(memberDto.getMobile())
                .setNickname(memberDto.getNickname())
                .setStatus(0)  //未支付
                .setPayType(1)  // 微信支付
                .setTotalFee(courseDto.getPrice().multiply(new BigDecimal(100)));//单位：分

        baseMapper.insert(order);
        return order.getId();
    }

    @Override
    public Order getByOrderId(String orderId, String memberId) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("id", orderId)
                .eq("member_id", memberId);
        Order order = baseMapper.selectOne(queryWrapper);
        return order;
    }

    @Override
    public Boolean isBuyByCourseId(String courseId, String memberId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getCourseId,courseId)
                    .eq(Order::getMemberId,memberId)
                    .eq(Order::getStatus,1);
        Integer count = baseMapper.selectCount(queryWrapper);
        return count.intValue() > 0;
    }

    @Override
    public List<Order> selectByMemberId(String memberId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getMemberId,memberId);
        queryWrapper.orderByAsc(Order::getGmtCreate);
        List<Order> orderList = baseMapper.selectList(queryWrapper);
        return orderList;
    }

    @Override
    public Boolean removeById(String orderId, String memberId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getId,orderId);
        queryWrapper.eq(Order::getMemberId,memberId);
        int result = baseMapper.delete(queryWrapper);
        return result > 0;
    }

    @Override
    public Order getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getOrderNo,orderNo);
        Order order = baseMapper.selectOne(queryWrapper);
        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateOrderStatus(Map<String, String> notifyMap) {

        //更新订单状态
        String outTradeNo = notifyMap.get("out_trade_no");
        Order order = this.getOrderByOrderNo(outTradeNo);
        order.setStatus(1); //支付成功
        baseMapper.updateById(order);

        //记录支付日志
        PayLog payLog = new PayLog();
        payLog.setOrderNo(outTradeNo) //订单号
                .setPayTime(new Date()) //支付时间
                .setPayType(1)//支付类型，微信支付
                .setTotalFee(order.getTotalFee().longValue())
                .setTradeState(notifyMap.get("result_code"))
                .setTransactionId(notifyMap.get("transaction_id"))
                .setAttr(new Gson().toJson(notifyMap));
        payLogMapper.insert(payLog);

        //更新课程销量：有问题直接熔断
        eduCourseFeign.updateBuyCountById(order.getCourseId());
    }

    /**
     * 查询支付结果
     * @param orderNo
     * @return true 已支付 false 未支付
     */
    @Override
    public boolean queryPayStatus(String orderNo) {

        Order order = this.getOrderByOrderNo(orderNo);

        return order.getStatus() == 1;
    }
}
