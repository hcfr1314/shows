package com.hhgs.shows.service;

import com.hhgs.shows.model.History;
import com.hhgs.shows.model.ImportData;
import com.hhgs.shows.model.ResponseData;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface PlantShowsService {

    Map<String, String> batchInsert(List<ImportData> result,int plantCode) throws ParseException;

    Map<String,Object> selectDataByCondition(ImportData importData);
    List<History> queryHistory(ImportData conditon);

}
