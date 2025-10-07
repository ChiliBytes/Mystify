package com.chilibytes.mystify.core.feature.blur.service;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomBlurEventHandlerService {

    private final BlurProcessorService blurProcessorService;


    public record BlurDialogControls(Label lblBlur, Slider sldBlur,
                                     Label lblBrush, Slider sldBrush,
                                     Button saveButton, Button cancelButton) {
    }

    public void setupEventHandlers(Stage stage, BlurDialogControls controls) {
        controls.sldBlur.setShowTickLabels(true);
        controls.sldBlur.setBlockIncrement(1);
        controls.sldBlur.setMajorTickUnit(1);
        blurProcessorService.applyBlurDialogSettings(controls);
        controls.saveButton.setOnAction(e -> {
            blurProcessorService.saveBlurSettings(controls);
            stage.close();
        });
        controls.cancelButton.setOnAction(e -> stage.close());
    }
}
