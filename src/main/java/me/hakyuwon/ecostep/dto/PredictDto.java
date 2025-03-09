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
        private String prediction;
    }
}
