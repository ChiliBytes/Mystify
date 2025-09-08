package com.chilibytes.mystify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import com.chilibytes.mystify.blur.*;

@Configuration
@ComponentScan("com.chilibytes.mystify.blur")
public class SpringConfig {

    @Bean
    public ImageProcessor imageProcessor() {
        return new ImageProcessor();
    }

    @Bean
    public FileService fileService() {
        return new FileService();
    }

    @Bean
    public UndoService undoService() {
        return new UndoService();
    }

    @Bean
    public ZoomService zoomService() {
        return new ZoomService();
    }

    @Bean
    public SettingsService settingsService() {
        return new SettingsService();
    }

    @Bean
    public UIService uiService(ImageProcessor imageProcessor, FileService fileService,
                               UndoService undoService, ZoomService zoomService,
                               SettingsService settingsService) {
        return new UIService(imageProcessor, fileService, undoService, zoomService, settingsService);
    }
}