package com.chilibytes.mystify.core.feature.textoverlay.service;

import com.chilibytes.mystify.general.service.SharedConstant;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FontSettingsEventHandlerService {

    private final FontSettingsService fontSettingsService;

    public record FontSettingsControls(Label lblFontFamily, ComboBox<String> cbxFontFamily,
                                       Label lblFontSize, Slider sldFontSize,
                                       Label lblFontColor, ColorPicker cpFontColor,
                                       Label lblPreview, TextArea txtPreview,
                                       Button btnOk, Button btnCancel) {

    }

    public void setupEventHandlers(Stage stage, FontSettingsControls controls) {

        stage.setOnShown(e -> {
            controls.cbxFontFamily.setValue(SharedConstant.getGlobalFontFamily());
            controls.sldFontSize.setValue(SharedConstant.getGlobalFontSize());
            controls.cpFontColor.setValue(SharedConstant.getGlobalFontColor());
            controls.txtPreview.setStyle(SharedConstant.getGlobalFontStyle());
        });
        controls.cbxFontFamily.setItems(fontSettingsService.getSystemFonts());

        controls.cpFontColor.setOnAction(e -> {
                    controls.txtPreview.setStyle(fontSettingsService.getFontColor(controls.cpFontColor.getValue()));
                    SharedConstant.setGlobalFontColor(controls.cpFontColor.getValue());
                }
        );

        controls.sldFontSize.valueProperty().addListener((obs, oldValue, newValue) -> {
            controls.txtPreview.setStyle(fontSettingsService.getFontSize(newValue.intValue()));
            SharedConstant.setGlobalFontSize(newValue.intValue());
        });

        controls.cbxFontFamily.setOnAction(e -> {
            String selectedFont = controls.cbxFontFamily.getValue();
            if (selectedFont != null) {
                controls.txtPreview.setStyle(fontSettingsService.getFontFamily(selectedFont));
                SharedConstant.setGlobalFontFamily(selectedFont);
            }
        });
        controls.btnOk.setOnAction(e -> {
                    SharedConstant.setGlobalFontStyle(fontSettingsService.getCombinedStyle());
                    stage.close();
                }
        );
        controls.btnCancel.setOnAction(e -> stage.close());
    }
}
