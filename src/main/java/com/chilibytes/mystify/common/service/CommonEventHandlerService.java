package com.chilibytes.mystify.common.service;

import com.chilibytes.mystify.component.FileService;
import com.chilibytes.mystify.core.feature.blur.BlurProcessor;
import com.chilibytes.mystify.core.service.ImageService;
import com.chilibytes.mystify.ui.component.PrincipalLayoutsBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.chilibytes.mystify.ui.common.CustomDialog.showSuccess;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommonEventHandlerService {

    private final BlurProcessor blurProcessor;
    private final ZoomService zoomService;
    private final FileService fileService;
    private final ImageService imageService;
    private final UndoService undoService;
    private final PrincipalLayoutsBuilder principalLayoutsBuilder;

    @Setter
    private WritableImage currentImage;
    private ImageView imageView;
    private Image originalImage;

    public record CommonApplicationButtons(Button loadButton, Button saveButton,
                                           Button resetButton, Button clearButton, Button undoButton) {

    }

    public record CommonApplicationControls(ImageView imageView, Slider outerZoomSlider) {

    }

    public void setupEventHandlers(Stage stage, CommonApplicationButtons commonApplicationButtons,
                                   CommonApplicationControls commonApplicationControls) {
        this.imageView = commonApplicationControls.imageView;

        commonApplicationButtons.loadButton.setOnAction(e -> handleLoadImage(stage));
        commonApplicationButtons.saveButton.setOnAction(e -> handleSaveImage(stage));
        commonApplicationButtons.resetButton.setOnAction(e -> handleResetImage());
        commonApplicationButtons.clearButton.setOnAction(e -> handleClearImage());
        commonApplicationButtons.undoButton.setOnAction(e -> handleUndo());

        imageView.setOnMouseDragged(e -> blurProcessor.handleApplyBlur(e, this.currentImage, this.imageView));
        imageView.setOnMouseClicked(e -> blurProcessor.handleApplyBlur(e, this.currentImage, this.imageView));
        imageView.setOnMouseReleased(e -> blurProcessor.setDragging(Boolean.FALSE));
        imageView.setOnMouseExited(e -> blurProcessor.setDragging(Boolean.FALSE));

        commonApplicationControls.outerZoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double zoomLevel = newVal.doubleValue() / 100.0;
            zoomService.setZoomLevel(zoomLevel);
            zoomService.applyZoom(imageView);
        });
    }

    public void handleLoadImage(Stage stage) {
        java.util.Optional<Image> loadedImage = fileService.loadImage(stage);
        loadedImage.ifPresent(image -> {
            this.originalImage = image;
            processLoadedImage(image);
        });
    }

    private void processLoadedImage(Image image) {
        currentImage = imageService.createWritableImageCopy(image);
        setCurrentImage(currentImage);
        imageView.setImage(currentImage);
        zoomService.resetZoom();
        zoomService.applyZoom(imageView);
        principalLayoutsBuilder.getOuterZoomSlider().setValue(100);
        undoService.clearHistory();
        principalLayoutsBuilder.updateUndoPanelButtonState(false);
    }

    public void handleSaveImage(Stage stage) {
        if (currentImage != null) {
            boolean saved = fileService.saveImage(stage, currentImage, fileService.getDefaultExtension());
            if (saved) {
                showSuccess("Image saved successfully");
            }
        } else {
            showSuccess("Warning", "No images to save found!");
        }
    }

    public void handleUndo() {
        if (undoService.canUndo()) {
            currentImage = undoService.undo(currentImage);
            setCurrentImage(currentImage);
            imageView.setImage(currentImage);
            principalLayoutsBuilder.updateUndoPanelButtonState(undoService.canUndo());
        }
    }

    public void handleClearImage() {
        if (currentImage != null) {
            undoService.saveState(currentImage);
            currentImage = blurProcessor.createBlankImage((int) currentImage.getWidth(), (int) currentImage.getHeight());
            setCurrentImage(currentImage);
            imageView.setImage(currentImage);
        }
    }

    public void handleResetImage() {
        if (originalImage != null) {
            undoService.saveState(currentImage);
            currentImage = imageService.createWritableImageCopy(originalImage);
            setCurrentImage(currentImage);
            imageView.setImage(currentImage);
        }
    }
}
