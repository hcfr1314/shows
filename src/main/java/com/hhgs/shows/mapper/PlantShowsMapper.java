package com.hhgs.shows.mapper;

import com.hhgs.shows.model.History;
import com.hhgs.shows.model.ImportData;
import com.hhgs.shows.model.PlantCode;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PlantShowsMapper {


    //根据表名和条件查询场站数据
    List<ImportData> getAllDataWithPlantTableAndCondition(@Param("plantTable") String plantTable, @Param("importData") ImportData importData);

    //根据场站名称查询场站所属表名
    String getPlantTableWithPlantName(String plantName);

    int batchInsert(@Param("result") List<ImportData> result);

    /**
     * 获取所有场站名称列表
     * @return
     */
    List<PlantCode> getAllPlantName();

    /**
     * 条件查询
     * @param importData
     * @return
     */
    List<ImportData> getDataByCondition(@Param("importData") ImportData importData);

    /**
     * 获取所有的场站编号
     * @return
     */
    List<Integer> getAllPlantCode();

    List<History> queryLastDayData(@Param("con") ImportData conditon);

    Map<String, History> monthCount(int plantCode);

    /**
     * 根据场站编号查询场站名称
     * @param plantCode
     * @return
     */
    String getPlantNamebyPlantCode(@Param("plantCode") int plantCode);

    /**
     * 条件查询已有数据
     * @param importData
     * @return
     */
    ImportData getDataByTimeAndValue(@Param("importData") ImportData importData);

    /**
     * 根据id删除数据
     * @param id
     */
    void deleteById(@Param("id") int id);

    /**
     * 根据时间范围删除数据
     * @param start
     * @param end
     */
    void deleteByTime(@Param("start") String start, @Param("end") String end,@Param("plantCode") int plantCode);
}
