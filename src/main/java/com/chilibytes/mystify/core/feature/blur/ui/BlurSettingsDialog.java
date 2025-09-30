package com.chilibytes.mystify.core.feature.blur.ui;

import com.chilibytes.mystify.core.feature.blur.service.BlurEventHandlerService;
import com.chilibytes.mystify.ui.MystifyApplication;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createSlider;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;

@Slf4j
@Component
@RequiredArgsConstructor
public class BlurSettingsDialog {

    private final BlurEventHandlerService blurEventHandlerService;

    private Slider blurSlider;
    private Slider brushSlider;

    private Label blurLabel;
    private Label brushLabel;

    private Button acceptButton;
    private Button cancelButton;

    private static final String CONTROLS_COLOR_STYLE = "-fx-background-color: #2C3E50; -fx-text-fill: white;";
    private static final String BLUR_SLIDER_LABEL = "Blur Level: ";
    private static final String BRUSH_SLIDER_LABEL = "Brush Size: ";

    public void showSettingsDialog() {
        Stage settingsStage = configureSettingsStage();

        configureSceneControls();
        applyBlurDialogSettings();

        BlurEventHandlerService.CommonBlurDialogButtons commonBlurDialogButtons = new BlurEventHandlerService.CommonBlurDialogButtons(acceptButton, cancelButton);
        BlurEventHandlerService.CommonBlurDialogControls commonBlurDialogControls = new BlurEventHandlerService.CommonBlurDialogControls(
                blurSlider, brushSlider, blurLabel, brushLabel
        );

        blurEventHandlerService.setupEventHandlers(settingsStage, commonBlurDialogButtons, commonBlurDialogControls);

        HBox buttonsBox = getButtonsLayout(acceptButton, cancelButton);
        VBox layout = getPrincipalLayout(buttonsBox);

        Scene settingsScene = new Scene(layout);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }

    public void applyBlurDialogSettings() {
        this.blurSlider.setValue(MystifyApplication.controlSettingsCache.getBlurLevel());
        this.brushSlider.setValue(MystifyApplication.controlSettingsCache.getBrushSize());

        this.blurLabel.setText("Radius: " + (int) this.blurSlider.getValue() + "px");
        this.brushLabel.setText(BRUSH_SLIDER_LABEL + (int) this.brushSlider.getValue() + "px");
    }

    private Stage configureSettingsStage() {
        Stage settingsStage = new Stage();
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Settings - Levels");
        settingsStage.setMinWidth(350);
        settingsStage.setMinHeight(320);
        return settingsStage;
    }

    private HBox getButtonsLayout(Button... buttons) {
        HBox buttonBox = new HBox(20, buttons);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        return buttonBox;
    }

    private VBox getPrincipalLayout(HBox buttonsBox) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle(CONTROLS_COLOR_STYLE);
        layout.setAlignment(Pos.CENTER);

        VBox blurGroup = new VBox(5, blurLabel, blurSlider);
        blurGroup.setAlignment(Pos.CENTER_LEFT);

        VBox brushGroup = new VBox(5, brushLabel, brushSlider);
        brushGroup.setAlignment(Pos.CENTER_LEFT);

        Label settingsTitleLabel = createLabel("Blur Image settings");
        layout.getChildren().addAll(settingsTitleLabel, blurGroup, brushGroup, buttonsBox);
        return layout;
    }

    private void configureSceneControls() {
        this.blurSlider = createSlider();
        this.brushSlider = createSlider();

        this.blurLabel = createLabel(BLUR_SLIDER_LABEL + (int) this.blurSlider.getValue());
        this.brushLabel = createLabel(BRUSH_SLIDER_LABEL + (int) this.brushSlider.getValue());

        this.blurSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                this.blurLabel.setText(BLUR_SLIDER_LABEL + newVal.intValue()));
        this.brushSlider.valueProperty().addListener((obs, oldVal, newVal) ->
                this.brushLabel.setText(BRUSH_SLIDER_LABEL + newVal.intValue()));

        this.acceptButton = createStandardButton("Save Settings");
        this.cancelButton = createStandardButton("Cancel");
    }
}
