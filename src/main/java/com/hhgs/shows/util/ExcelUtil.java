package com.hhgs.shows.util;


import com.hhgs.shows.model.History;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.apache.poi.ss.usermodel.*;

import org.apache.poi.ss.util.CellRangeAddress;

import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;

import org.apache.poi.xssf.usermodel.*;

import java.io.*;

import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.*;

public class ExcelUtil {


    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";
    private final static String CSV = "csv";

    private String filename;
    private XSSFSheetXMLHandler.SheetContentsHandler handler;

    public ExcelUtil(String filename) {
        this.filename = filename;
    }

    public ExcelUtil setHandler(XSSFSheetXMLHandler.SheetContentsHandler handler) {
        this.handler = handler;
        return this;
    }








    /**
     * 读取excel文件
     *
     * @param wb workbook
     * @param sheetIndex    sheet页下标：从0开始
     * @param startReadLine 开始读取的行:从0开始
     * @param tailLine      去除最后读取的行
     */
    public static void readExcel(Workbook wb, int sheetIndex, int startReadLine, int tailLine) {

        Sheet sheet = wb.getSheetAt(sheetIndex);
        Row row = null;

        for (int i = startReadLine; i < sheet.getLastRowNum() - tailLine + 1; i++) {

            row = sheet.getRow(i);
            for (Cell c : row) {
                c.setCellType(STRING);
                boolean isMerge = isMergedRegion(sheet, i, c.getColumnIndex());
                //判断是否具有合并单元格
                if (isMerge) {
                    String rs = getMergedRegionValue(sheet, row.getRowNum(), c.getColumnIndex());
                }
            }

        }

    }

