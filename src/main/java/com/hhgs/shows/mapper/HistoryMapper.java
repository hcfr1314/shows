package com.hhgs.shows.mapper;

import com.hhgs.shows.model.History;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryMapper {

    void batchInsert(@Param("list") List<History> list);

    List<History> queryByPlantCodeAndTime(@Param("start") String start, @Param("end") String end, @Param("plantCode") int plantCode);

    void deleteByTime(@Param("start") String start, @Param("end") String end,@Param("plantCode") int plantCode);

    void insertData(@Param("history") History history);
}
