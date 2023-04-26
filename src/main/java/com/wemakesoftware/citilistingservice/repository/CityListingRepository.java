package com.wemakesoftware.citilistingservice.repository;

import com.wemakesoftware.citilistingservice.model.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CityListingRepository extends CrudRepository<City, Long>, PagingAndSortingRepository<City, Long> {

    Page<City> findAllByNameLike(String name, Pageable pageable);
}