    /**
     * 读取标准Excel文件
     *
     * @param file          文件
     * @param sheetIndex    sheet页下标：从0开始
     * @param startReadLine 开始读取的行:从0开始
     * @return 数据集合
     */
    public static List<String[]> readStandardExcel(File file, int sheetIndex, int startReadLine) {

        Workbook workbook = getWorkBook(file);
        List<String[]> list = new ArrayList<>();
        if (workbook != null) {

            Sheet sheet = workbook.getSheetAt(sheetIndex);

            int lastRowNum = sheet.getLastRowNum();
            for (int rowNum = startReadLine; rowNum <= lastRowNum; rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                int firstCellNum = row.getFirstCellNum();
                int lastCellNum = row.getLastCellNum();
                String[] cells = new String[row.getLastCellNum()];
                for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    cells[cellNum] = getCellValue(cell).trim();
                }

                list.add(cells);
            }
        }
        return list;

    }

    /**
     * 读取标准Excel文件
     *
     * @param file  需要读取的文件
     * @param sheetIndex  sheet索引  从0开始
     * @param startRow  从哪一行开始读取
     * @param endRow    从哪一行结束
     * @param startCell 从那一列开始读取
     * @param endCell   从那一列结束
     * @return  返回list<String[]>
     */
    public static List<String[]> readStandardExcel(File file, int sheetIndex, int startRow, int endRow, int startCell, int endCell) {
        Workbook workbook = getWorkBook(file);
        List<String[]> list = new ArrayList<>();

        if (workbook != null) {
            Sheet sheet = workbook.getSheetAt(sheetIndex);
            endRow=endRow==0?sheet.getLastRowNum():endRow;
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {

                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                String[] cells = new String[endCell];
                for (int cellNum = startCell; cellNum < endCell; cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    cells[cellNum] = getCellValue(cell).trim();
                }

                list.add(cells);

            }

        }
        return list;

    }

    /**
     * 读取Excel文档表头信息
     *
     * @param file  要读取的文件
     * @param sheetIndex  读取文件的sheet 索引
     * @return   返回List<String>
     */
    public static List<String> getExcelTitle(File file, int sheetIndex) {

        List<String> titles = new ArrayList<>();

        Workbook workbook = getWorkBook(file);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(0);
        int firstCellNum = row.getFirstCellNum();
        int lastCellNum = row.getLastCellNum();
        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            titles.add(getCellValue(cell).trim());
        }
        return titles;

    }

    /**
     * 指定行数进行读取Excel文档表头信息
     *
     * @param file  要读取的文件
     * @param sheetIndex  读取sheet 索引
     * @param rowNum  读取的行
     * @return  List<String>
     */
    public static List<String> getExcelTitleByRowNum(File file, int sheetIndex,int rowNum) {

        List<String> titles = new ArrayList<>();

        Workbook workbook = getWorkBook(file);
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Row row = sheet.getRow(rowNum);
        int firstCellNum = row.getFirstCellNum();
        int lastCellNum = row.getLastCellNum();
        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            titles.add(getCellValue(cell).trim());
        }
        return titles;

    }

    /**
     * 获取合并单元格的值
     *
     * @param sheet excel的sheet对象
     * @param row   合并的行
     * @param column  合并的列
     * @return  String
     */
    private static String getMergedRegionValue(Sheet sheet, int row, int column) {

        int sheetMergeCount = sheet.getNumMergedRegions();

        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();

            if (row >= firstRow && row <= lastRow) {

                if (column >= firstColumn && column <= lastColumn) {
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell);
                }
            }
        }

        return null;
    }

    /**
     * 判断合并了行
     *
     * @param sheet 合并的sheet
     * @param row   合并的行
     * @param column 合并的列
     * @return false/true
     */
    public static boolean isMergedRow(Sheet sheet, int row, int column) {

        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row == firstRow && row == lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断指定的单元格是否是合并单元格
     *
     * @param sheet  excel sheet索引
     * @param row    行下标
     * @param column 列下标
     * @return false/true
     */
    private static boolean isMergedRegion(Sheet sheet, int row, int column) {

        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {

            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if (row >= firstRow && row <= lastRow) {
                if (column >= firstColumn && column <= lastColumn) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断sheet页中是否含有合并单元格
     *
     * @param sheet 合并的sheet
     * @return true/false
     */
    private static boolean hasMerged(Sheet sheet) {
        return sheet.getNumMergedRegions() > 0;
    }

    /**
     * 合并单元格
     *
     * @param sheet sheet
     * @param firstRow 开始行
     * @param lastRow  结束行
     * @param firstCol 开始列
     * @param lastCol  结束列
     */
    private static void mergeRegion(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    /**
     * 获取单元格的值
     *
     * @param cell 单元格对象
     * @return string
     */
    private static String getCellValue(Cell cell) {

        if (cell == null) return "";
        // 把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellTypeEnum() == NUMERIC) {
            cell.setCellType(STRING);
        }
        if (cell.getCellTypeEnum() == STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellTypeEnum() == BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellTypeEnum() == FORMULA) {
            return cell.getCellFormula();
        } else if (cell.getCellTypeEnum() == BLANK) {
            return "NULL";
        }
        return "";
    }

    /**
     * 获取workboot
     *
     * @param file 文件
     * @return Workbook
     */
    private static Workbook getWorkBook(File file) {

        String fileName = file.getName();
        Workbook workbook = null;
        try {
            InputStream is = new FileInputStream(file);
            if (fileName.endsWith(XLS) ) {
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(XLSX) || fileName.endsWith(CSV)) {
                workbook = new XSSFWorkbook(is);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workbook;
    }

    /**
     * 创建新excel.
     *
     * @param fileDir   excel的路径
     * @param sheetName 要创建的表格页签名称也作为文件名称使用
     * @param titleRow  excel的第一行即表格头
     * @param data      表格数据
     * @return 文件路径
     * @throws Exception e
     */
    public static String createExcel(String fileDir, String sheetName, String[] titleRow, String[][] data) throws Exception {
        FileUtil.checkIsExist(fileDir);
        // 创建workbook
        Workbook workbook = new XSSFWorkbook();
        // 添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        Sheet sheet = workbook.createSheet(sheetName);
        // 新建文件
        String path = fileDir + "/" + sheetName + ".xlsx";
        try (FileOutputStream out = new FileOutputStream(path)) {
            // 添加表头
            Row row = workbook.getSheet(sheetName).createRow(0);//创建第一行
            for (short i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }
            // 设置文件数据
            if (data != null && data.length > 0) {

                for (int i = 1; i <= data.length; i++) {
                    // 创建行
                    Row dataRow = sheet.createRow(i);
                    // 获取数据
                    String[] datas = data[i - 1];
                    for (int j = 0; j < datas.length; j++) {

                        Cell dataCell = dataRow.createCell(j);
                        dataCell.setCellValue(datas[j]);
                    }
                }
            }
            // 写入数据文件
            workbook.write(out);

            return path;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String createExcel(String fileDir, String sheetName, String[] titleRow, List<History> historyList) throws Exception {
        FileUtil.checkIsExist(fileDir);
        // 创建workbook
        Workbook workbook = new XSSFWorkbook();
        // 添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        Sheet sheet = workbook.createSheet(sheetName);
        // 新建文件
        String path = fileDir + "/" + sheetName + ".xlsx";
        try (FileOutputStream out = new FileOutputStream(path)) {
            // 添加表头
            Row row = workbook.getSheet(sheetName).createRow(0);//创建第一行
            for (short i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }
            // 设置文件数据
            if (historyList != null && historyList.size() > 0) {

                for (int i = 1; i <= historyList.size(); i++) {
                    // 创建行
                    Row dataRow = sheet.createRow(i);
                    // 设置数据
                    dataRow.createCell(0).setCellValue(historyList.get(i-1).getPlantCode());
                    dataRow.createCell(1).setCellValue(historyList.get(i-1).getRealActivePower());
                    dataRow.createCell(2).setCellValue(historyList.get(i-1).getAvailablePower());
                    dataRow.createCell(3).setCellValue(historyList.get(i-1).getSchedulerOfActivePower());
                    dataRow.createCell(4).setCellValue(historyList.get(i-1).getTheoreticalRealPower());
                    dataRow.createCell(5).setCellValue(historyList.get(i-1).getAvailablePower()-historyList.get(i-1).getRealActivePower());
                    dataRow.createCell(6).setCellValue(historyList.get(i-1).getTheoreticalRealPower()-historyList.get(i-1).getAvailablePower());
                    dataRow.createCell(7).setCellValue(DateUtil.getFormatTime(historyList.get(i-1).getCreateTime()));
                }
            }
            // 写入数据文件
            workbook.write(out);

            return path;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 创建合并单元格数据
     * @param fileDir 文件路径
     * @param sheetName sheet名称
     * @param titleRow 标题
     * @param data     业务数据
     * @param regionList  “”
     * @return  文件路径
     * @throws Exception io异常
     */
    public static String createRegionExcel(String fileDir, String sheetName, String[] titleRow, String[][] data, List<CellRangeAddress> regionList) throws Exception {

        // 创建workbook
        Workbook workbook = new XSSFWorkbook();
        // 添加Worksheet（不添加sheet时生成的xls文件打开时会报错)
        Sheet sheet = workbook.createSheet(sheetName);
        // 新建文件
        String path = fileDir + "/" + sheetName + ".xlsx";
        try (FileOutputStream out = new FileOutputStream(path)) {
            // 添加表头
            Row row = workbook.getSheet(sheetName).createRow(0);//创建第一行
            for (short i = 0; i < titleRow.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(titleRow[i]);
            }
            // 设置文件数据
            if (data != null && data.length > 0) {

                for (int i = 1; i <= data.length; i++) {
                    // 创建行
                    Row dataRow = sheet.createRow(i);
                    // 获取数据
                    String[] datas = data[i - 1];
                    for (int j = 0; j < datas.length; j++) {

                        Cell dataCell = dataRow.createCell(j);
                        dataCell.setCellValue(datas[j]);
                    }
                }
            }

            for(CellRangeAddress region : regionList){
                sheet.addMergedRegion(region);
            }
            // 写入数据文件
            workbook.write(out);
            return path;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
