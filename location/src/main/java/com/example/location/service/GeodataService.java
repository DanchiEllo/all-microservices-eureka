package com.example.location.service;

import com.example.location.model.Geodata;
import com.example.location.model.Weather;
import com.example.location.repository.GeodataRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;


@Service
public class GeodataService {

    private final RestTemplate restTemplate;

    private final GeodataRepository repository;

    public GeodataService(RestTemplate restTemplate, GeodataRepository repository) {
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    public Weather redirectRequestWeather(String location) {
        Optional<Geodata> geoOptional = repository.findByName(location);
        if (geoOptional.isPresent()) {
            Geodata geodata = geoOptional.get();
            String url = String.format("http://localhost:8082/?lat=%s&lon=%s", geodata.getLat(), geodata.getLon());
            return restTemplate.getForObject(url, Weather.class);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "location not found");
    }

    public Iterable<Geodata> findAll() {
        return repository.findAll();
    }

    public Optional<Geodata> findByName(String location) {
        return repository.findByName(location);
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
