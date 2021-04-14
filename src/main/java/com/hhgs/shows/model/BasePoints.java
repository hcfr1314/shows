package com.hhgs.shows.model;

import java.math.BigInteger;

public class BasePoints {

    //原始测点id
    private long orgId;
    //测点描述
    private String description;
    //测点类型
    private int orgType;
    //场站编码
    private int plantCode;

    public long getOrgId() {
        return orgId;
    }

    public void setOrgId(long orgId) {
        this.orgId = orgId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getOrgType() {
        return orgType;
    }

    public void setOrgType(int orgType) {
        this.orgType = orgType;
    }

    public int getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(int plantCode) {
        this.plantCode = plantCode;
    }

    @Override
    public String toString() {
        return "BasePoints{" +
                "orgId=" + orgId +
                ", description='" + description + '\'' +
                ", orgType=" + orgType +
                ", plantCode=" + plantCode +
                '}';
    }
}
