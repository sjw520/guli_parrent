package com.atguigu.eduservice.controller;


import com.atguigu.commonutils.JwtUtils;
import com.atguigu.commonutils.R;
import com.atguigu.commonutils.vo.UcenterMemberVo;
import com.atguigu.eduservice.client.UcenterClient;
import com.atguigu.eduservice.entity.EduComment;
import com.atguigu.eduservice.service.EduCommentService;
import com.atguigu.servicebase.exceptionhandler.GuliException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 评论 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-06-17
 */
@RestController
@RequestMapping("/eduservice/comment")
@CrossOrigin
public class EduCommentController {

    @Autowired
    private EduCommentService eduCommentService;

    @Autowired
    private UcenterClient ucenterClient;

    //根据课程id分页查询课程评论的方法
    @GetMapping("/getCommentPage/{page}/{limit}")
    public R getCommentPage(@PathVariable long page,@PathVariable long limit,String courseId){
        Page<EduComment> eduCommentPage = new Page<>(page,limit);

        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(courseId)){
            wrapper.eq("course_id",courseId);
        }

        wrapper.orderByDesc("gmt_create");

        eduCommentService.page(eduCommentPage,wrapper);

        List<EduComment> records = eduCommentPage.getRecords();
        long current = eduCommentPage.getCurrent();
        long total = eduCommentPage.getTotal();
        long size = eduCommentPage.getSize();
        long pages = eduCommentPage.getPages();
        boolean hasPrevious = eduCommentPage.hasPrevious();
        boolean hasNext = eduCommentPage.hasNext();

        Map<String,Object> map = new HashMap<>();
        map.put("current",current);
        map.put("size",size);
        map.put("total",total);
        map.put("pages",pages);
        map.put("hasPrevious",hasPrevious);
        map.put("hasNext",hasNext);
        map.put("list",records);

        return R.ok().data(map);

    }

    //添加评论
    @PostMapping("/auth/addComment")
    public R addComment(HttpServletRequest request,@RequestBody EduComment eduComment){
        String memberId = JwtUtils.getMemberIdByJwtToken(request);

        if(StringUtils.isEmpty(memberId)){
             throw new GuliException(20001,"请先登录");
        }

        eduComment.setMemberId(memberId);

        UcenterMemberVo memberVo = ucenterClient.getMemberInfoById(memberId);
        eduComment.setAvatar(memberVo.getAvatar());
        eduComment.setNickname(memberVo.getNickname());

        //保存评论
        eduCommentService.save(eduComment);

        return R.ok();
    }

}

