package com.order.api.config;

import com.order.api.client.ShipmentInterceptor;
import feign.RequestInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    @Bean
    public ModelMapper mapper() {
        return new ModelMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RequestInterceptor shipmentRequestInterceptor() {
        return new ShipmentInterceptor();
    }

}
