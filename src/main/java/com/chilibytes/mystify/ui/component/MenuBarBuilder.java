package com.chilibytes.mystify.ui.component;

import com.chilibytes.mystify.general.service.CommonEventHandlerService;
import com.chilibytes.mystify.core.feature.blur.ui.BlurSettingsDialog;
import com.chilibytes.mystify.core.feature.collage.ui.CollageDialog;
import com.chilibytes.mystify.core.feature.pdf.ui.PdfMakerDialog;
import com.chilibytes.mystify.core.feature.video.ui.SlideshowDialog;
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
    private final BlurSettingsDialog blurSettingsDialog;
    private final CollageDialog collageDialog;
    private final PdfMakerDialog pdfMakerDialog;
    private final SlideshowDialog slideshowDialog;

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

        MenuItem blurItem = new MenuItem("Blur Effect");
        blurItem.setOnAction(e -> blurSettingsDialog.showDialog());

        Menu blurMenu = new Menu("Image Filters");
        blurMenu.getItems().add(blurItem);

        MenuItem collageItem = new MenuItem("Create Collage");
        collageItem.setOnAction(e -> collageDialog.showDialog());

        MenuItem images2PdfItem = new MenuItem("Images to PDF");
        images2PdfItem.setOnAction(e -> pdfMakerDialog.showDialog());

        Menu collageMenu = new Menu("Image Creation");
        collageMenu.getItems().addAll(collageItem, images2PdfItem);


        MenuItem slideshowsItem = new MenuItem("Create SlideShows");
        slideshowsItem.setOnAction(e -> slideshowDialog.showDialog());

        Menu slideshowMenu = new Menu("Video");
        slideshowMenu.getItems().add(slideshowsItem);


        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, blurMenu, collageMenu, slideshowMenu);
        menuBar.useSystemMenuBarProperty().set(false);

        return menuBar;
    }
}
