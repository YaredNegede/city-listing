package com.wemakesoftware.citilistingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDto {

    private String photoName;

    private String photoUrl;

}
