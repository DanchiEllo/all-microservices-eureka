package com.example.weather.service;

import com.example.weather.model.Main;
import com.example.weather.model.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${appid}")
    private String appId;

    @Value("${url.weather}")
    private String urlWeather;



    @Cacheable(value = "weather", key = "{#lat, #lon}")
    public Main getWeather(String lat, String lon) {
        String request = String.format("%s?lat=%s&lon=%s&units=metric&appid=%s",
                urlWeather, lat, lon, appId);
        return Objects.requireNonNull(restTemplate.getForObject(request, Root.class)).getMain();
    }

}
