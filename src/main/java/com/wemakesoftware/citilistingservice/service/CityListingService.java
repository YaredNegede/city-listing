package com.wemakesoftware.citilistingservice.service;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.dto.PhotoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CityListingService {

    Page<CityDto> getCities(String name, Pageable pageable);

    void deleteCity(long id);

    void updateCityDetail(long id, PhotoDto newCity);

    void updateCityDetail(long id, CityDto cityDto);

    Page<PhotoDto> getPhotos(String named, Pageable pageable) throws Exception;

    Page<PhotoDto> getPhotos(long id, Pageable pageable) throws Exception;

    void save(CityDto cityDto);

    CityDto get(long id) throws Exception;
}
