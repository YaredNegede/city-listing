package com.wemakesoftware.citilistingservice.controller;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.service.CityListingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cities")
@AllArgsConstructor
public class CityListingController {

    public final CityListingService cityService;


    @GetMapping
    public ResponseEntity<Page<CityDto>> getAllCities(@RequestParam(required = false) String name, Pageable pageable) {
        return new ResponseEntity<>(cityService.getCities(name, pageable), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable long id){
        cityService.deleteCity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCityDetail(@PathVariable long id, @RequestBody City newCity) {
        cityService.updateCityDetail(id,newCity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
