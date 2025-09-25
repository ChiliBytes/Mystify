package com.chilibytes.mystify.ui.component;

import com.chilibytes.mystify.common.service.CommonEventHandlerService;
import com.chilibytes.mystify.core.feature.blur.service.BlurEventHandlerService;
import com.chilibytes.mystify.core.feature.blur.ui.BlurSettingsDialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MenuBarBuilder {

    private final CommonEventHandlerService commonEventHandlerService;
    private final BlurEventHandlerService blurEventHandlerService;
    private final BlurSettingsDialog blurSettingsDialog;

    @Getter
    @Setter
    private Image image;

    public MenuBar createMenuBar(Stage primaryStage) {
        MenuItem loadImageItem = new MenuItem("Load Image");
        loadImageItem.setOnAction(e -> commonEventHandlerService.handleLoadImage(primaryStage));

        MenuItem saveImageItem = new MenuItem("Save Image");
        saveImageItem.setOnAction(e -> commonEventHandlerService.handleSaveImage(primaryStage));

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(loadImageItem, saveImageItem);

        MenuItem resetImageItem = new MenuItem("Reset Image");
        resetImageItem.setOnAction(e -> commonEventHandlerService.handleResetImage());

        MenuItem clearImageItem = new MenuItem("Clear Image");
        clearImageItem.setOnAction(e -> commonEventHandlerService.handleClearImage());

        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(resetImageItem, clearImageItem);

        Menu settingsMenu = new Menu("Image Filters");
        MenuItem levelsItem = new MenuItem("Blur Effect");
        levelsItem.setOnAction(e -> blurEventHandlerService.handleOpenBlurSettings(blurSettingsDialog));
        settingsMenu.getItems().add(levelsItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, settingsMenu);
        menuBar.useSystemMenuBarProperty().set(false);

        return menuBar;
    }
}