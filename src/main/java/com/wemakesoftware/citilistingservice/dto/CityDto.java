package com.wemakesoftware.citilistingservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CityDto {
    @NotEmpty(message = "Name is required")
    private String name;
    @NotEmpty(message = "Photo name is required")
    private String photoName;
    @NotEmpty(message = "Photo is required")
    private String photoUrl;
}
