package com.wemakesoftware.citilistingservice.flyway;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Statement;

@Slf4j
public class FlywayMigration extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        log.debug("migration started");
        final Resource cityResource = new ClassPathResource("src/test/resources/data/world-cities.csv");
        FileReader fileReader = new FileReader(cityResource.getFile());
        BufferedReader inStream = new BufferedReader(fileReader);
        String inString;
        while ((inString = inStream.readLine()) != null) {
            log.debug("migrating "+inString);
            String[] cyt = inString.split(",");
            try (Statement select = context.getConnection().createStatement()) {
                try (Statement update = context.getConnection().createStatement()) {
                    update.execute("INSERT INTO city (name) VALUES ("+cyt[0]+"");
                }
            }
        }
        log.debug("migration started");
    }
}
