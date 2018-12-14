package ozcan.cagirici.innovation.model;

import lombok.Getter;
import lombok.Setter;

public class DailyReportModel {
    @Getter
    @Setter
    private String icName;

    @Getter
    @Setter
    private String mail;

    @Getter
    @Setter
    private String sevenHrsPerDay;

    @Getter
    @Setter
    private String deepWorkBlocks;

    @Getter
    @Setter
    private String devTime;

    @Getter
    @Setter
    private String dailyCic;

    @Getter
    @Setter
    private String intensityFocus;

    @Getter
    @Setter
    private int focusScore = 0;

    @Getter
    @Setter
    private int intensityScore = 0;

    @Getter
    @Setter
    private int devTimePercentage = 0;

}
