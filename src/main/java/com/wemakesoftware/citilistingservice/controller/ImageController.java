package com.wemakesoftware.citilistingservice.controller;


import com.wemakesoftware.citilistingservice.dto.DownloadFileResponseDto;
import com.wemakesoftware.citilistingservice.service.ImageService;
import io.minio.errors.MinioException;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/api/city/image/")
@Slf4j
public class ImageController {

    private ImageService imageService;

    @PatchMapping(produces = "application/json",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity update(@RequestPart("image") MultipartFile image,
                                 @RequestParam("objectName") String objectName) throws MinioException, IOException {
        return ResponseEntity.ok(imageService.replace(image, objectName));
    }

    @PostMapping(produces = "application/json",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@RequestPart("image") MultipartFile image,
                                       @RequestParam("objectName") String objectName) throws IOException {
        return ResponseEntity.ok(imageService.uploadImage(image, objectName));
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestParam("objectName") String objectName) throws MinioException {
        imageService.remove(objectName);
        return ResponseEntity.accepted().build();
    }

    @GetMapping(produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> download(@RequestParam("objectName") String objectName) throws MinioException {

        DownloadFileResponseDto downloadImage =   DownloadFileResponseDto.builder()
                                            .fileName(objectName)
                                            .inputStream(imageService.download(objectName))
                                            .contentType("image/jpeg") //TODO: hard coded mime type
                                            .build();

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(downloadImage.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + downloadImage.getFileName())
                .body(new ByteArrayResource(downloadImage.getInputStream()));
    }

}

