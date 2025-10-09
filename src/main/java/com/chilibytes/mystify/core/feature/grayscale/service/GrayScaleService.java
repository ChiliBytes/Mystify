package com.chilibytes.mystify.core.feature.grayscale.service;

import com.chilibytes.mystify.general.service.CommonEventHandlerService;
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

    public void grayScaleImage(CommonEventHandlerService commonEventHandlerService,
                               double grayScaleFactor) {

        if (grayScaleFactor >= 0 && grayScaleFactor <= 10) {
            commonEventHandlerService.handleResetImage();
            return;
        }

        grayScaleFactor = grayScaleFactor / 100;
        WritableImage imageCopy = commonEventHandlerService.handleCreateOriginalImageCopy();
        ImageView imageView = new ImageView(imageCopy);

        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(-1.0);
        colorAdjust.setBrightness((grayScaleFactor - 0.5) * 0.5);

        imageView.setEffect(colorAdjust);
        SnapshotParameters params = new SnapshotParameters();
        WritableImage grayScaledImage = imageView.snapshot(params, imageCopy);

        commonEventHandlerService.setCurrentImage(grayScaledImage);
        commonEventHandlerService.getImageView().setImage(grayScaledImage);
    }
}
