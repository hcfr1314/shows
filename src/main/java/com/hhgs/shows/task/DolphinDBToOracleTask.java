package com.hhgs.shows.task;


import com.hhgs.shows.mapper.PlantShowsMapper;
import com.hhgs.shows.model.BO.DolphinDBResult;
import com.hhgs.shows.model.BasePoints;
import com.hhgs.shows.model.DO.Dolphin.DolphinObject;
import com.hhgs.shows.model.ImportData;
import com.hhgs.shows.service.PlantShowsService;
import com.hhgs.shows.util.DateUtil;
import com.hhgs.shows.util.ListUtil;
import com.hhgs.shows.util.SelectDataFromDolphindb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class DolphinDBToOracleTask {

    @Autowired
    private PlantShowsService plantShowsService;

    private Map<Integer, List<BasePoints>> pointsListMap;

    private Map<Integer,List<DolphinObject>> objectListMap;

    @Autowired
    private PlantShowsMapper plantShowsMapper;

    private  ListUtil<ImportData> listUtil;

    @PostConstruct
    public void init() {

      //查询所有基础测点数据
        pointsListMap = plantShowsService.queryAllPoints();
      //查询所有场站dolphindb数据对象
        objectListMap = plantShowsService.queryAllObjects();

        listUtil = new ListUtil<>();

    }

    //@Scheduled(cron = "0 0 0 * * ?")
    public void run() {
        //构造开始时间和结束时间
        String beforeOneDayTime = DateUtil.getBeforeOneDayTime();
        String startTimeTemp = beforeOneDayTime +" 00:00:00";
        String endTimeTemp = beforeOneDayTime +" 23:59:59";

        long startTime = DateUtil.getTime(startTimeTemp);
        long endTime = DateUtil.getTime(endTimeTemp);

        //将要被存储的集合数据
        List<ImportData> insertDataList = new ArrayList<>();

        pointsListMap.forEach((plantCode,pointsList)->{
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
                if(point.getOrgType() == 5) {
                    DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                    realActivePowerList = dolphinDBResult.getData().getDataRows();
                }
                if(point.getOrgType() == 6) {
                    DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                    theoreticalRealPowerList = dolphinDBResult.getData().getDataRows();
                }
                if(point.getOrgType() == 7) {
                    DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                    availablePowerList = dolphinDBResult.getData().getDataRows();
                }
                if(point.getOrgType() == 8) {
                    DolphinDBResult dolphinDBResult = SelectDataFromDolphindb.getDataFromInfluxdb(dataObjectName, startTime, endTime, point.getOrgId());
                    schedulerOfActivPowerList = dolphinDBResult.getData().getDataRows();
                }

            }

            for (int i = 0; i < realActivePowerList.size(); i++) {
                ImportData importData = new ImportData();
                importData.setRealActivePower(realActivePowerList.get(i)[2]);
                importData.setTheoreticalRealPower(theoreticalRealPowerList.get(i)[2]);
                importData.setAvailablePower(availablePowerList.get(i)[2]);
                importData.setSchedulerOfActivePower(schedulerOfActivPowerList.get(i)[2]);
                importData.setTimestampPower(DateUtil.stringToDate(realActivePowerList.get(i)[1].split(".")[0]));
                insertDataList.add(importData);
            }

            List<List<ImportData>> smList = listUtil.bgListToSmList(insertDataList, 1500);
            //插入数据
            insertData(smList);
            //清空集合数据
            insertDataList.clear();
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

}
