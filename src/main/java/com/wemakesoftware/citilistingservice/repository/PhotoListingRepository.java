package com.wemakesoftware.citilistingservice.repository;

import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PhotoListingRepository extends CrudRepository<Photo, Long> {

    Page<Photo> findAllByPhotoNameLike(String name, Pageable pageable);

    Optional<Photo> findByCity(City city);
}
