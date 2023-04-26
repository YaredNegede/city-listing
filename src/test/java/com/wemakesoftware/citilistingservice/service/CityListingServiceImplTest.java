package com.wemakesoftware.citilistingservice.service;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.dto.PhotoDto;
import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.repository.CityListingRepository;
import com.wemakesoftware.citilistingservice.repository.PhotoListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CityListingServiceImplTest {

    private CityListingRepository cityRepository;

    private PhotoListingRepository photoListingRepository;

    private CityListingServiceImpl cityListingService ;

    @BeforeEach
    public void setup(){
        cityRepository = mock(CityListingRepository.class);
        photoListingRepository = mock(PhotoListingRepository.class);
        cityListingService = new CityListingServiceImpl(cityRepository, photoListingRepository);
    }

    @Test
    @Description("get all cities with the name, should return non null list of elements")
    void getCities() {

        List<City> data = List.of(
                new City(0l,"name"),
                new City(0l,"name1"),
                new City(0l,"name2"),
                new City(0l,"name3"),
                new City(0l,"name4"),
                new City(0l,"name5")
        );

        Page<City> res = new PageImpl<>(data);
        when(cityRepository.findAllByNameLike(anyString(),any())).thenReturn(res);
        Page<CityDto> citiesDto = cityListingService.getCities("New", PageRequest.of(1, 10));
        assertNotNull(citiesDto.getContent());
        assertEquals(data.size(),citiesDto.getSize());

        when(cityRepository.findAll(PageRequest.of(1, 10))).thenReturn(res);

        Page<CityDto> citiesDto2 = cityListingService.getCities(null, PageRequest.of(1, 10));
        assertNotNull(citiesDto2.getContent());
        assertEquals(data.size(),citiesDto2.getSize());
    }

    @Test
    @Description("this should not throw exception")
    void deleteCity() {

        cityListingService.deleteCity(1l);
    }

    @Test
    @Description("Should update city detail")
    void updateCityDetail() {

        CityDto city = CityDto
                .builder()
                .id(1l)
                .name("name")
                .build();

        Optional<City> rest = Optional.of(new City(1l,"dada"));

        when(cityRepository.save(any())).thenReturn(rest.get());
        when(cityRepository.findById(any())).thenReturn(rest);

        cityListingService.updateCityDetail(1l,city);

        verify(cityRepository, atLeastOnce()).findById(any());
        verify(cityRepository, atLeastOnce()).save(any());

    }

}