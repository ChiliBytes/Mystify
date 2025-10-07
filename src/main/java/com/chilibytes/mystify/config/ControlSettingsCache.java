package com.chilibytes.mystify.config;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ControlSettingsCache {
    private int blurLevel;
    private int brushSize;
    private int leftPaneMinWidth;
    private int leftPaneMaxWidth;
    private int leftPanePadding;
    private int footerMinHeight;
    private int footerMaxHeight;
    private WritableImage originalImage;
    private ImageView imageView;
}
