package com.chilibytes.mystify.ui;

import com.chilibytes.mystify.config.ControlSettingsCache;
import com.chilibytes.mystify.config.SpringConfig;
import com.chilibytes.mystify.config.service.ApplicationOptionManagerService;
import com.chilibytes.mystify.ui.component.UIService;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;

@Slf4j
public class MystifyApplication extends javafx.application.Application {


    public static final ControlSettingsCache controlSettingsCache = new ControlSettingsCache();

    private void createConfigurationsCache() {
        ApplicationOptionManagerService applicationOptionManagerService = new ApplicationOptionManagerService();
        ApplicationOptionManagerService.Settings settings = applicationOptionManagerService.loadSettings();

        controlSettingsCache.setBlurLevel(settings.getBlurLevel());
        controlSettingsCache.setBrushSize(settings.getBrushSize());

        controlSettingsCache.setFooterMinHeight(settings.getFooterMinHeight());
        controlSettingsCache.setFooterMaxHeight(settings.getFooterMaxHeight());

        controlSettingsCache.setLeftPaneMinWidth(settings.getLeftPaneMinWidth());
        controlSettingsCache.setLeftPaneMaxWidth(settings.getLeftPaneMaxWidth());
        controlSettingsCache.setLeftPanePadding(settings.getLeftPanePadding());

        //TODO: Can we use a getter via CommonEventHandlerService instead of adding to cache?
        controlSettingsCache.setOriginalImage(null);
        controlSettingsCache.setImageView(new ImageView());
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initializing spring context on start()
            ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
            createConfigurationsCache();

            UIService uiService = context.getBean(UIService.class);
            uiService.initializeUI(primaryStage);

        } catch (Exception e) {
            log.error("Error on start(): {}", e.getMessage(), e);
            showError("Start", "An error has occurred while starting the app", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}