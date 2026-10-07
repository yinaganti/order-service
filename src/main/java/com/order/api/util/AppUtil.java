package com.order.api.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AppUtil {

    final RestTemplate restTemplate;

    public AppUtil(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T postForObject(String url, Object request, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-type", "application/json");
        HttpEntity<Object> entity = new HttpEntity<>(request, headers);
        return restTemplate.postForObject(url, entity, responseType);
    }

}
