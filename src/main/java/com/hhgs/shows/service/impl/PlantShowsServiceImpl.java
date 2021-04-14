package com.hhgs.shows.service.impl;

import com.hhgs.shows.mapper.HistoryMapper;
import com.hhgs.shows.mapper.PlantShowsMapper;
import com.hhgs.shows.model.BasePoints;
import com.hhgs.shows.model.DO.Dolphin.DolphinObject;
import com.hhgs.shows.model.History;
import com.hhgs.shows.model.ImportData;
import com.hhgs.shows.model.ResponseData;
import com.hhgs.shows.service.HistoryService;
import com.hhgs.shows.service.PlantShowsService;
import com.hhgs.shows.task.HistoryTask;
import com.hhgs.shows.util.DateUtil;
import com.hhgs.shows.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlantShowsServiceImpl implements PlantShowsService {

    @Autowired
    private PlantShowsMapper plantShowsMapper;

    @Autowired
    private HistoryMapper historyMapper;

    @Autowired
    private HistoryTask historyTask;

    private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private  DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public Map<String, String> batchInsert(List<ImportData> result,int plantCode) throws ParseException {

        HashMap<String, String> resultMap = new HashMap<>();

        if (result == null || result.size() == 0) {
            return null;
        }

        //先删除数据
        deleteData(result,plantCode);

        ListUtil<ImportData> listUtil = new ListUtil<>();
        List<List<ImportData>> listToSmList = listUtil.bgListToSmList(result, 1500);
        //插入数据
        int total = insertData(listToSmList);


        resultMap.put("插入条数", total + "");

        //如果上传多天的数据，使用此方式获取每一天的时间
        List<String> timesList = getTimeList(result);

        //如果只上传一天的数据，使用此方式获取数据对应的时间
        Date timestampPower = result.get(0).getTimestampPower();
        String time = DateUtil.getFormatTime(timestampPower);
        String times = time.split(" ")[0];

        historyTask.run(timesList,plantCode);

        return resultMap;
    }

    @Override
    public Map<String, Object> selectDataByCondition(ImportData importData) {
        List<ImportData> dataList = plantShowsMapper.getDataByCondition(importData);

        Map<String, Object> resultMap = new HashMap<>();

        //实发电量
        List<ResponseData> realActiveEletricEnergyList = new ArrayList<>();
        //全厂给定发电量
        List<ResponseData> availableEletricEnergyList = new ArrayList<>();
        //调度下发的电量
        List<ResponseData> schedulerOfActiveEletricEnergyList = new ArrayList<>();
        //理论发电功率
        List<ResponseData> theoreticalEletricEnergyList = new ArrayList<>();


        //取实发有功功率大于零的数据
        for (ImportData data : dataList) {
            if(data.getRealActivePower() != null) {
                BigDecimal value = new BigDecimal(data.getRealActivePower());
                ResponseData responseData = new ResponseData();
                //BigDecimal tempNum = new BigDecimal(60);
                //BigDecimal tmp = value.divide(tempNum, 10, BigDecimal.ROUND_CEILING);
                responseData.setVal(value);
                responseData.setTimeStamp(data.getTimestampPower());
                realActiveEletricEnergyList.add(responseData);
            }
        }

        //取全厂有功给定值大于零的数据
        for (ImportData data : dataList) {
            if(data.getAvailablePower() != null) {
                BigDecimal value = new BigDecimal(data.getAvailablePower());
                ResponseData responseData = new ResponseData();
                //BigDecimal tempNum = new BigDecimal(60);
                //BigDecimal tmp = value.divide(tempNum, 10, BigDecimal.ROUND_CEILING);
                responseData.setVal(value);
                responseData.setTimeStamp(data.getTimestampPower());
                availableEletricEnergyList.add(responseData);
            }
        }

        //取调度下发的有功设定值大于零的数据
        for (ImportData data : dataList) {
            if(data.getSchedulerOfActivePower() != null) {
            BigDecimal value = new BigDecimal(data.getSchedulerOfActivePower());
            ResponseData responseData = new ResponseData();
            //BigDecimal tempNum = new BigDecimal(60);
            //BigDecimal tmp = value.divide(tempNum, 10, BigDecimal.ROUND_CEILING);
            responseData.setVal(value);
            responseData.setTimeStamp(data.getTimestampPower());
            schedulerOfActiveEletricEnergyList.add(responseData);
            }
        }

        //取理论发电功率值大于零的数据
        for (ImportData data : dataList) {
            if(data.getTheoreticalRealPower() != null) {
                BigDecimal value = new BigDecimal(data.getTheoreticalRealPower());
                ResponseData responseData = new ResponseData();
                //BigDecimal tempNum = new BigDecimal(60);
                //BigDecimal tmp = value.divide(tempNum, 10, BigDecimal.ROUND_CEILING);
                responseData.setVal(value);
                responseData.setTimeStamp(data.getTimestampPower());
                theoreticalEletricEnergyList.add(responseData);
            }
        }

        resultMap.put("实发电量", realActiveEletricEnergyList);
        resultMap.put("可用发电量", availableEletricEnergyList);
        resultMap.put("调度下发的电量", schedulerOfActiveEletricEnergyList);
        resultMap.put("理论发电量", theoreticalEletricEnergyList);

        //添加每日
        String start = format.format(importData.getStartTime());
        String end = format.format(importData.getEndTime());
        List<History> historyList = historyMapper.queryByPlantCodeAndTime(start, end, importData.getPlantCode());
        resultMap.put("showData", historyList);
        return resultMap;
    }

    @Override
    public List<History> queryHistory(ImportData conditon) {

        List<History> histories = plantShowsMapper.queryLastDayData(conditon);
        return histories;
    }

    @Override
    public Map<Integer, List<BasePoints>> queryAllPoints() {
        List<BasePoints> basePoints = plantShowsMapper.queryAllPoints();
        Map<Integer, List<BasePoints>> collect = basePoints.stream().collect(Collectors.groupingBy(BasePoints::getPlantCode));
        return collect;
    }

    @Override
    public Map<Integer, List<DolphinObject>> queryAllObjects() {
        List<DolphinObject> dolphinObjects = plantShowsMapper.queryAllDolphinObject();
        Map<Integer, List<DolphinObject>> collect = dolphinObjects.stream().collect(Collectors.groupingBy(DolphinObject::getPlantCode));
        return collect;
    }

    @Transactional
    public void deleteData(List<ImportData> importDataList,int plantCode) throws ParseException {
        List<String> timeList = getTimeList(importDataList);

        for (String times : timeList) {
            String end = times + " 23:59:59";
            String start =times + " 00:00:00";
            plantShowsMapper.deleteByTime(start,end,plantCode);
        }
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

    private List<String> getTimeList(List<ImportData> importDataList) {
        //如果上传多天的数据，使用此方式获取每一天的时间
        List<String> timesList = new ArrayList<>();
        for (ImportData importData : importDataList) {
            Date timestampPower = importData.getTimestampPower();
            String time = DateUtil.getFormatTime(timestampPower);
            String times = time.split(" ")[0];
            if(!timesList.contains(times)) {
                timesList.add(times);
            }
        }
        return timesList;
    }


}
