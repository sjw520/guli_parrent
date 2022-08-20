package com.atguigu.eduservice.client;

import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.UcenterMemberVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Component
@FeignClient(name = "service-ucenter",fallback = UcenterClientImpl.class)
public interface UcenterClient {

    @PostMapping("/educenter/member/getUserInfoOrder/{id}")
    public UcenterMemberVo getMemberInfoById(@PathVariable("id") String id);

}
