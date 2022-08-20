package com.atguigu.eduorder.service.impl;

import com.atguigu.commonutils.ordervo.CourseWebVoOrder;
import com.atguigu.commonutils.ordervo.UcenterMemberOrder;
import com.atguigu.eduorder.client.EduClient;
import com.atguigu.eduorder.client.UcenterClient;
import com.atguigu.eduorder.entity.Order;
import com.atguigu.eduorder.mapper.OrderMapper;
import com.atguigu.eduorder.service.OrderService;
import com.atguigu.eduorder.utils.OrderNoUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 订单 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-08-15
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {


    @Autowired
    private EduClient eduClient;
    @Autowired
    private UcenterClient ucenterClient;
    @Override
    public String createOrders(String courseId, String memberId) {
        //根据用户id，获取用户信息
        UcenterMemberOrder userInfoOrder = ucenterClient.getMemberInfoById(memberId);
        //根据课程id，获取课程信息
        CourseWebVoOrder courseInfoOrder = eduClient.getCourseInfoOrder(courseId);


        Order order = new Order();
        order.setOrderNo(OrderNoUtil.getOrderNo());//订单号
        order.setCourseId(courseId);//课程id
        order.setCourseTitle(courseInfoOrder.getTitle());
        order.setCourseCover(courseInfoOrder.getCover());
        order.setTeacherName(courseInfoOrder.getTeacherName());
        order.setTotalFee(courseInfoOrder.getPrice());
        order.setMemberId(memberId);
        order.setMobile(userInfoOrder.getMobile());
        order.setNickname(userInfoOrder.getNickname());
        order.setStatus(0);//支付状态：（ 1：已支付，0：未支付 ）
        order.setPayType(1);//支付类型： 1：微信 ， 2：支付宝
        baseMapper.insert(order);

        return order.getOrderNo();
    }
}
