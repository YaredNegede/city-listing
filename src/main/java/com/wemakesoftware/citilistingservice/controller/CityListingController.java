package com.wemakesoftware.citilistingservice.controller;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.dto.PhotoDto;
import com.wemakesoftware.citilistingservice.service.CityListingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(Paths.root_city)
@AllArgsConstructor
@Slf4j
public class CityListingController {

    public final CityListingService cityService;

    @GetMapping
    public ResponseEntity<Page<CityDto>> getAllCities(
            @RequestParam(required = false) String name,
            @RequestParam(required=true,defaultValue="0")  Integer currentPage,
            @RequestParam(required=true,defaultValue="10") Integer size) {

        return new ResponseEntity<>(cityService.getCities(name, PageRequest.of(currentPage,size)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable long id){
        cityService.deleteCity(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateCity(@PathVariable long id, @RequestBody CityDto newCity) {
        cityService.updateCityDetail(id,newCity);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/photo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateCity(@PathVariable long id, @RequestBody PhotoDto photoDto) {
        cityService.updateCityDetail(id,photoDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/{name}/photo")
    public ResponseEntity<Page<PhotoDto>> getPhotos(@PathVariable(required=false) String name,
                                          @RequestParam(required=false,defaultValue="0")  Integer currentPage,
                                          @RequestParam(required=false,defaultValue="10") Integer size) throws Exception {
        return  ResponseEntity.ok(cityService.getPhotos(name,PageRequest.of(currentPage,size)));
    }

    @GetMapping(value = "/{id}/city-photos")
    public ResponseEntity<Page<PhotoDto>> getPhotos(@PathVariable int id,
                                          @RequestParam(required=false,defaultValue="0")  Integer currentPage,
                                          @RequestParam(required=false,defaultValue="10") Integer size) throws Exception {
        return  ResponseEntity.ok(cityService.getPhotos(id,PageRequest.of(currentPage,size)));
    }

}
