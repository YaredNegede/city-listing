package com.wemakesoftware.citilistingservice.repository;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface CityListingRepository extends CrudRepository<City, Long> {

    Page<CityDto> findAllByName(String name, Pageable pageable);
}
