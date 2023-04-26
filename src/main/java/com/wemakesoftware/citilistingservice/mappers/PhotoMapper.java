package com.wemakesoftware.citilistingservice.mappers;

import com.wemakesoftware.citilistingservice.dto.PhotoDto;
import com.wemakesoftware.citilistingservice.model.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PhotoMapper {

    Photo toCity(PhotoDto photoDto);

    PhotoDto fromCity(Photo photo);
    
}
