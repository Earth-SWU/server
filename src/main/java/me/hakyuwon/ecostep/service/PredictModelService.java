package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import me.hakyuwon.ecostep.dto.PredictDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PredictModelService {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String FASTAPI_URL = "http://43.200.154.213:8000/predict/"; // FastAPI 서버의 URL

    public boolean isMissionSuccessful(MultipartFile image) throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 이미지 파일을 전송할 MultiValueMap 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(image.getBytes()) {
            @Override
            public String getFilename() {
                return image.getOriginalFilename(); // 파일 이름 설정
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // FastAPI 서버에 POST 요청
        ResponseEntity<Map> response = restTemplate.postForEntity(FASTAPI_URL, requestEntity, Map.class);

        // JSON 응답 처리
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            String classification = (String) response.getBody().get("class");
            return "reusable".equals(classification); // reusable이면 성공
        }

        return false; // 실패 처리
    }
    /*
    public PredictDto.PredictResponse callPredictAPI(String inputData) {
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
        return responseEntity.getBody();
    } catch (
    RestClientException e) {
        // 예외 발생 시 로깅 후 적절한 응답 반환
        throw new RuntimeException("FastAPI 호출 중 오류 발생: " + e.getMessage(), e);
    }
    }*/
}
