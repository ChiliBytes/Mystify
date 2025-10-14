package com.chilibytes.mystify.core.feature.imageoverlay.service;

import com.chilibytes.mystify.general.service.ImageService;
import com.chilibytes.mystify.general.service.MainEventHandlerService;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageOverlayEventHandlerService {
    private final ImageOverlayService imageOverlayService;
    private final MainEventHandlerService mainEventHandlerService;
    private final ImageService imageService;

    public record ImageOverlayControls(Button btnImagePath, TextArea txtImagePath, Button btnOk, Button btnCancel) {
    }

    public void setupEventHandlers(Stage stage, ImageOverlayControls controls) {

        controls.btnImagePath.setOnAction(e ->
                imageService.loadImage(stage).ifPresent((image -> {
                    controls.txtImagePath.setText(image.getUrl());
                    imageOverlayService.putImage(
                            controls.txtImagePath.getText(),
                            mainEventHandlerService.getImageView()
                    );
                    stage.close();
                })));

        controls.btnOk.setOnAction(e -> {
            imageOverlayService.putImage(
                    controls.txtImagePath.getText(),
                    mainEventHandlerService.getImageView()
            );
            stage.close();
        });
        controls.btnCancel.setOnAction(e -> stage.close());
    }
}
