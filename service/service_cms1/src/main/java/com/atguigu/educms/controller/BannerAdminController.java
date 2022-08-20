package com.atguigu.educms.controller;


import com.atguigu.commonutils.R;
import com.atguigu.educms.entity.CrmBanner;
import com.atguigu.educms.entity.vo.BannerQuery;
import com.atguigu.educms.service.CrmBannerService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 首页banner表 前端控制器
 * </p>
 *
 * @author sjw
 * @since 2022-07-23
 */
@RestController
@RequestMapping("/educms/banneradmin")
@CrossOrigin
public class BannerAdminController {

    @Autowired
    private CrmBannerService crmBannerService;
    //分页查询banner
    @PostMapping("/pageBanner/{page}/{limit}")
    public R pageBanner(@PathVariable long page, @PathVariable long limit, @RequestBody(required = false) BannerQuery bannerQuery){
        Page<CrmBanner> pageBanner = new Page<>(page,limit);
        crmBannerService.page(pageBanner,null);

        return R.ok().data("items",pageBanner.getRecords()).data("total",pageBanner.getTotal());
    }


    //添加banner
    @PostMapping("/addBanner")
    public R addBanner(@RequestBody CrmBanner crmBanner){
        crmBannerService.save(crmBanner);
        return R.ok();
    }

    @ApiOperation(value = "获取Banner")
    @GetMapping("/get/{id}")
    public R get(@PathVariable String id) {
        CrmBanner banner = crmBannerService.getById(id);
        return R.ok().data("item", banner);
    }

    @ApiOperation(value = "修改Banner")
    @PutMapping("/update")
    public R updateById(@RequestBody CrmBanner banner) {
        crmBannerService.updateById(banner);
        return R.ok();
    }

    @ApiOperation(value = "删除Banner")
    @DeleteMapping("/remove/{id}")
    public R remove(@PathVariable String id) {
        crmBannerService.removeById(id);
        return R.ok();
    }

}

