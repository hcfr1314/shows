package com.hhgs.shows.task;

import com.hhgs.shows.model.History;
import com.hhgs.shows.model.ImportData;
import com.hhgs.shows.service.HistoryService;
import com.hhgs.shows.service.PlantShowsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HistoryTask {

    private  DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private  PlantShowsService plantShowsService;

    @Autowired
    private  HistoryService historyService;

    //@Scheduled(cron = "0 0 0 * * ?")
    public  void run(List<String> timesList,int plantCode) throws ParseException {
        //构造查询日期
        LocalDate now = LocalDate.now();
        /*String end = format.format(now) + " 00:00:00";
        String start = format.format(now.plusDays(-1)) + " 00:00:00";*/
        for (String times : timesList) {
            String end = times + " 23:59:59";
            String start =times + " 00:00:00";
            String createTime = times + " 10:00:00";

            Date startDate = dateFormat.parse(start);
            Date endDate = dateFormat.parse(end);
            Date createDate = dateFormat.parse(createTime);
            ImportData condition = new ImportData();
            condition.setStartTime(startDate);
            condition.setEndTime(endDate);


            //构造查询条件（当天所有数据）
            List<History> list = plantShowsService.queryHistory(condition);
            for (History history : list) {
                history.setCreateTime(createDate);
            }
            historyService.deleteByTime(start,end,plantCode);
            //数据入库
            if (list !=null && !list.isEmpty()) {
                for (History history : list) {
                    if (history.getPlantCode()==plantCode) {
                        historyService.insertData(history);
                    }
                }
            }
        }

//        start="2020-05-01 00:00:00";
//        end="2020-05-20 00:00:00";


    }
}
