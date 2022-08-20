package com.atguigu.eduservice.client;

import com.alibaba.excel.event.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderClientImpl implements OrderClient {
    @Override
    public boolean isBuyCourse(String courseId, String memberId) {
        return false;
    }
}
