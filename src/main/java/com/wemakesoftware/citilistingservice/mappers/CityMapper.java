package com.wemakesoftware.citilistingservice.mappers;

import com.wemakesoftware.citilistingservice.dto.CityDto;
import com.wemakesoftware.citilistingservice.model.City;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface CityMapper {

    City toCity(CityDto cityDto);

    CityDto fromCity(City city);

}
