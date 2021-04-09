package com.hhgs.shows.controller;
import com.hhgs.shows.common.FileTypeEnum;
import com.hhgs.shows.common.MessageConstant;
import com.hhgs.shows.mapper.PlantShowsMapper;
import com.hhgs.shows.model.History;
import com.hhgs.shows.model.ImportData;
import com.hhgs.shows.model.PlantCode;
import com.hhgs.shows.model.ResponseData;
import com.hhgs.shows.service.HistoryService;
import com.hhgs.shows.service.PlantShowsService;
import com.hhgs.shows.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api("场站信息展示")
@RestController
@RequestMapping("/plant")
@CrossOrigin
public class PlantController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(PlantController.class);

    @Autowired
    private PlantShowsMapper plantShowsMapper;

    @Autowired
    private PlantShowsService plantShowsService;

    @Autowired
    private HistoryService historyService;

    @ApiOperation( notes = "根据场站名称查询场站数据",value = "根据场站名称查询场站数据")
    @PostMapping("getPlantData")
    public BaseResponse getPlantData(@ApiParam(required = true,value = "importData") @RequestBody(required = true) ImportData importData) {
        try {
            Map<String,Object> resultMap = plantShowsService.selectDataByCondition(importData);

            return BaseResponse.initSuccessBaseResponse(resultMap);
        } catch (Exception e) {
            logger.error("PlantController--getPlantData--e"+e.getMessage());
            e.printStackTrace();
            return BaseResponse.initError(null,e.getMessage());
        }
    }


    /**
     * 经本机测试：一万五千数据导入时间为6.34s
     * @param file 文件
     * @return CommonResponse
     */
    @CrossOrigin
    @ApiOperation(notes = "导入场站数据数据", value = "导入场站数据数据")
    @PostMapping(value = "importFile")
    public BaseResponse importFile(@RequestParam("file") MultipartFile file) {

        String fileName = file.getOriginalFilename();
        int plantCode = Integer.parseInt(fileName.split("-")[1].split("\\.")[0]);

        List<Integer> allPlantcCode = plantShowsMapper.getAllPlantCode();
        if(!allPlantcCode.contains(plantCode)) {
            return BaseResponse.initError(null, "场站不存在");
        }

        String timestamp = File.separator + new Date().getTime() + File.separator;
        //String base = System.getProperty("user.dir")+ file_storage_path_upload +timestamp;
        String base = file_storage_path_upload +timestamp;
        try {

            File test = new File(base);
            if (!test.exists()) {
                test.mkdirs();
            }
            File dest = new File(base + fileName);
            file.transferTo(dest);
            List<ImportData> importDataList = CsvUtils.readFile(base+fileName, plantCode);
            Map<String, String> stringIntegerMap = plantShowsService.batchInsert(importDataList,plantCode);

            //插入完成，删除文件
            if(test.isDirectory()){
                File[] files = test.listFiles();
                if(files.length>0){
                    for(File tmp : files){
                        tmp.delete();
                    }
                }
            }
            //删除文件夹
            test.delete();

            return BaseResponse.initSuccessBaseResponse(stringIntegerMap);
        } catch (Exception e) {
            logger.error("PlantController--importFile--e " + e.getMessage());
            e.printStackTrace();
            return BaseResponse.initError(null, e.getMessage());
        }
    }

    /**
     * 将excel文件中的内容封装到数组中
     * @param array
     * @param plantCode
     * @return
     * @throws Exception
     */
    private ImportData createKksDicData(String[] array,String plantCode) throws Exception {
        if (array.length < 6) {
            throw new Exception(MessageConstant.ARRAY_LENGTH_ANOMALY);
        }
        ImportData importData = new ImportData();

            importData.setRealActivePower(array[0]);
            importData.setTheoreticalTotalPower(array[1]=array[1].equals("NULL")?null:array[1]);
            importData.setTheoreticalRealPower(array[2]=array[2].equals("NULL")?null:array[2]);
            importData.setAvailablePower(array[3]);
            importData.setGivenValueOfActivePower(array[4]);
            importData.setSchedulerOfActivePower(array[5]);
            importData.setPlantCode(Integer.parseInt(plantCode));
            importData.setTimestampPower(DateUtil.getDateTime(array[7]));
        return importData;
    }

    /**
     * 下载数据
     * @param request
     * @param response
     * @return
     */
    @CrossOrigin
    @ResponseBody
    @ApiOperation(notes = "下载某个时间段的数据",value = "下载某个时间段的数据")
    @PostMapping(value = "download")
    public ResponseEntity<byte[]> downloadData(@RequestParam("plantCode") int plantCode,@RequestParam("startTime") String startTime,@RequestParam("endTime") String endTime,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<History> histories = historyService.exportData(startTime, endTime, plantCode);

        File test = null;
        try {
            String timestamp = File.separator + new Date().getTime() + File.separator;
            String base = file_storage_path_download +timestamp;
             test = new File(base);
            if (!test.exists()) {
                test.mkdirs();
            }
            String[] title = {"场站编号","实发发电量","可用发电量","下发发电量","理论发电量","限电电量","故障损失电量","时间"};
            String plantName = plantShowsMapper.getPlantNamebyPlantCode(plantCode);
            String filePath = ExcelUtil.createExcel(base, plantName, title, histories);
            return downLoad(response,request,filePath, FileTypeEnum.XLS);
        } catch (Exception e) {
            logger.error("PlantController--downloadData--e " + e.getMessage());
            e.printStackTrace();
            return null;
        }finally {
            //插入完成，删除文件
            if(test.isDirectory()){
                File[] files = test.listFiles();
                if(files.length>0){
                    for(File tmp : files){
                        tmp.delete();
                    }
                }
            }
            //删除文件夹
            test.delete();

        }

    }

    @CrossOrigin
    @ApiOperation(notes = "获取场站列表", value = "获取场站列表")
    @GetMapping("/getAllPlantNameAndCode")
    public BaseResponse getAllPlantNameAndCode() {
        try {
            List<PlantCode> plantNames = plantShowsMapper.getAllPlantName();
            return BaseResponse.initSuccessBaseResponse(plantNames);
        } catch (Exception e) {
            logger.error("PlantController--getAllPlantNameAndCode--e " + e.getMessage());
            e.printStackTrace();
            return BaseResponse.initError(null, e.getMessage());
        }
    }


    @CrossOrigin
    @ApiOperation(notes = "月份魂宗", value = "月份魂宗")
    @GetMapping("/month")
    public BaseResponse getAllPlantNameAndCode(@RequestParam("plantCode") int plantCode) {
        try {
            Map<Integer, History> result = historyService.monthount(plantCode);
            return BaseResponse.initSuccessBaseResponse(result);
        } catch (Exception e) {
            logger.error("PlantController--getAllPlantNameAndCode--e " + e.getMessage());
            e.printStackTrace();
            return BaseResponse.initError(null, e.getMessage());
        }
    }

    @CrossOrigin
    @ApiOperation(notes = "月份魂宗", value = "月份魂宗")
    @GetMapping("/day")
    public BaseResponse dayCount(@RequestParam("plantCode") int plantCode,@RequestParam("start") String start){
        try {
            List<History> result = historyService.dayCount(start,plantCode);
            return BaseResponse.initSuccessBaseResponse(result);
        } catch (Exception e) {
            logger.error("PlantController--getAllPlantNameAndCode--e " + e.getMessage());
            e.printStackTrace();
            return BaseResponse.initError(null, e.getMessage());
        }
    }




}
