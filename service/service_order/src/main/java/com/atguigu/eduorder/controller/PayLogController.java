package com.atguigu.eduorder.controller;


import com.atguigu.commonutils.R;
import com.atguigu.eduorder.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 支付日志表 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-08-15
 */
@RestController
@RequestMapping("/eduorder/paylog")
@CrossOrigin
public class PayLogController {

    @Autowired
    private PayLogService payLogService;

    //根据订单号，生成微信支付二维码的接口

    @GetMapping("/createNative/{orderNo}")
    public R createNative(@PathVariable String orderNo){
        //返回信息，包含二维码地址、其他信息
        Map map = payLogService.createNative(orderNo);
        System.out.println("返回二维码"+map);
        return R.ok().data(map);
    }


    //根据订单号查询订单支付状态
    @GetMapping("/queryPayStatus/{orderNo}")
    public R queryPayStatus(@PathVariable String orderNo){
        Map<String,String> map = payLogService.queryPayStatus(orderNo);
        System.out.println("订单状态"+map);
        if(map == null){
            return R.error().message("支付出错了");
        }
        //如果返回的map不为空，通过map获取订单的状态
        if(map.get("trade_state").equals("SUCCESS")){
            //添加记录到支付表里，并更新订单表的状态
            payLogService.updateOrderStatus(map);
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中").code(25000);
    }
}

