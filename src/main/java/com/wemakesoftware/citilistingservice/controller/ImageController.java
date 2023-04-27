package com.wemakesoftware.citilistingservice.controller;

import com.wemakesoftware.citilistingservice.dto.DownloadFileResponseDto;
import com.wemakesoftware.citilistingservice.service.ImageService;
import io.minio.errors.MinioException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/api/city/image/")
@Slf4j
public class ImageController {

    private ImageService imageService;

    @PutMapping(value = "update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> update(@RequestPart("image") MultipartFile image,
                                 @RequestParam("objectName") String objectName) throws Exception {
        return ResponseEntity.ok(imageService.replace(image, objectName));
    }

    @PostMapping(value = "create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@RequestPart("image") MultipartFile image,
                                       @RequestParam("objectName") String objectName) throws Exception {
        return ResponseEntity.ok(imageService.uploadImage(image, objectName));
    }

    @DeleteMapping(value = "remove")
    public ResponseEntity<Void> remove(@RequestParam("objectName") String objectName) throws Exception {
        imageService.remove(objectName);
        return ResponseEntity.accepted().build();
    }

    @GetMapping(value = "download", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Resource> download(@RequestParam("objectName") String objectName) throws Exception {

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

