package com.chilibytes.mystify.general.service;

import com.chilibytes.mystify.core.feature.blur.service.BlurProcessorService;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.chilibytes.mystify.ui.common.CustomDialog.showSuccess;

@Slf4j
@RequiredArgsConstructor
@Service
public class MainEventHandlerService {

    private final BlurProcessorService blurProcessorService;
    private final ZoomService zoomService;
    private final ImageService imageService;
    private final UndoService undoService;

    @Setter
    @Getter
    private WritableImage currentImage;

    @Setter
    @Getter
    private ImageView imageView;

    @Getter
    private Image originalImage;

    public record CommonApplicationButtons(Button btnLoadImage, Button btnSaveImage,
                                           Button btnResetImage, Button btnClearImage,
                                           Button btnUndo, Button btnRotateClockwise,
                                           Button btnTest) {

    }

    public record CommonApplicationControls(ImageView imageView, Slider outerZoomSlider) {

    }

    public void setupEventHandlers(Stage stage, CommonApplicationButtons commonApplicationButtons,
                                   CommonApplicationControls commonApplicationControls) {
        this.imageView = commonApplicationControls.imageView;

        commonApplicationButtons.btnLoadImage.setOnAction(e -> handleLoadImage(stage));
        commonApplicationButtons.btnSaveImage.setOnAction(e -> handleSaveImage(stage));
        commonApplicationButtons.btnResetImage.setOnAction(e -> handleResetImage());
        commonApplicationButtons.btnClearImage.setOnAction(e -> handleClearImage());
        commonApplicationButtons.btnUndo.setOnAction(e -> handleUndo());
        commonApplicationButtons.btnRotateClockwise.setOnAction(e -> handleRotateImage());

        commonApplicationButtons.btnTest.setOnAction(e -> {

        });

        imageView.setOnMouseReleased(e -> blurProcessorService.setDragging(Boolean.FALSE));
        imageView.setOnMouseExited(e -> blurProcessorService.setDragging(Boolean.FALSE));

        commonApplicationControls.outerZoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double zoomLevel = newVal.doubleValue() / 100.0;
            zoomService.setZoomLevel(zoomLevel);
            zoomService.applyZoom(imageView);
        });
    }

    public void handleLoadImage(Stage stage) {
        Optional<Image> loadedImage = imageService.loadImage(stage);
        loadedImage.ifPresent(image -> {
            this.originalImage = image;
            imageService.processLoadedImage(this);
        });
    }

    public void handleSaveImage(Stage stage) {
        if (currentImage != null) {
            boolean saved = imageService.saveImage(stage, currentImage, imageService.getDefaultExtension());
            if (saved) {
                showSuccess("Image saved successfully");
            }
        } else {
            showSuccess("Warning", "No images to save found!");
        }
    }

    public void handleClearImage() {
        imageService.clearImage(this);
    }

    public void handleResetImage() {
        imageService.handleResetImage(this);
    }

    public WritableImage handleCreateOriginalImageCopy() {
        return imageService.createWritableImageCopy(this);
    }

    private void handleUndo() {
        undoService.handleUndo(this);
    }

    private void handleRotateImage() {
        imageService.rotateImage(this);
    }
}
