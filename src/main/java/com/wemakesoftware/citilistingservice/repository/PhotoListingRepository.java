package com.wemakesoftware.citilistingservice.repository;

import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface PhotoListingRepository extends CrudRepository<Photo, Long>, PagingAndSortingRepository<Photo, Long> {

    Page<Photo> findAllByPhotoNameLike(String name, Pageable pageable);

    Optional<Photo> findByCity(City city);

    Page<Photo> findAllByCity(City city, Pageable pageable);
}
