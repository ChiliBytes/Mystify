package com.chilibytes.mystify.core.feature.blur.service;

import com.chilibytes.mystify.general.service.CommonEventHandlerService;
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
public class AutoBlurEventHandlerService {

    private final BlurProcessorService blurProcessorService;
    private final CommonEventHandlerService commonEventHandlerService;

    public record AutoBlurDialogControls(Label lblTitle, Slider sldBlurLevel, Button btnOk, Button btnCancel) {

    }

    public void setupEventHandlers(Stage stage, AutoBlurDialogControls controls) {
        controls.sldBlurLevel.valueProperty().addListener((obs, oldVal, newVal) ->
                blurProcessorService.applyFullBlur(commonEventHandlerService, newVal.intValue()));

        controls.btnOk.setOnAction(e -> stage.close());
        controls.btnCancel.setOnAction(e -> {
            commonEventHandlerService.handleResetImage();
            stage.close();
        });
    }
}
