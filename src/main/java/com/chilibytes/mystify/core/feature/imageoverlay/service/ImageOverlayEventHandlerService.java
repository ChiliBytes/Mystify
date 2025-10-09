package com.chilibytes.mystify.core.feature.imageoverlay.service;

import com.chilibytes.mystify.general.service.CommonEventHandlerService;
import com.chilibytes.mystify.general.service.FileService;
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
    private final CommonEventHandlerService commonEventHandlerService;
    private final FileService fileService;

    public record ImageOverlayControls(Button btnImagePath, TextArea txtImagePath, Button btnOk, Button btnCancel) {
    }

    public void setupEventHandlers(Stage stage, ImageOverlayControls controls) {

        controls.btnImagePath.setOnAction(e ->
                fileService.loadImage(stage).ifPresent((image -> {
                    controls.txtImagePath.setText(image.getUrl());
                    imageOverlayService.putImage(
                            controls.txtImagePath.getText(),
                            commonEventHandlerService.getImageView()
                    );
                    stage.close();
                })));

        controls.btnOk.setOnAction(e -> {
            imageOverlayService.putImage(
                    controls.txtImagePath.getText(),
                    commonEventHandlerService.getImageView()
            );
            stage.close();
        });
        controls.btnCancel.setOnAction(e -> stage.close());
    }
}
