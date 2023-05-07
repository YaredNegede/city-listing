package com.wemakesoftware.citilistingservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityDto {

    private long id;

    @NotEmpty(message = "Name is required")
    private String name;

    private String countryName;

}
