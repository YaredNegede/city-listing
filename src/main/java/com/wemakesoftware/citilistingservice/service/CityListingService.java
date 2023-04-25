package com.wemakesoftware.citilistingservice.service;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.repository.CityListingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@AllArgsConstructor
public class CityListingService {

    private final CityListingRepository cityRepository;

    public Page<CityDto> getCities(String name, Pageable pageable) {
        return  cityRepository.findAllByName(name,pageable);
    }

    public void deleteCity(long id) {
        cityRepository.deleteById(id);
    }

    public void updateCityDetail(long id, City newCity) {

        Optional<City> found = cityRepository.findById(id);

        found.ifPresent(city ->
        {
             if(newCity.getName() != null){
                 city.setName(newCity.getName());
             }
             if(newCity.getPhotoName() != null){
                 city.setPhotoName(newCity.getPhotoName());
             }

             if(newCity.getPhotoUrl() != null){
                 city.setPhotoUrl(newCity.getPhotoUrl());
             }

             cityRepository.save(newCity);

       });
    }
}
