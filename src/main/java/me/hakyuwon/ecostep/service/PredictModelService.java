package me.hakyuwon.ecostep.service;

import me.hakyuwon.ecostep.dto.PredictDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

@Service
public class PredictModelService {
    @Autowired
    private RestTemplate restTemplate;

    private static final String FASTAPI_URL = "http://172.19.6.88:8000/predict/"; // FastAPI 서버의 URL

    public PredictModelService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String callPredictAPI(String inputData) {
        // 요청 객체 설정
        try {
        PredictDto.PredictRequest request = new PredictDto.PredictRequest();
        request.setInputData(inputData);

        // FastAPI 서버로 요청을 보내는 HTTP 엔티티 생성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<PredictDto.PredictRequest> entity = new HttpEntity<>(request, headers);

        // FastAPI 서버 호출
        ResponseEntity<PredictDto.PredictResponse> responseEntity = restTemplate.exchange(
                FASTAPI_URL, HttpMethod.POST, entity, PredictDto.PredictResponse.class);

        // 응답 데이터 반환
        return responseEntity.getBody().getPrediction();
    } catch (
    RestClientException e) {
        // 예외 발생 시 로깅 후 적절한 응답 반환
        throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage(), e);
    }
    }
}
