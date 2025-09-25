package com.chilibytes.mystify.core.feature.blur.service;

import com.chilibytes.mystify.config.service.ApplicationOptionManagerService;
import com.chilibytes.mystify.core.feature.blur.ui.BlurSettingsDialog;

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
public class BlurEventHandlerService {

    private final ApplicationOptionManagerService applicationOptionManagerService;
    private static final String BRUSH_SLIDER_LABEL = "Brush Size: ";

    public record CommonBlurDialogButtons(Button saveButton, Button cancelButton) {
    }

    public record CommonBlurDialogControls(Slider blurSlider, Slider brushSlider,
                                           Label blurLabel, Label brushLabel) {
    }

    public void handleOpenBlurSettings(BlurSettingsDialog blurSettingsDialogPanel) {
        blurSettingsDialogPanel.showSettingsDialog();
    }

    public void setupEventHandlers(Stage stage, CommonBlurDialogButtons blurDialogButtons,
                                   CommonBlurDialogControls blurDialogControls) {
        blurDialogButtons.saveButton.setOnAction(e -> {
            saveBlurSettings(blurDialogControls);
            stage.close();
        });

        blurDialogButtons.cancelButton.setOnAction(e -> stage.close());
    }

    public void saveBlurSettings(CommonBlurDialogControls blurDialogControls) {

        int blurRadius = (int) blurDialogControls.blurSlider.getValue();
        int brushSize = (int) blurDialogControls.brushSlider.getValue();

        blurDialogControls.blurSlider.setValue(blurRadius);
        blurDialogControls.brushSlider.setValue(brushSize);

        blurDialogControls.blurLabel.setText("Radius: " + blurRadius + "px");
        blurDialogControls.brushLabel.setText(BRUSH_SLIDER_LABEL + brushSize + "px");

        applicationOptionManagerService.saveSettings(blurRadius, brushSize);
    }
}
