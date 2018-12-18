package com.aurea.wsproreport.model;

import lombok.Getter;
import lombok.Setter;

public class WeeklyComplianceModel {

    @Getter
    @Setter
    private String icName;

    @Getter
    @Setter
    private String mail;

    @Getter
    @Setter
    private int eightHrsPerDay = 0;

    @Getter
    @Setter
    private int deepWorkBlocks = 0;

    @Getter
    @Setter
    private int devTime = 0;

    @Getter
    @Setter
    private int dailyCic = 0;

    @Getter
    @Setter
    private int intensityFocus = 0;

    public void addSevenHrsPerDay(int eightHrsPerDay) {
        this.eightHrsPerDay += eightHrsPerDay;
    }

    public void addDeepWorkBlocks(int deepWorkBlocks) {
        this.deepWorkBlocks += deepWorkBlocks;
    }

    public void addDevTime(int devTime) {
        this.devTime += devTime;
    }

    public void addDailyCic(int dailyCic) {
        this.dailyCic += dailyCic;
    }

    public void addIntensityFocus(int intensityFocus) {
        this.intensityFocus += intensityFocus;
    }
}
