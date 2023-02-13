package com.atguigu.guli.service.trade.service;

import com.atguigu.guli.service.trade.entity.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单 服务类
 * </p>
 *
 * @author TJS
 * @since 2022-11-28
 */
public interface OrderService extends IService<Order> {

    String saveOrder(String courseId, String memberId);

    Order getByOrderId(String orderId, String memberId);

    Boolean isBuyByCourseId(String courseId, String memberId);

    List<Order> selectByMemberId(String memberId);

    Boolean removeById(String orderId, String memberId);

    //根据订单号查询订单
    Order getOrderByOrderNo(String orderNo);

    void updateOrderStatus(Map<String, String> notifyMap);

    boolean queryPayStatus(String orderNo);
}
