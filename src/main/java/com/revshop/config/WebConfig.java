package com.revshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path directPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path nestedPath = Paths.get("revshop", uploadDir).toAbsolutePath().normalize();

        Set<String> locations = new LinkedHashSet<>();
        locations.add("file:" + directPath + "/");
        locations.add("file:" + nestedPath + "/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(locations.toArray(String[]::new));
    }
}
