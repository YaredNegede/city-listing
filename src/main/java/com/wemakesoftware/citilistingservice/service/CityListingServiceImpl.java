package com.wemakesoftware.citilistingservice.service;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.dto.PhotoDto;
import com.wemakesoftware.citilistingservice.mappers.CityMapper;
import com.wemakesoftware.citilistingservice.mappers.CityMapperImpl;
import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.model.Photo;
import com.wemakesoftware.citilistingservice.repository.CityListingRepository;
import com.wemakesoftware.citilistingservice.repository.PhotoListingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CityListingServiceImpl implements CityListingService {

    private final CityListingRepository cityRepository;

    private final PhotoListingRepository photoListingRepository;

    private final CityMapper cityMapper = new CityMapperImpl();

    public CityListingServiceImpl(CityListingRepository cityRepository, PhotoListingRepository photoListingRepository) {
        this.cityRepository = cityRepository;
        this.photoListingRepository = photoListingRepository;
    }

    @Override
    public Page<CityDto> getCities(String name, Pageable pageable) {
        Page<City> cities = name != null ? cityRepository.findAllByNameLike(name, pageable)
                : cityRepository.findAll(pageable);
        List<CityDto> citiesFound = cities.getContent()
                .stream()
                .map(cityMapper::fromCity)
                .collect(Collectors.toList());
        return new PageImpl<>(citiesFound);
    }

    @Override
    public void deleteCity(long id) {
        cityRepository.deleteById(id);
    }

    @Override
    public void updateCityDetail(long id, PhotoDto newCity) {
        Optional<City> found = cityRepository.findById(id);
        found.ifPresent(city -> {
            Optional<Photo> photoOptional = photoListingRepository.findByCity(city);
            photoOptional.ifPresentOrElse(
                    photoFound -> {
                        if (photoFound.getPhotoName() != null) {
                            photoFound.setPhotoName(newCity.getPhotoName());
                        }
                        if (newCity.getPhotoUrl() != null) {
                            photoFound.setPhotoUrl(newCity.getPhotoUrl());
                        }
                        photoFound.setCity(city);
                        photoListingRepository.save(photoFound);
                    }, () -> {
                        Photo photo = Photo.builder()
                                .photoName(newCity.getPhotoName())
                                .photoUrl(newCity.getPhotoUrl())
                                .build();
                        photo.setCity(city);
                        photoListingRepository.save(photo);
                    });
        });
    }

    @Override
    public void updateCityDetail(long id, CityDto cityDto) {
        if (cityDto.getName() != null) {
            cityRepository.findById(id)
                    .ifPresent(city -> {
                        city.setName(cityDto.getName());
                        cityRepository.save(city);
                    });
        }
    }

    @Override
    public Page<PhotoDto> getPhotos(String name, Pageable pageable) throws Exception {
        Page<Photo> photos = null != name ? photoListingRepository.findAllByPhotoNameLike(name, pageable)
                                          :  photoListingRepository.findAll(pageable);
        return new PageImpl<>(photos.getContent().stream().map(cityMapper::fromPhoto).collect(Collectors.toList()));
    }

    @Override
    public Page<PhotoDto> getPhotos(long id, Pageable pageable) throws Exception {
        Optional<City> city = cityRepository.findById(id);
        if(city.isEmpty()){
            throw new Exception("city not found");
        }
        Page<Photo> photos = photoListingRepository.findAllByCity(city.get(),pageable);
        return new PageImpl<>(photos.getContent().stream().map(cityMapper::fromPhoto).collect(Collectors.toList()));
    }

    @Override
    public void save(CityDto cityDto) {
        this.cityRepository.save(cityMapper.toCity(cityDto));
    }
}
