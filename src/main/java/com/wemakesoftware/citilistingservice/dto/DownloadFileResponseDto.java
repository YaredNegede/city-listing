package com.wemakesoftware.citilistingservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DownloadFileResponseDto {

    private String fileName;

    private String contentType;

    private byte[] inputStream;

}

