package com.wemakesoftware.citilistingservice.flyway;

import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.model.Photo;
import com.wemakesoftware.citilistingservice.repository.CityListingRepository;
import com.wemakesoftware.citilistingservice.repository.PhotoListingRepository;
import com.wemakesoftware.citilistingservice.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class FlywayMigration extends BaseJavaMigration {

    @Value("${minio.bucket-name.city}")
    private String cityImageBucketName;


    @Value("${migration.folder.cities}")
    private String migrationFolderCities;

    @Value("${migration.folder.cities.photos}")
    private String migrationFolderCitiesPhoto;

    @Autowired
    private CityListingRepository cityListingRepository;

    @Autowired
    private PhotoListingRepository photoListingRepository;

    @Autowired
    private ImageService imageService;

    @Override
    public void migrate(Context context) throws Exception {
        log.debug("migration started");

        File cities = new File(migrationFolderCities);

        File photos = new File(migrationFolderCitiesPhoto);

        if(cities.exists()) {

            try (FileReader fileReader = new FileReader(migrationFolderCities)) {

                BufferedReader inStream = new BufferedReader(fileReader);

                String inString;

                while ((inString = inStream.readLine()) != null) {

                    String[] cyt = inString.split(",");

                    City city = cityListingRepository.save(new City(cyt[0]));

                    if(photos.exists()) {

                        File citiesPhoto = new File(migrationFolderCitiesPhoto + File.separator + cyt[1]);

                        List<File> cityPhotoDir = List.of(Objects.requireNonNull(citiesPhoto.listFiles()));

                        cityPhotoDir.forEach(file -> {
                            Resource res = new FileSystemResource(file);
                            try {
                                String url = imageService.uploadFile(cityImageBucketName, res, file.getName());
                                Photo photo = Photo.builder()
                                        .photoUrl(url)
                                        .photoName(file.getName())
                                        .city(city)
                                        .build();
                                photoListingRepository.save(photo);
                            } catch (Exception e) {
                                log.error(e.getMessage());
                            }
                        });

                    }
                }
            }
        }

        log.debug("migration started");
    }
}
