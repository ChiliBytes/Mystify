package com.chilibytes.mystify.general.service;

import com.chilibytes.mystify.core.feature.blur.service.BlurProcessorService;
import com.chilibytes.mystify.core.service.ImageService;
import com.chilibytes.mystify.ui.component.PrincipalLayoutsBuilder;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.chilibytes.mystify.ui.common.CustomDialog.showSuccess;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommonEventHandlerService {

    private final BlurProcessorService blurProcessorService;
    private final ZoomService zoomService;
    private final FileService fileService;
    private final ImageService imageService;
    private final UndoService undoService;
    private final PrincipalLayoutsBuilder principalLayoutsBuilder;

    @Setter
    @Getter
    private WritableImage currentImage;

    @Setter
    @Getter
    private ImageView imageView;

    @Getter
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

        imageView.setOnMouseReleased(e -> blurProcessorService.setDragging(Boolean.FALSE));
        imageView.setOnMouseExited(e -> blurProcessorService.setDragging(Boolean.FALSE));

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
        this.originalImage = image;
        currentImage = imageService.createWritableImageCopy(image);
        setCurrentImage(currentImage);
        imageView.setImage(currentImage);
        zoomService.resetZoom();
        zoomService.applyZoom(imageView);
        principalLayoutsBuilder.getOuterZoomSlider().setValue(100);
        undoService.clearHistory();
        principalLayoutsBuilder.updateUndoPanelButtonState(false);
        this.setImageView(imageView);
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
            currentImage = blurProcessorService.createBlankImage((int) currentImage.getWidth(), (int) currentImage.getHeight());
            setCurrentImage(currentImage);
            imageView.setImage(currentImage);

            ((Pane)imageView.getParent())
                    .getChildren()
                    .removeIf(control -> !(control instanceof  ImageView));
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

    public WritableImage handleCreateOriginalImageCopy() {
        return imageService.createWritableImageCopy(this.originalImage);
    }
}
