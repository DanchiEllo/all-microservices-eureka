package com.example.location.controller;

import com.example.location.model.Geodata;
import com.example.location.model.Weather;
import com.example.location.service.GeodataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class WeatherController {

    private final GeodataService geodataService;

    public WeatherController(GeodataService geodataService) {
        this.geodataService = geodataService;
    }

    @GetMapping("/weather")
    public Weather redirectRequestWeather(@RequestParam String location) {
        return geodataService.redirectRequestWeather(location);
    }

    @GetMapping("/all")
    public Iterable<Geodata> findAll() {
        return geodataService.findAll();
    }

    @GetMapping()
    public Optional<Geodata> getGeodata(@RequestParam String location) {
        return geodataService.findByName(location);
    }

    @PostMapping()
    public ResponseEntity<?> saveGeodata(@RequestBody Geodata geodata) {
        Optional<Geodata> savedGeodata = geodataService.save(geodata);
        if (savedGeodata.isPresent()) {
            return ResponseEntity.ok(savedGeodata.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Geodata with the specified name already exists");
        }
    }

    @PutMapping()
    public ResponseEntity<Geodata> updateGeodataByLocation(@RequestBody Geodata geodata) {
        return new ResponseEntity<>(geodataService.updateGeodataByLocation(geodata), HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteGeodataByLocation(@RequestParam String location) {
        boolean deleted = geodataService.deleteGeodataByLocation(location);
        if (deleted) {
            return ResponseEntity.ok("Object deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Object with the specified name does not exist");
        }
    }

}