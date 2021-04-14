package com.hhgs.shows.model.DO.Dolphin;

/**
 * hbase数据对象和场站的关系
 */
public class DolphinObject {

    /**
     * 主键
     */
    private int id;

    /**
     * 场站编号
     */
    private int plantCode;

    /**
     * 场站名称
     */
    private String plantName;

    /**
     * 对应dolphin数据对象
     */
    private String dataObjectName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(int plantCode) {
        this.plantCode = plantCode;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getDataObjectName() {
        return dataObjectName;
    }

    public void setDataObjectName(String dataObjectName) {
        this.dataObjectName = dataObjectName;
    }

    @Override
    public String toString() {
        return "HbaseObject{" +
                "id=" + id +
                ", plantCode=" + plantCode +
                ", plantName='" + plantName + '\'' +
                ", dataObjectName='" + dataObjectName + '\'' +
                '}';
    }
}
