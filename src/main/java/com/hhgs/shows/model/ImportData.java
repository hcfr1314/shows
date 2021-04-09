package com.hhgs.shows.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ImportData {

   private int id;

   //实发有功功率
   private String realActivePower;

   //理论总有功
   private String theoreticalTotalPower;

   //理论发电功率
   private String theoreticalRealPower;

   //可用发电功率
   private String availablePower;

   //全厂有功给定值
   private String givenValueOfActivePower;

   //调度下发的有功设定值
   private String schedulerOfActivePower;

    //场站编号
    private int plantCode;

    //开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    //结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    //数据产生时间
    private Date timestampPower;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRealActivePower() {
        return realActivePower;
    }

    public void setRealActivePower(String realActivePower) {
        this.realActivePower = realActivePower;
    }

    public String getTheoreticalTotalPower() {
        return theoreticalTotalPower;
    }

    public void setTheoreticalTotalPower(String theoreticalTotalPower) {
        this.theoreticalTotalPower = theoreticalTotalPower;
    }

    public String getTheoreticalRealPower() {
        return theoreticalRealPower;
    }

    public void setTheoreticalRealPower(String theoreticalRealPower) {
        this.theoreticalRealPower = theoreticalRealPower;
    }

    public String getAvailablePower() {
        return availablePower;
    }

    public void setAvailablePower(String availablePower) {
        this.availablePower = availablePower;
    }

    public String getGivenValueOfActivePower() {
        return givenValueOfActivePower;
    }

    public void setGivenValueOfActivePower(String givenValueOfActivePower) {
        this.givenValueOfActivePower = givenValueOfActivePower;
    }

    public String getSchedulerOfActivePower() {
        return schedulerOfActivePower;
    }

    public void setSchedulerOfActivePower(String schedulerOfActivePower) {
        this.schedulerOfActivePower = schedulerOfActivePower;
    }

    public int getPlantCode() {
        return plantCode;
    }

    public void setPlantCode(int plantCode) {
        this.plantCode = plantCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getTimestampPower() {
        return timestampPower;
    }

    public void setTimestampPower(Date timestampPower) {
        this.timestampPower = timestampPower;
    }


}
