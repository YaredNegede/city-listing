package db.migration;

import com.wemakesoftware.citilistingservice.model.City;
import com.wemakesoftware.citilistingservice.model.Photo;
import com.wemakesoftware.citilistingservice.repository.CityListingRepository;
import com.wemakesoftware.citilistingservice.repository.PhotoListingRepository;
import com.wemakesoftware.citilistingservice.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.sql.PreparedStatement;

@Slf4j
public class V1__FlywayMigration extends BaseJavaMigration {

    @Value("${minio.bucket-name.city}")
    private String cityImageBucketName;


    @Value("${migration.folder.cities}")
    private String migrationFolderCities = "classpath:data/V1_world-cities.csv";

    @Value("${migration.folder.cities.photos}")
    private String migrationFolderCitiesPhoto= "classpath:data/photos/";

    @Autowired
    private CityListingRepository cityListingRepository;

    @Autowired
    private PhotoListingRepository photoListingRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private Flyway flyway;

    public void migrate(File cities,Context context) throws Exception {
        log.debug("migration started");

        File photos = new File(migrationFolderCitiesPhoto);

        if (cities.exists()) {

            try (FileReader fileReader = new FileReader(cities)) {

                BufferedReader inStream = new BufferedReader(fileReader);

                String inString;

                while ((inString = inStream.readLine()) != null) {

                    String[] cyt = inString.split(",");

                    try (PreparedStatement statement =
                                 context
                                         .getConnection()
                                         .prepareStatement("INSERT INTO city (name, countryName) VALUES ('"+cyt[0]+"','"+cyt[1]+"')")) {
                        statement.execute();
                    }
                }
            }
        }
        log.debug("migration started");
    }

    @Override
    public void migrate(Context context) throws Exception {

        URL dataFile = getClass().getClassLoader().getResource("data/V1_world-cities.csv");

        migrate(new File(dataFile.getFile()),context);

    }
}
