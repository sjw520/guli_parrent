package com.atguigu.eduservice.controller;

import com.atguigu.commonutils.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/eduservice/user")
@CrossOrigin
public class EduLoginController {


    @PostMapping("/login")
    public R login(){
        return R.ok().data("token","admin");
    }

    @GetMapping("/info")
    public R info(){

        return R.ok().data("roles","[admin]")
                .data("name","admin")
                .data("avatar","http://www.weixintouxiang.cn/weixin/20140607090832328.gif");
    }
}
