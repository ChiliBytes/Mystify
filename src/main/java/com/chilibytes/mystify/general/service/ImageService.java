package com.chilibytes.mystify.general.service;

import com.chilibytes.mystify.config.ApplicationProperties;
import com.chilibytes.mystify.core.feature.imageoverlay.service.ImageOverlayService;
import com.chilibytes.mystify.ui.component.PrincipalLayoutsBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ApplicationProperties applicationProperties;
    private final ImageOverlayService imageOverlayService;
    private final UndoService undoService;
    private final PrincipalLayoutsBuilder principalLayoutsBuilder;
    private final ZoomService zoomService;
    private final ImageProcessor imageProcessor;

    public Optional<Image> loadImage(Stage stage) {
        FileChooser fileChooser = imageProcessor.createImageFileChooser();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                imageProcessor.setDefaultExtensionFromFile(file);
                return Optional.of(image);
            } catch (Exception e) {
                log.error("Error on loadImage(): {}", e.getMessage());
                showError("Load Image", "An Error has occurred while loading the image", e);
            }
        }
        return Optional.empty();
    }

    public void processLoadedImage(MainEventHandlerService mainEventHandlerService) {
        ImageView imageView = mainEventHandlerService.getImageView();
        WritableImage currentImage = createWritableImageCopy(mainEventHandlerService);
        mainEventHandlerService.setCurrentImage(currentImage);
        imageView.setImage(currentImage);
        zoomService.resetZoom();
        zoomService.applyZoom(imageView);
        principalLayoutsBuilder.getOuterZoomSlider().setValue(100);
        undoService.clearHistory();
        principalLayoutsBuilder.updateUndoPanelButtonState(false);
        mainEventHandlerService.setImageView(imageView);
    }

    public boolean saveImage(Stage stage, Image image, String defaultExtension) {
        FileChooser fileChooser = imageProcessor.createSaveFileChooser(defaultExtension);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Image imageToSave = SharedConstant.isMultiLayerImage() ?
                        imageOverlayService.unifyImage() :
                        image;
                imageProcessor.saveImageToFile(imageToSave, file);
                SharedConstant.setMultiLayerImage(false);
                return true;
            } catch (IOException e) {
                log.error("IOException on saveImage(): {}", e.getMessage(), e);
                showError("Save Image", "An IOException has occurred while saving the image", e);

            } catch (Exception ex) {
                log.error("Error on saveImage(): {}", ex.getMessage(), ex);
                showError("Save Image", "An error has occurred while saving the image", ex);
            }
        }
        return false;
    }

    public void handleResetImage(MainEventHandlerService mainEventHandlerService) {
        Image originalImage = mainEventHandlerService.getOriginalImage();
        if (originalImage != null) {
            undoService.saveState(mainEventHandlerService.getCurrentImage());
            WritableImage currentImage = createWritableImageCopy(mainEventHandlerService);
            mainEventHandlerService.setCurrentImage(currentImage);
            mainEventHandlerService.getImageView().setImage(currentImage);
        }
    }

    public void clearImage(MainEventHandlerService mainEventHandlerService) {
        WritableImage currentImage = mainEventHandlerService.getCurrentImage();
        if (!Objects.isNull(currentImage)) {
            undoService.saveState(currentImage);
            WritableImage blankImage = createBlankImage((int) currentImage.getWidth(), (int) currentImage.getHeight());
            mainEventHandlerService.setCurrentImage(blankImage);
            mainEventHandlerService.getImageView().setImage(blankImage);
            ((Pane) mainEventHandlerService.getImageView().getParent())
                    .getChildren()
                    .removeIf(control -> !(control instanceof ImageView));
        }
    }

    public WritableImage createWritableImageCopy(MainEventHandlerService mainEventHandlerService) {
        Image source = mainEventHandlerService.getOriginalImage();
        var copy = new WritableImage((int) source.getWidth(), (int) source.getHeight());
        var reader = source.getPixelReader();
        var writer = copy.getPixelWriter();

        IntStream.range(0, (int) source.getHeight())
                .forEach(y ->
                        IntStream.range(0, (int) source.getWidth())
                                .forEach(x ->
                                        writer.setColor(x, y, reader.getColor(x, y))
                                )
                );
        return copy;
    }

    public String getDefaultExtension() {
        return imageProcessor.getDefaultExtension();
    }

    private WritableImage createBlankImage(int width, int height) {
        WritableImage blankImage = new WritableImage(width, height);
        var writer = blankImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, Color.TRANSPARENT);
            }
        }
        return blankImage;
    }

}
