package com.chilibytes.mystify.ui.component;

import com.chilibytes.mystify.core.feature.blur.ui.AutoBlurDialog;
import com.chilibytes.mystify.core.feature.blur.ui.CustomBlurDialog;
import com.chilibytes.mystify.core.feature.collage.ui.CollageDialog;
import com.chilibytes.mystify.core.feature.grayscale.ui.GrayScaleDialog;
import com.chilibytes.mystify.core.feature.imageoverlay.ui.ImageOverlayDialog;
import com.chilibytes.mystify.core.feature.pdf.ui.PdfMakerDialog;
import com.chilibytes.mystify.core.feature.resize.ui.ResizeImageDialog;
import com.chilibytes.mystify.core.feature.textoverlay.ui.FontSettingsDialog;
import com.chilibytes.mystify.core.feature.video.ui.SlideshowDialog;
import com.chilibytes.mystify.general.service.MainEventHandlerService;
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

    private final MainEventHandlerService mainEventHandlerService;
    private final CustomBlurDialog customBlurDialog;
    private final CollageDialog collageDialog;
    private final PdfMakerDialog pdfMakerDialog;
    private final SlideshowDialog slideshowDialog;
    private final AutoBlurDialog autoBlurDialog;
    private final GrayScaleDialog grayScaleDialog;
    private final ImageOverlayDialog imageOverlayDialog;
    private final FontSettingsDialog fontSettingsDialog;
    private final ResizeImageDialog resizeImageDialog;

    @Getter
    @Setter
    private Image image;

    public MenuBar createMenuBar(Stage primaryStage) {
        MenuItem loadImageItem = new MenuItem("Load Image");
        loadImageItem.setOnAction(e -> mainEventHandlerService.handleLoadImage(primaryStage));

        MenuItem saveImageItem = new MenuItem("Save Image");
        saveImageItem.setOnAction(e -> mainEventHandlerService.handleSaveImage(primaryStage));

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(loadImageItem, saveImageItem);


        MenuItem resetImageItem = new MenuItem("Reset Image");
        resetImageItem.setOnAction(e -> mainEventHandlerService.handleResetImage());

        MenuItem clearImageItem = new MenuItem("Clear Image");
        clearImageItem.setOnAction(e -> mainEventHandlerService.handleClearImage());

        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(resetImageItem, clearImageItem);

        MenuItem customBlurItem = new MenuItem("Custom Blur");
        customBlurItem.setOnAction(e -> customBlurDialog.showDialog());

        MenuItem autoBlurItem = new MenuItem("Automatic Blur");
        autoBlurItem.setOnAction(e -> autoBlurDialog.showDialog());

        MenuItem grayScaleItem = new MenuItem("Gray Scale");
        grayScaleItem.setOnAction(e -> grayScaleDialog.showDialog());

        Menu blurMenu = new Menu("Image Filters");
        blurMenu.getItems().addAll(customBlurItem, autoBlurItem, grayScaleItem);

        MenuItem collageItem = new MenuItem("Create Collage");
        collageItem.setOnAction(e -> collageDialog.showDialog());

        MenuItem images2PdfItem = new MenuItem("Images to PDF");
        images2PdfItem.setOnAction(e -> pdfMakerDialog.showDialog());

        MenuItem overlayImagesItem = new MenuItem("Overlay Images");
        overlayImagesItem.setOnAction(e -> imageOverlayDialog.showDialog());

        Menu collageMenu = new Menu("Image Editor");
        collageMenu.getItems().addAll(collageItem, images2PdfItem, overlayImagesItem);

        MenuItem resizeImagesItem = new MenuItem("Downgrade from HD");
        resizeImagesItem.setOnAction(e -> resizeImageDialog.showDialog());

        Menu resizeMenu = new Menu("Image Resizer");
        resizeMenu.getItems().addAll(resizeImagesItem);

        MenuItem fontSettingsItem = new MenuItem("Font Settings");
        fontSettingsItem.setOnAction(e -> fontSettingsDialog.showDialog());

        Menu settingsMenu = new Menu("Settings");
        settingsMenu.getItems().add(fontSettingsItem);

        MenuItem slideshowsItem = new MenuItem("Create SlideShows");
        slideshowsItem.setOnAction(e -> slideshowDialog.showDialog());

        Menu slideshowMenu = new Menu("Video");
        slideshowMenu.getItems().add(slideshowsItem);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, blurMenu, collageMenu, settingsMenu, slideshowMenu, resizeMenu);
        menuBar.useSystemMenuBarProperty().set(false);

        return menuBar;
    }
}
