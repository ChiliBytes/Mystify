package com.chilibytes.mystify.blur;

import com.chilibytes.mystify.exception.MystifyGenericException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsService {

    private static final String CONFIG_FILE = "settings.json";
    private Settings currentSettings;

    public SettingsService() {
        this.currentSettings = new Settings(15, 20, 1.0, 30, 50, 5, 35, 45);
    }

    public void saveSettings(int blurLevel, int brushSize, double zoomLevel) {
        currentSettings = new Settings(blurLevel, brushSize, zoomLevel);

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(currentSettings, writer);
        } catch (IOException e) {
            throw new MystifyGenericException("IOException on saveSettings() " + e.getMessage());
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
            throw new MystifyGenericException("IOException on loadSettings() " + e.getMessage());
        }

        return new Settings(15, 20, 1.0, 30, 50, 5, 35, 45);
    }

    public static class Settings {
        private final int blurLevel;
        private final int brushSize;
        private final double zoomLevel;
        private final int leftPaneMinWidth;
        private final int leftPaneMaxWidth;
        private final int leftPanePadding;
        private final int footerMinHeight;
        private final int footerMaxHeight;


        public Settings(int blurLevel, int brushSize, double zoomLevel) {
            this.blurLevel = blurLevel;
            this.brushSize = brushSize;
            this.zoomLevel = zoomLevel;
            this.leftPaneMinWidth = 30;
            this.leftPaneMaxWidth = 50;
            this.leftPanePadding = 80;
            this.footerMinHeight = 35;
            this.footerMaxHeight = 45;
        }

        //TODO: Lot of arguments here
        public Settings(int blurLevel, int brushSize, double zoomLevel,
                        int leftPaneMinWidth, int leftPaneMaxWidth, int leftPanePadding,
                        int footerMinHeight, int footerMaxHeight) {
            this.blurLevel = blurLevel;
            this.brushSize = brushSize;
            this.zoomLevel = zoomLevel;
            this.leftPaneMinWidth = leftPaneMinWidth;
            this.leftPaneMaxWidth = leftPaneMaxWidth;
            this.leftPanePadding = leftPanePadding;
            this.footerMinHeight = footerMinHeight;
            this.footerMaxHeight = footerMaxHeight;
        }

        // Getters
        public int getBlurLevel() {
            return blurLevel;
        }

        public int getBrushSize() {
            return brushSize;
        }

        public double getZoomLevel() {
            return zoomLevel;
        }

        public int getLeftPaneMinWidth() {
            return leftPaneMinWidth;
        }

        public int getLeftPaneMaxWidth() {
            return leftPaneMaxWidth;
        }

        public int getLeftPanePadding() {
            return leftPanePadding;
        }

        public int getFooterMinHeight() {
            return footerMinHeight;
        }

        public int getFooterMaxHeight() {
            return footerMaxHeight;
        }
    }
}
