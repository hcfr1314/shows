package com.hhgs.shows.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class History {

    private int id;

    private int plantCode;

    private double realActivePower;

    //可用发电量
    private double availablePower;

    private double schedulerOfActivePower;

    //理论发电功率
    private double theoreticalRealPower;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date  createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

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

    public double getRealActivePower() {
        return realActivePower;
    }

    public void setRealActivePower(double realActivePower) {
        this.realActivePower = realActivePower;
    }


    public double getAvailablePower() {
        return availablePower;
    }

    public void setAvailablePower(double availablePower) {
        this.availablePower = availablePower;
    }

    public double getSchedulerOfActivePower() {
        return schedulerOfActivePower;
    }

    public void setSchedulerOfActivePower(double schedulerOfActivePower) {
        this.schedulerOfActivePower = schedulerOfActivePower;
    }

    public double getTheoreticalRealPower() {
        return theoreticalRealPower;
    }

    public void setTheoreticalRealPower(double theoreticalRealPower) {
        this.theoreticalRealPower = theoreticalRealPower;
    }
}
