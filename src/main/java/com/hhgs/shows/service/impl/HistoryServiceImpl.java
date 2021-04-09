package com.hhgs.shows.service.impl;

import com.hhgs.shows.mapper.HistoryMapper;
import com.hhgs.shows.model.History;
import com.hhgs.shows.service.HistoryService;
import com.hhgs.shows.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.*;

@Service
public class HistoryServiceImpl implements HistoryService {

    @Autowired
    private HistoryMapper mapper;

    private static DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchInsert(List<History> list) {
        mapper.batchInsert(list);
    }

    @Override
    public void insertData(History history) {
        mapper.insertData(history);
    }

    @Override
    public Map<Integer, History> monthount(int plantCode) {
        LocalDate now=LocalDate.now();
        String end=formatter.format(now)+" 00:00:00";
        LocalDate firstDay=now.with(TemporalAdjusters.firstDayOfYear());
        String start=formatter.format(firstDay)+" 00:00:00";
        //构造查询条件
        List<History> list=mapper.queryByPlantCodeAndTime(start,end,plantCode);

        Map<Integer,List<History>> map=new HashMap<>();
        //处理数据
        if(!list.isEmpty()){
            for(History history : list){
                Date date=history.getCreateTime();
                int month=date.getMonth()+1;
                if(!map.containsKey(month)){
                    List<History> datas=new ArrayList<>();
                    datas.add(history);
                    map.put(month,datas);
                    continue;
                }

                map.get(month).add(history);

            }
        }

        Map<Integer,History> resultMap=new HashMap<>();

        map.forEach((month,historyList)->{
            if(!historyList.isEmpty()){
                History ele=new History();
                for(History his:historyList){
                    ele.setAvailablePower(ele.getAvailablePower()+his.getAvailablePower());
                    ele.setRealActivePower(ele.getRealActivePower()+his.getRealActivePower());
                    ele.setSchedulerOfActivePower(ele.getSchedulerOfActivePower()+his.getSchedulerOfActivePower());
                    ele.setTheoreticalRealPower(ele.getTheoreticalRealPower()+his.getTheoreticalRealPower());
                }
                resultMap.put(month,ele);
            }
        });


        //补全数据
        int month=now.getMonthValue();
        for(int i=1;i<=month;i++){
            if(!resultMap.containsKey(i)){
                resultMap.put(i,null);
            }
        }
        return resultMap;
    }

    @Override
    public List<History> dayCount(String start, int plantCode) {
        LocalDate localDate=LocalDate.parse(start).with(TemporalAdjusters.lastDayOfMonth());
        start=start+" 00:00:00";
        String end=formatter.format(localDate.plusDays(1))+" 00:00:00";
        System.out.println(end);
        return mapper.queryByPlantCodeAndTime(start,end,plantCode);
    }

    @Override
    public List<History> exportData(String startTime, String endTime, int plantCode) {
        List<History> histories = mapper.queryByPlantCodeAndTime(startTime, endTime, plantCode);

        return histories;
    }

    @Override
    public void deleteByTime(String start, String end,int plantCode) {
        mapper.deleteByTime(start,end,plantCode);
    }


}
