package com.atguigu.eduorder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.entity.PayLog;
import com.atguigu.eduorder.mapper.PayLogMapper;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.eduorder.service.PayLogService;
import com.atguigu.eduorder.utils.HttpClient;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 支付日志表 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-08-15
 */
@Service
public class PayLogServiceImpl extends ServiceImpl<PayLogMapper, PayLog> implements PayLogService {

    @Autowired
    private OrderService orderService;
    @Override
    public Map createNative(String orderNo) {

        try{
            //1、根据订单号查询订单信息
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            wrapper.eq("order_no",orderNo);
            Order order = orderService.getOne(wrapper);


            //2、使用map来设置生成二维码需要的参数
            Map map = new HashMap();
            map.put("appid","wx74862e0dfcf69954");//支付id
            map.put("mch_id", "1558950191");//商户号
            map.put("nonce_str", WXPayUtil.generateNonceStr());//生成随机的字符串，让每次生成的二维码不一样
            map.put("body", order.getCourseTitle());//生成二维码的名字
            map.put("out_trade_no", orderNo);//二维码唯一的标识
            map.put("total_fee", order.getTotalFee().multiply(new BigDecimal("100")).longValue()+"");//支付金额
            map.put("spbill_create_ip", "127.0.0.1");//现在进行支付的ip地址，实际项目使用项目的域名
            map.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");//支付后回调地址
            map.put("trade_type", "NATIVE");//支付类型，NATIVE:根据价格生成二维码


            //3、发送httpClient请求，传递参数【xml格式】，微信支付提供的固定地址
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            //参数1：要转换为xml格式的map
            //参数2：商户的key，用于加密二维码中的信息
            client.setXmlParam(WXPayUtil.generateSignedXml(map,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));
            client.setHttps(true);//上面发送请求的是https。默认不支持，需要设置为true支持
            client.post();


            //4、得到发送请求返回的结果
            //返回的结果是xml格式的
            String xml = client.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            Map m = new HashMap();
            m.put("out_trade_no",orderNo);
            m.put("course_id",order.getCourseId());
            m.put("total_fee",order.getTotalFee());
            m.put("result_code",resultMap.get("result_code")); //二维码操作状态码
            m.put("code_url",resultMap.get("code_url")); //二维码地址

            return m;
        }catch (Exception e){
        throw new GuliException(20001,"生成二维码失败");
        }

    }

    @Override
    public Map<String, String> queryPayStatus(String orderNo) {

        try{
            //1、 封装参数
            Map m = new HashMap<>();
            m.put("appid", "wx74862e0dfcf69954");
            m.put("mch_id", "1558950191");
            m.put("out_trade_no", orderNo);
            m.put("nonce_str", WXPayUtil.generateNonceStr());

            //2、发送httpClient请求
            HttpClient client = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            client.setHttps(true);
            client.setXmlParam(WXPayUtil.generateSignedXml(m,"T6m9iK73b0kn9g5v426MKfHQH7X8rKwb"));//通过商户key加密
            client.post();

            //3、返回第三方的数据
            String xml = client.getContent();


            //4、转成Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(xml);

            //5、返回
            return resultMap;
        }catch(Exception e){
            return null;
        }

    }

    @Override
    public void updateOrderStatus(Map<String, String> map) {
//获取前一步生成二维码中的订单号
        String orderNo = map.get("out_trade_no");
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no",orderNo);
        Order order = orderService.getOne(wrapper);


        //判断订单状态是否为1，为1就是支付过了
        if(order.getStatus().intValue() == 1){
            return;
        }
        //更新订单表中的叮当状态
        order.setStatus(1);//1代表已支付
        orderService.updateById(order);


        //向支付表里添加支付记录
        PayLog tPayLog = new PayLog();
        tPayLog.setOrderNo(orderNo);//支付订单号
        tPayLog.setPayTime(new Date());//支付时间
        tPayLog.setPayType(1);//支付类型
        tPayLog.setTotalFee(order.getTotalFee());//总金额(分)
        tPayLog.setTradeState(map.get("trade_state"));//支付状态
        tPayLog.setTransactionId(map.get("transaction_id"));//订单流水号
        tPayLog.setAttr(JSONObject.toJSONString(map));
        baseMapper.insert(tPayLog);
    }
}
