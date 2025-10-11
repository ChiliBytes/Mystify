package com.chilibytes.mystify.core.feature.textoverlay.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FontSettingsService {

    private String colorStyle = "-fx-text-fill: #000000;";
    private String sizeStyle = "-fx-font-size: 14px;";
    private String fontFamilyStyle = "-fx-font-family: 'Arial';";

    public ObservableList<String> getSystemFonts() {
        List<String> availableFonts = javafx.scene.text.Font.getFamilies();
        ObservableList<String> fontList = FXCollections.observableArrayList(availableFonts);
        fontList.sort(String::compareToIgnoreCase);
        return fontList;
    }

    public String getCombinedStyle() {
        return colorStyle + " " + sizeStyle + " " + fontFamilyStyle;
    }

    public String getFontColor(Color color) {
        updateColor(color);
        return getCombinedStyle();
    }

    public String getFontSize(int size) {
        updateSize(size);
        return getCombinedStyle();
    }

    public String getFontFamily(String fontFamily) {
        updateFontFamily(fontFamily);
        return getCombinedStyle();
    }

    private void updateColor(Color color) {
        colorStyle = String.format("-fx-text-fill: #%02X%02X%02X;",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private void updateSize(int size) {
        sizeStyle = String.format("-fx-font-size: %dpx;", size);
    }

    private void updateFontFamily(String fontFamily) {
        fontFamilyStyle = String.format("-fx-font-family: '%s';", fontFamily);
    }
}
