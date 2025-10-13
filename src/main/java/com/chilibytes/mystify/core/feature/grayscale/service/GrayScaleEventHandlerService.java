package com.chilibytes.mystify.core.feature.grayscale.service;

import com.chilibytes.mystify.general.service.MainEventHandlerService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrayScaleEventHandlerService {
    private final MainEventHandlerService mainEventHandlerService;

    private final GrayScaleService grayScaleService;

    public record GrayScaleControls(Label lblFactor, Slider sldScaleFactor,
                                    Button okButton, Button cancelButton) {
    }

    public void setupEventHandlers(Stage stage, GrayScaleControls controls) {
        controls.okButton.setOnAction(e -> stage.close());
        controls.sldScaleFactor.valueProperty().addListener((obs, oldValue, newValue) ->
                grayScaleService.grayScaleImage(mainEventHandlerService, ((Double) newValue))
        );

        controls.cancelButton.setOnAction(e -> {
            mainEventHandlerService.handleResetImage();
            stage.close();
        });
    }
}
