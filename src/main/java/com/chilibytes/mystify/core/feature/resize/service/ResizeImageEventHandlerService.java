package com.chilibytes.mystify.core.feature.resize.service;

import com.chilibytes.mystify.general.service.ImageService;
import com.chilibytes.mystify.general.service.MainEventHandlerService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResizeImageEventHandlerService {

    private final ImageService imageService;
    private final MainEventHandlerService mainEventHandlerService;

    public record ResizeImageControl(Button btnSelectImage, TextArea txtSelectImage,
                                     Label lblImageQuality, Slider sldImageQuality,
                                     Label lblDimensionsTitle, Label lblDimensionWidth,
                                     Label lblDimensionHeight, Label lblPreviewTitle,
                                     ImageView imvPreview, Button btnOk, Button btnCancel) {
    }

    public void setupEventHandlers(Stage stage, ResizeImageControl resizeImageControls) {
        resizeImageControls.btnSelectImage.setOnAction(e -> {
            Optional<Image> loadedImage = imageService.loadImage(stage);
            loadedImage.ifPresent(image -> {
                resizeImageControls.txtSelectImage.setText(ImageService.getImagePath(image));
                resizeImageControls.imvPreview.setImage(image);
                resizeImageControls.lblDimensionHeight.setText("Height: " + image.getHeight() + " Px");
                resizeImageControls.lblDimensionWidth.setText("Width: " + image.getWidth() + " Px");
            });
        });

        resizeImageControls.btnOk.setOnAction(e -> {
            WritableImage downgradedImage = imageService.downgradeImage(
                    resizeImageControls.txtSelectImage.getText(),
                    (int) resizeImageControls.sldImageQuality.getValue());

            mainEventHandlerService.setCurrentImage(downgradedImage);
            mainEventHandlerService.getImageView().setImage(downgradedImage);
            stage.close();
        });

        resizeImageControls.btnCancel.setOnAction(e -> stage.close());
    }
}
