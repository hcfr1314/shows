package com.hhgs.shows.service;

import com.hhgs.shows.model.History;

import java.util.List;
import java.util.Map;

public interface HistoryService {

    void batchInsert(List<History> list);

    void insertData(History history);

    Map<Integer, History> monthount(int plantCode);

    List<History> dayCount(String start, int plantCode);

    List<History> exportData(String startTime,String endTime,int plantCode);

    void deleteByTime( String start,  String end,int plantCode);
}
