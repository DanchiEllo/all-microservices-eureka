package com.example.location.service;

import com.example.location.model.Geodata;
import com.example.location.model.Weather;
import com.example.location.repository.GeodataRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.Optional;


@Service
public class GeodataService {

    private final GeodataRepository repository;
    private final WebClient webClient;

    public GeodataService(GeodataRepository repository, WebClient.Builder webClientBuilder) {
        this.repository = repository;
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public Iterable<Geodata> findAll() {
        return repository.findAll();
    }

    public Optional<Geodata> findByName(String location) {
        return repository.findByName(location);
    }

    public Mono<Weather> redirectRequestWeather(String location) {
        Optional<Geodata> geodata = findByName(location);
        if (geodata.isPresent()) {
            String url = String.format("/?lat=%s&lon=%s", geodata.get().getLat(), geodata.get().getLon());
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(Weather.class);
        }
        else {
            return null;
        }
    }

    public Optional<Geodata> save(Geodata geodata) {
        if (repository.existsByName(geodata.getName())) {
            return Optional.empty();
        }
        return Optional.of(repository.save(geodata));
    }

    public Geodata updateGeodataByLocation(Geodata geodata) {
        return repository.findByName(geodata.getName())
                .filter(existingGeodata -> existingGeodata != geodata)
                .map(existingGeodata -> {
                    existingGeodata.setLon(geodata.getLon());
                    existingGeodata.setLat(geodata.getLat());
                    return repository.save(existingGeodata);
                })
                .orElse(null);
    }

    @Transactional
    public boolean deleteGeodataByLocation(String location) {
        if (repository.existsByName(location)) {
            repository.deleteByName(location);
            return true;
        } else {
            return false;
        }
    }

}
