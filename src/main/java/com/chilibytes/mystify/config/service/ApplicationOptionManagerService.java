package com.chilibytes.mystify.config.service;

import com.chilibytes.mystify.ui.MystifyApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;

@Slf4j
@Service
public class ApplicationOptionManagerService {

    private static final String CONFIG_FILE = "settings.json";
    private Settings currentSettings;

    public void saveSettings(int blurLevel, int brushSize) {
       persistSettings(blurLevel, brushSize);
       bulkSettingsToCache(blurLevel, brushSize);
    }
    private void bulkSettingsToCache(int blurLevel, int brushSize){
        MystifyApplication.controlSettingsCache.setBlurLevel(blurLevel);
        MystifyApplication.controlSettingsCache.setBrushSize(brushSize);
    }

    private void persistSettings(int blurLevel, int brushSize) {
        loadSettings();
        currentSettings = new Settings(blurLevel, brushSize, currentSettings.leftPaneMinWidth, currentSettings.leftPaneMaxWidth,
                currentSettings.leftPanePadding, currentSettings.footerMinHeight, currentSettings.footerMaxHeight);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(currentSettings, writer);
        } catch (IOException e) {
            log.error("IOException on saveSettings(): {}", e.getMessage(), e);
            showError("Save Settings", "An IOException has occurred while saving the settings", e);
        } catch (Exception ex) {
            log.error("Error on saveSettings(): {}", ex.getMessage(), ex);
            showError("Save Settings", "An error has occurred while saving the settings", ex);
        }
    }

    public Settings loadSettings() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Gson gson = new Gson();
            currentSettings = gson.fromJson(reader, Settings.class);

            if (currentSettings != null) {
                return currentSettings;
            }
        } catch (IOException e) {
            log.error("IOException on loadSettings(): {}", e.getMessage(), e);
            showError("Load Settings", "An IOException has occurred while loading the settings", e);
        }

        return new Settings();
    }

    @ToString
    @Getter
    @AllArgsConstructor
    public static class Settings {

        private static final int DEFAULT_BLUR_LEVEL = 15;
        private static final int DEFAULT_BRUSH_SIZE = 20;

        private static final int DEFAULT_LEFT_PANEL_MIN_WIDTH = 80;
        private static final int DEFAULT_LEFT_PANEL_MAX_WIDTH = 100;
        private static final int DEFAULT_LEFT_PANEL_PADDING = 15;

        private static final int DEFAULT_FOOTER_MIN_HEIGHT = 35;
        private static final int DEFAULT_FOOTER_MAX_HEIGHT = 45;

        private final int blurLevel;
        private final int brushSize;
        private final int leftPaneMinWidth;
        private final int leftPaneMaxWidth;
        private final int leftPanePadding;
        private final int footerMinHeight;
        private final int footerMaxHeight;

        public Settings() {
            this.blurLevel = DEFAULT_BLUR_LEVEL;
            this.brushSize = DEFAULT_BRUSH_SIZE;
            this.leftPaneMinWidth = DEFAULT_LEFT_PANEL_MIN_WIDTH;
            this.leftPaneMaxWidth = DEFAULT_LEFT_PANEL_MAX_WIDTH;
            this.leftPanePadding = DEFAULT_LEFT_PANEL_PADDING;
            this.footerMinHeight = DEFAULT_FOOTER_MIN_HEIGHT;
            this.footerMaxHeight = DEFAULT_FOOTER_MAX_HEIGHT;
        }
    }
}
