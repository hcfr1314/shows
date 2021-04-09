package com.hhgs.shows.util;

import com.hhgs.shows.model.ImportData;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取csv文件并解析入库
 */
public class CsvUtils {

    public static List<ImportData> readFile(String url, int plantCode) {
        List<ImportData> result = new ArrayList<>();
        //url = System.getProperty("user.dir") + "/upload/5.5晨阳.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(url))) {
            String line = br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().length() > 0) {
                    String[] arr = line.split(",");
                    ImportData data = new ImportData();
                    data.setTimestampPower(DateUtil.getDateTime(arr[0]));
                    data.setRealActivePower(arr[1]);
                    data.setAvailablePower(arr[2]);
                    data.setSchedulerOfActivePower(arr[3]);
                    data.setTheoreticalRealPower(arr[4]);
                    data.setPlantCode(plantCode);
                    result.add(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
