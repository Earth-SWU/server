package me.hakyuwon.ecostep.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptOCR {

    public static String SECRET = "anF0a0J3bU9FWk9qSE1Kam95cUFVaXBITWRqdHhObUE=";
    public static String API_URL = "https://t4ftq5nimr.apigw.ntruss.com/custom/v1/39682/331be77e3e9afb78a4d72618b003940354bd698604ccdfa904d9d8736fe9097c/general";

    // 이미지에서 텍스트 추출
    public String analyzeReceipt(MultipartFile file) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        // Request JSON for the OCR API
        String requestId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();

        // Create the JSON body
        String requestJson = String.format(
                "{\"images\": [{\"format\": \"jpg\", \"name\": \"demo\"}], \"requestId\": \"%s\", \"version\": \"V2\", \"timestamp\": %d}",
                requestId, timestamp
        );

        // Set up the headers and body
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("X-OCR-SECRET", SECRET);

        HttpHeaders jsonPartHeaders = new HttpHeaders();
        jsonPartHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>(requestJson, jsonPartHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("message", jsonPart);
        body.add("file", file.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("OCR API call failed: " + response.getStatusCode());
        }
    }
}
