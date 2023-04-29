package com.wemakesoftware.citilistingservice.controller;

import com.wemakesoftware.citilistingservice.CityListingServiceApplication;
import com.wemakesoftware.citilistingservice.controller.util.MinioSetup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = CityListingServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class ImageControllerTestIT extends MinioSetup {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should be able to create image")
    void create() throws Exception {
        String fileName = UUID.randomUUID() +".jpeg";
        createOne(fileName, "1.jpeg", this.mockMvc)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));

    }

    @Test
    @DisplayName("Should not be able to create image")
    void createError() throws Exception {
        this.mockMvc.perform(post(Paths.root_image+Paths.root_image_create))
                .andExpect(status().is(400))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("Content-Type 'null' is not supported");
    }

    @Test
    @DisplayName("Should update images")
    void update() throws Exception {

        String fileName = UUID.randomUUID() +".jpeg";

        createOne(fileName, "1.jpeg", this.mockMvc)
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andReturn()
                    .getResponse()
                    .getContentAsString()
                    .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));

        final Resource imageResource = new ClassPathResource("2.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        MockMultipartHttpServletRequestBuilder builder = multipart(Paths.root_image+Paths.root_image_update);
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        this.mockMvc.perform(builder
                        .file("image", image.getBytes())
                        .param("objectName", fileName))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));

    }

    @Test
    @DisplayName("Should not be updating image")
    void updateError() throws Exception {

        final Resource imageResource = new ClassPathResource("1.jpeg");

        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        String fileName = UUID.randomUUID() +".jpeg";

        this.mockMvc.perform(
                        multipart(Paths.root_image+Paths.root_image_create)
                                .file("image", image.getBytes())
                                .param("objectName", fileName)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,fileName));

        this.mockMvc.perform(post(Paths.root_image+Paths.root_image_update))
                .andExpect(status().is(400))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("Content-Type 'null' is not supported");
    }

    @Test
    @DisplayName("Should remove image")
    void remove() throws Exception {
        String objectName = UUID.randomUUID() +".jpeg";
        createOne(objectName, "1.jpeg", this.mockMvc)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,objectName));

        mockMvc.perform(delete(Paths.root_image+Paths.root_image_delete+"?objectName="+objectName))
                .andDo(print())
               .andExpect(status().isAccepted());
    }
 
    @Test
    @DisplayName("Should download image")
    void download() throws Exception {
        final Resource imageResource = new ClassPathResource("1.jpeg");
        String objectName = UUID.randomUUID() +".jpeg";
        final MockMultipartFile image = new MockMultipartFile(
                "image", imageResource.getFilename(),
                MediaType.MULTIPART_FORM_DATA_VALUE,
                imageResource.getInputStream());

        createOne(objectName, "1.jpeg", this.mockMvc)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains(String.join(Paths.root_image,Paths.root_image_download,objectName));

        mockMvc.perform(get(Paths.root_image+Paths.root_image_download+"?objectName="+objectName))
                .andDo(print())
               .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type","image/jpeg"))
                .andExpect(header().stringValues("Content-Disposition","attachment; filename="+objectName));
    }


}
