package me.hakyuwon.ecostep.dto;

import lombok.Getter;
import lombok.Setter;

public class PredictDto {

    @Getter
    @Setter
    public static class PredictRequest {
        private String inputData;
    }

    @Getter
    @Setter
    public static class PredictResponse {
        private int missionId;
        private String missionName;
        private double carbonReduction;
        private double duration;
        private int score;
        private double adjustedScore;
        private double adjustedDuration;
        private double percentile;
        private String rankingMessage;
        private String ageGroup;
        private String region;
        private double avgCarbonReduction;
        private double avgScore;
        private String comparisonMessage;
    }
}
