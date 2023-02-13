package com.atguigu.guli.service.trade.service.impl;

import com.atguigu.guli.common.base.result.ResultCodeEnum;
import com.atguigu.guli.common.base.util.ExceptionUtils;
import com.atguigu.guli.common.base.util.HttpClientUtils;
import com.atguigu.guli.service.base.exception.GuliException;
import com.atguigu.guli.service.trade.entity.Order;
import com.atguigu.guli.service.trade.service.OrderService;
import com.atguigu.guli.service.trade.service.WeixinPayService;
import com.atguigu.guli.service.trade.util.WeixinPayProperties;
import com.atguigu.guli.service.trade.util.sdk.WXPayUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private WeixinPayProperties weixinPayProperties;

    @Override
    public Map<String, Object> createNative(String orderNo, String remoteAddr) {

        try {
            // 根据订单获取订单
            Order order = orderService.getOrderByOrderNo(orderNo);

            //调用api的统一下单api
            HttpClientUtils client = new HttpClientUtils("https://api.mch.weixin.qq.com/pay/unifiedorder");

            //组合参数
            HashMap<String, String> params = new HashMap<>();
            params.put("appid", weixinPayProperties.getAppId());//公众号id
            params.put("mch_id", weixinPayProperties.getPartner());//商户号id
            params.put("nonce_str", WXPayUtil.generateNonceStr());//随机数
            params.put("body",order.getCourseTitle());//商品描述 "java11’最新发布‘"
            params.put("out_trade_no",orderNo);//商户订单号
            params.put("total_fee",order.getTotalFee().intValue()+"");//订单金额 ，单位是分
            params.put("spbill_create_ip",remoteAddr);//订单ip
            params.put("notify_url", weixinPayProperties.getNotifyUrl());//通知地址(回调地址)
            params.put("trade_type","NATIVE");//交易类型

            //组装签名  将参数转换成xml字符串,并且在字符串的最后追加计算的签名
            String xmlParams = WXPayUtil.generateSignedXml(params, weixinPayProperties.getPartnerKey());
            log.info(xmlParams);

            //将参数放入请求对象的方法体
            client.setXmlParam(xmlParams);
            //使用https协议传输
            client.setHttps(true);
            //使用post请求发送请求
            client.post();
            //得到响应
            String resultXml = client.getContent();
            log.info("resultXml:"+resultXml);

            //将xml响应结果转成map对象
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            //错误处理
            if("FAIL".equals(resultMap.get("return_code")) || "FAIL".equals(resultMap.get("result_code"))){
                log.error("微信支付统一下单错误 - "
                        + "return_code: " + resultMap.get("return_code")
                        + "return_msg: " + resultMap.get("return_msg")
                        + "result_code: " + resultMap.get("result_code")
                        + "err_code: " + resultMap.get("err_code")
                        + "err_code_des: " + resultMap.get("err_code_des"));
                throw new GuliException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
            }

            //要组装的结果对象
            HashMap<String, Object> map = new HashMap<>();
            map.put("result_code",resultMap.get("result_code"));//交易标识
            map.put("code_url",resultMap.get("code_url"));//二维码url
            map.put("course_id",order.getCourseId());//课程id
            map.put("total_fee",order.getTotalFee());//价格
            map.put("out_trade_no",order.getOrderNo());//商品订单

            return map;
        } catch (Exception e) {
            log.error(ExceptionUtils.getMessage(e));
            throw new GuliException(ResultCodeEnum.PAY_UNIFIEDORDER_ERROR);
        }

    }
}
