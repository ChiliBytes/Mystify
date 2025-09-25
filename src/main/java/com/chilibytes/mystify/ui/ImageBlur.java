package com.chilibytes.mystify.ui;

import com.chilibytes.mystify.config.SpringConfig;
import com.chilibytes.mystify.ui.component.UIService;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;


@Slf4j
public class ImageBlur extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initializing spring context on start()
            ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
            UIService uiService = context.getBean(UIService.class);
            uiService.initializeUI(primaryStage);

        } catch (Exception e) {
            log.error("Error on start(): {}", e.getMessage(), e);
            showError("Start","An error has occurred while starting the app", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}