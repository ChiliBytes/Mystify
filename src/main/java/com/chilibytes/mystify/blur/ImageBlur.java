package com.chilibytes.mystify.blur;

import com.chilibytes.mystify.config.SpringConfig;
import com.chilibytes.mystify.exception.MystifyGenericException;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ImageBlur extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initializing spring context on start()
            ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
            UIService uiService = context.getBean(UIService.class);
            uiService.initializeUI(primaryStage);
        } catch (Exception e) {
            throw new MystifyGenericException("Error initializing Spring context", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}