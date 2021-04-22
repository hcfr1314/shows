package com.hhgs.shows.task;


import com.hhgs.shows.mapper.PlantShowsMapper;
import com.hhgs.shows.model.BO.DolphinDBResult;
import com.hhgs.shows.model.BasePoints;
import com.hhgs.shows.model.DO.Dolphin.DolphinObject;
import com.hhgs.shows.model.History;
import com.hhgs.shows.model.ImportData;
import com.hhgs.shows.service.HistoryService;
import com.hhgs.shows.service.PlantShowsService;
import com.hhgs.shows.util.DateUtil;
import com.hhgs.shows.util.ListUtil;
import com.hhgs.shows.util.SelectDataFromDolphindb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Component
public class DolphinDBToOracleTask {

    @Autowired
    private PlantShowsService plantShowsService;

    private Map<Integer, List<BasePoints>> pointsListMap;

    private Map<Integer, List<DolphinObject>> objectListMap;

    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private PlantShowsMapper plantShowsMapper;

    @Autowired
    private HistoryService historyService;

    private ListUtil<ImportData> listUtil;

    @PostConstruct
    public void init() {
        //查询所有基础测点数据
        pointsListMap = plantShowsService.queryAllPoints();
        //查询所有场站dolphindb数据对象
        objectListMap = plantShowsService.queryAllObjects();

        listUtil = new ListUtil<>();
    }

    @Scheduled(cron = "0 0 1 * * ? ")
    //@Scheduled(fixedRate = 36000000L)
    public void run() {
        //构造开始时间和结束时间
        String beforeOneDayTime = DateUtil.getBeforeOneDayTime();
        String startTimeTemp = beforeOneDayTime + " 00:00:00";
        String endTimeTemp = beforeOneDayTime + " 23:59:59";

        long startTime = DateUtil.getTime(startTimeTemp);
        long endTime = DateUtil.getTime(endTimeTemp);

        //将要被存储的集合数据
        List<ImportData> insertDataList = new ArrayList<>();

        pointsListMap.forEach((plantCode, pointsList) -> {

            if (objectListMap.get(plantCode) != null) {
                System.out.println("plantCode: " + plantCode + "=============================");
                String dataObjectName = objectListMap.get(plantCode).get(0).getDataObjectName();

                //实发有功功率列表
                List<String[]> realActivePowerList = null;

                //理论发电功率列表
                List<String[]> theoreticalRealPowerList = null;

                //可用发电功率列表
                List<String[]> availablePowerList = null;

                //调度下发的有功设定值列表
                List<String[]> schedulerOfActivPowerList = null;

                for (BasePoints point : pointsList) {
                    if (point.getOrgType() == 5) {
                        DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                        if (dolphinDBResult != null && dolphinDBResult.getData() != null) {
                            realActivePowerList = dolphinDBResult.getData().getDataRows();
                        }
                    }
                    if (point.getOrgType() == 6) {
                        DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                        if (dolphinDBResult != null && dolphinDBResult.getData() != null) {
                            theoreticalRealPowerList = dolphinDBResult.getData().getDataRows();
                        }
                    }
                    if (point.getOrgType() == 7) {
                        DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                        if (dolphinDBResult != null && dolphinDBResult.getData() != null) {
                            availablePowerList = dolphinDBResult.getData().getDataRows();
                        }
                    }
                    if (point.getOrgType() == 8) {
                        DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                        if (dolphinDBResult != null && dolphinDBResult.getData() != null) {
                            schedulerOfActivPowerList = dolphinDBResult.getData().getDataRows();
                        }
                    }
                }

                if (realActivePowerList != null) {
                    for (int i = 0; i < realActivePowerList.size(); i++) {
                        ImportData importData = new ImportData();
                        importData.setRealActivePower(realActivePowerList == null ? null : realActivePowerList.get(i)[2]);
                        importData.setTheoreticalRealPower(theoreticalRealPowerList == null ? null : theoreticalRealPowerList.get(i)[2]);
                        importData.setAvailablePower(availablePowerList == null ? null : availablePowerList.get(i)[2]);
                        importData.setSchedulerOfActivePower(schedulerOfActivPowerList == null ? null : schedulerOfActivPowerList.get(i)[2]);
                        importData.setTimestampPower(DateUtil.stringToDate(realActivePowerList == null ? null : realActivePowerList.get(i)[1].split("\\.")[0]));
                        importData.setPlantCode(plantCode);
                        insertDataList.add(importData);
                    }
                }
            }

            List<List<ImportData>> smList = listUtil.bgListToSmList(insertDataList, 1500);
            //插入数据
            insertData(smList);
            //清空集合数据
            insertDataList.clear();

            //统计各个指标一整天的积分值并入库
            run(beforeOneDayTime, plantCode);
        });
    }

    @Transactional
    public int insertData(List<List<ImportData>> listToSmList) {
        //插入总记录数
        int total = 0;
        for (List<ImportData> importDataList : listToSmList) {
            total += plantShowsMapper.batchInsert(importDataList);
        }
        return total;
    }

    private void run(String time, int plantCode) {
        //构造查询日期
        String end = time + " 23:59:59";
        String start = time + " 00:00:00";
        String createTime = time + " 10:00:00";

        try {
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
            historyService.deleteByTime(start, end, plantCode);
            //数据入库
            if (list != null && !list.isEmpty()) {
                for (History history : list) {
                    if (history.getPlantCode() == plantCode) {
                        historyService.insertData(history);
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
