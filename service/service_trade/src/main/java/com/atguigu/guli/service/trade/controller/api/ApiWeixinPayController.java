package com.atguigu.guli.service.trade.controller.api;

import com.atguigu.guli.service.trade.entity.Order;
import com.atguigu.guli.service.trade.service.OrderService;
import com.atguigu.guli.service.trade.service.WeixinPayService;
import com.atguigu.guli.common.base.result.*;
import com.atguigu.guli.service.trade.util.StreamUtils;
import com.atguigu.guli.service.trade.util.WeixinPayProperties;
import com.atguigu.guli.service.trade.util.sdk.WXPayUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@CrossOrigin
@Api(tags = "网站微信支付")
@RestController
@RefreshScope //动态刷新nacos配置中心文件
@Slf4j
@RequestMapping("/api/trade/weixin-pay")
public class ApiWeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    @Autowired
    private WeixinPayProperties weixinPayProperties;

    @Autowired
    private OrderService orderService;


    @ApiOperation("创建微信native支付方式")
    @GetMapping("create-native/{orderNo}")
    public R createNative(
            @ApiParam("订单号") @PathVariable("orderNo") String orderNo,
            HttpServletRequest request){
        String remoteAddr = request.getRemoteAddr();
        Map<String, Object> map = weixinPayService.createNative(orderNo, remoteAddr);
        return R.ok().data(map);
    }

    @PostMapping("callback/notify")
    public String wxNotify(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {

        log.info("\n callback/notify 被调用");
        ServletInputStream inputStream = request.getInputStream();
        String notifyXml = StreamUtils.inputStream2String(inputStream, "utf-8");
        log.info("\n notifyXml = \n " + notifyXml);

        //验签：验证签名是否正确
        if (WXPayUtil.isSignatureValid(notifyXml,weixinPayProperties.getPartnerKey())) {

            //解析返回结果
            Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyXml);

            //判断支付是否成功
            if("SUCCESS".equals(notifyMap.get("result_code"))){

                //金额校验
                String totalFee = notifyMap.get("total_fee"); // 支付结果返回的订单金额
                String outTradeNo = notifyMap.get("out_trade_no"); //订单号
                Order order = orderService.getOrderByOrderNo(outTradeNo); // 查询本地订单

                //校验返回的订单金额是否与商户侧的订单金额一致
                if( order != null && order.getTotalFee().intValue() == Integer.parseInt(totalFee) ) {

                    //接口调用的幂等性：无论接口被调用多少次，最后所影响的结果都是一致的
                    if(order.getStatus() != 1) {
                        //更新订单的状态
                        orderService.updateOrderStatus(notifyMap);
                    }

                    //支付成功：给微信发送我已接收通知的响应
                    //创建响应对象
                    Map<String, String> returnMap = new HashMap<>();
                    returnMap.put("return_code", "SUCCESS");
                    returnMap.put("return_msg", "OK");
                    String returnXml = WXPayUtil.mapToXml(returnMap);
                    response.setContentType("text/xml");
                    log.info("支付成功，通知已处理");
                    return returnXml;
                }
            }

        }

        //创建响应对象：微信接收到校验失败的结果后，会反复的调用当前回调函数
        Map<String, String> returnMap = new HashMap<>();
        returnMap.put("return_code", "FAIL");
        returnMap.put("return_msg", "");
        String returnXml = WXPayUtil.mapToXml(returnMap);
        response.setContentType("text/xml");
        log.info("校验失败");
        return returnXml;
    }
}
