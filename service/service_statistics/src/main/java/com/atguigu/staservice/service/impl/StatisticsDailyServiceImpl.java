package com.atguigu.staservice.service.impl;

import com.atguigu.commonutils.R;
import com.atguigu.staservice.client.UcenterClient;
import com.atguigu.staservice.entity.StatisticsDaily;
import com.atguigu.staservice.mapper.StatisticsDailyMapper;
import com.atguigu.staservice.service.StatisticsDailyService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 网站统计日数据 服务实现类
 * </p>
 *
 * @author sjw
 * @since 2022-08-16
 */
@Service
public class StatisticsDailyServiceImpl extends ServiceImpl<StatisticsDailyMapper, StatisticsDaily> implements StatisticsDailyService {

    @Autowired
    private UcenterClient ucenterClient;

    @Override
    public void registerCount(String day) {

        //添加记录之前删除相同数据
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.eq("date_calculated",day);
        baseMapper.delete(wrapper);

        R registerR = ucenterClient.countRegister(day);
        Integer countRegister = (Integer) registerR.getData().get("countRegister");

        StatisticsDaily sta = new StatisticsDaily();
        sta.setRegisterNum(countRegister);//注册人数
        sta.setDateCalculated(day);//统计日期


        sta.setVideoViewNum(RandomUtils.nextInt(100,200));
        sta.setLoginNum(RandomUtils.nextInt(100,200));
        sta.setCourseNum(RandomUtils.nextInt(100,200));
        baseMapper.insert(sta);
    }

    @Override
    public Map<String, Object> getShowData(String type,String begin,String end) {
        QueryWrapper<StatisticsDaily> wrapper = new QueryWrapper<>();
        wrapper.between("date_calculated",begin,end);
        wrapper.select("date_calculated",type);
        List<StatisticsDaily> staList = baseMapper.selectList(wrapper);

        //创建两个list集合，一个日期list，一个数量list
        List<String> date_calculatedList = new ArrayList<>();
        List<Integer> numDataList = new ArrayList<>();

        for (int i = 0; i <staList.size() ; i++) {
            StatisticsDaily daily = staList.get(i);
            date_calculatedList.add(daily.getDateCalculated());
            switch (type){
                case "login_num": numDataList.add(daily.getLoginNum());break;
                case "register_num": numDataList.add(daily.getRegisterNum());break;
                case "video_view_num": numDataList.add(daily.getVideoViewNum());break;
                case "course_num": numDataList.add(daily.getCourseNum());break;
                default:
                    break;
            }
        }
        Map<String,Object> map = new HashMap<>();
        map.put("date_calculatedList",date_calculatedList);
        map.put("numDataList",numDataList);
        return map;
    }
}
