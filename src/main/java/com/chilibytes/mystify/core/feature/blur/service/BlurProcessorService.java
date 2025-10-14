package com.chilibytes.mystify.core.feature.blur.service;

import com.chilibytes.mystify.config.service.ApplicationOptionManagerService;
import com.chilibytes.mystify.general.service.MainEventHandlerService;
import com.chilibytes.mystify.general.service.UndoService;
import com.chilibytes.mystify.ui.MystifyApplication;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlurProcessorService {

    private final UndoService undoService;
    private final ApplicationOptionManagerService applicationOptionManagerService;
    private static final String BRUSH_SLIDER_LABEL = "Brush Size: ";

    @Getter
    @Setter
    private boolean isDragging = false;

    public void applyCircularBlur(WritableImage image, int centerX, int centerY, int radius, int blurRadius) {

        var reader = image.getPixelReader();
        var writer = image.getPixelWriter();
        final int radiusSquared = radius * radius;

        IntStream.rangeClosed(
                        Math.max(0, centerY - radius),
                        Math.min((int) image.getHeight() - 1, centerY + radius)
                )
                .forEach(y ->
                        IntStream.rangeClosed(
                                        Math.max(0, centerX - radius),
                                        Math.min((int) image.getWidth() - 1, centerX + radius)
                                )
                                .filter(x -> {
                                    int dx = x - centerX;
                                    int dy = y - centerY;
                                    return (dx * dx + dy * dy) <= radiusSquared;
                                })
                                .forEach(x -> {
                                    var blurredColor = calculateBlurredColor(image, x, y, blurRadius, reader);
                                    writer.setColor(x, y, blurredColor);
                                })
                );
    }

    public Color calculateBlurredColor(WritableImage image, int x, int y, int radius, PixelReader reader) {
        if (radius <= 1) return reader.getColor(x, y);

        double totalRed = 0;
        double totalGreen = 0;
        double totalBlue = 0;
        double totalAlpha = 0;
        int count = 0;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int sampleX = x + dx;
                int sampleY = y + dy;

                if (isWithinImageBounds(image, sampleX, sampleY)) {
                    var color = reader.getColor(sampleX, sampleY);
                    totalRed += color.getRed();
                    totalGreen += color.getGreen();
                    totalBlue += color.getBlue();
                    totalAlpha += color.getOpacity();
                    count++;
                }
            }
        }

        return count == 0 ? reader.getColor(x, y) :
                new Color(totalRed / count, totalGreen / count, totalBlue / count, totalAlpha / count);
    }

    public boolean isWithinImageBounds(WritableImage image, int x, int y) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }

    public void handleApplyBlur(MouseEvent event, MainEventHandlerService mainEventHandlerService) {
        ImageView imageView = mainEventHandlerService.getImageView();
        WritableImage currentImage = mainEventHandlerService.getCurrentImage();

        if (currentImage != null) {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED ||
                    (event.getEventType() == MouseEvent.MOUSE_DRAGGED && !isDragging)) {
                undoService.saveState(currentImage);
                isDragging = true;
            }

            double imageX = convertToImageX(event.getX(), imageView);
            double imageY = convertToImageY(event.getY(), imageView);

            if (isWithinImageBounds(currentImage, (int) imageX, (int) imageY)) {
                int brushSize = MystifyApplication.controlSettingsCache.getBrushSize();
                int blurRadius = MystifyApplication.controlSettingsCache.getBlurLevel();

                applyCircularBlur(currentImage, (int) imageX, (int) imageY, brushSize, blurRadius);
            }
        }
    }

    private double convertToImageX(double mouseX, ImageView imageView) {
        if (imageView.getImage() == null) return mouseX;

        double viewWidth = imageView.getBoundsInLocal().getWidth();
        double imageWidth = imageView.getImage().getWidth();
        double scaleX = imageWidth / viewWidth;
        double offsetX = 0;

        if (imageView.getFitWidth() > 0 && viewWidth > imageView.getFitWidth()) {
            offsetX = (viewWidth - imageView.getFitWidth()) / 2;
        }

        return (mouseX - offsetX) * scaleX;
    }

    private double convertToImageY(double mouseY, ImageView imageView) {
        if (imageView.getImage() == null) return mouseY;

        double viewHeight = imageView.getBoundsInLocal().getHeight();
        double imageHeight = imageView.getImage().getHeight();
        double scaleY = imageHeight / viewHeight;
        double offsetY = 0;

        if (imageView.getFitHeight() > 0 && viewHeight > imageView.getFitHeight()) {
            offsetY = (viewHeight - imageView.getFitHeight()) / 2;
        }

        return (mouseY - offsetY) * scaleY;
    }

    public void saveBlurSettings(CustomBlurEventHandlerService.BlurDialogControls blurDialogControls) {

        int blurRadius = (int) blurDialogControls.sldBlur().getValue();
        int brushSize = (int) blurDialogControls.sldBrush().getValue();

        blurDialogControls.sldBlur().setValue(blurRadius);
        blurDialogControls.sldBrush().setValue(brushSize);

        blurDialogControls.lblBlur().setText("Radius: " + blurRadius + "px");
        blurDialogControls.lblBrush().setText(BRUSH_SLIDER_LABEL + brushSize + "px");

        applicationOptionManagerService.saveSettings(blurRadius, brushSize);
    }

    public void applyBlurDialogSettings(CustomBlurEventHandlerService.BlurDialogControls blurDialogControls) {
        blurDialogControls.sldBlur().setValue(MystifyApplication.controlSettingsCache.getBlurLevel());
        blurDialogControls.sldBrush().setValue(MystifyApplication.controlSettingsCache.getBrushSize());

        blurDialogControls.lblBlur().setText("Radius: " + (int) blurDialogControls.sldBlur().getValue() + "px");
        blurDialogControls.lblBrush().setText(BRUSH_SLIDER_LABEL + (int) blurDialogControls.sldBrush().getValue() + "px");
    }

    public void applyFullBlur(MainEventHandlerService eventHandlerService, int blurLevel) {
        if (blurLevel == 0) {
            eventHandlerService.handleResetImage();
        }

        WritableImage copy = eventHandlerService.handleCreateOriginalImageCopy();
        WritableImage newImage = blurEntirePicture(copy, blurLevel);
        eventHandlerService.setCurrentImage(newImage);
        eventHandlerService.getImageView().setImage(newImage);
    }

    public WritableImage blurEntirePicture(Image image, double blurRadius) {
        if (image == null || blurRadius <= 0) {
            return (WritableImage) image;
        }

        ImageView tempView = new ImageView(image);
        tempView.setEffect(new GaussianBlur(blurRadius));

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        WritableImage blurredImage = new WritableImage((int) image.getWidth(), (int) image.getHeight());
        tempView.snapshot(params, blurredImage);

        return blurredImage;
    }
}
