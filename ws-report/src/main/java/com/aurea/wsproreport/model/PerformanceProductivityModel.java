package com.aurea.wsproreport.model;

import lombok.Getter;
import lombok.Setter;

public class PerformanceProductivityModel {

    @Getter
    @Setter
    private String icName;

    @Getter
    @Setter
    private String whichWeek;

    @Getter
    @Setter
    private String completedWeek;

    @Getter
    @Setter
    private String mail;

    @Getter
    @Setter
    private String role;

    @Getter
    @Setter
    private String tech;

    @Getter
    @Setter
    private String bootStart;

    @Getter
    @Setter
    private String currentTeam;

    @Getter
    @Setter
    private String sem;

    @Getter
    @Setter
    private String week1team;

    @Getter
    @Setter
    private int week1Score;

    @Getter
    @Setter
    private String week2team;

    @Getter
    @Setter
    private int week2Score;

    @Getter
    @Setter
    private String week3team;

    @Getter
    @Setter
    private int week3Score;

    @Getter
    @Setter
    private String week4team;

    @Getter
    @Setter
    private int week4Score;

    @Getter
    @Setter
    private double avgScore;
}
