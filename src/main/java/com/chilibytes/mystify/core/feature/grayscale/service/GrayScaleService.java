package com.chilibytes.mystify.core.feature.grayscale.service;

import com.chilibytes.mystify.general.service.MainEventHandlerService;
import javafx.scene.SnapshotParameters;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GrayScaleService {

    public void grayScaleImage(MainEventHandlerService mainEventHandlerService,
                               double grayScaleFactor) {

        if (grayScaleFactor >= 0 && grayScaleFactor <= 10) {
            mainEventHandlerService.handleResetImage();
            return;
        }

        grayScaleFactor = grayScaleFactor / 100;
        WritableImage imageCopy = mainEventHandlerService.handleCreateOriginalImageCopy();
        ImageView imageView = new ImageView(imageCopy);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1.0);
        colorAdjust.setBrightness((grayScaleFactor - 0.5) * 0.5);

        imageView.setEffect(colorAdjust);
        SnapshotParameters params = new SnapshotParameters();
        WritableImage grayScaledImage = imageView.snapshot(params, imageCopy);

        mainEventHandlerService.setCurrentImage(grayScaledImage);
        mainEventHandlerService.getImageView().setImage(grayScaledImage);
    }
}
