package com.chilibytes.mystify.core.feature.textoverlay.service;

import com.chilibytes.mystify.core.feature.blur.service.BlurProcessorService;
import com.chilibytes.mystify.general.service.MainEventHandlerService;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TextOverlayEventHandlerService {

    private final TextOverlayService textOverlayService;
    private final BlurProcessorService blurProcessorService;
    private final MainEventHandlerService mainEventHandlerService;

    public record TextOverlayControls(Button btnActivateOverlay,
                                      MainEventHandlerService mainEventHandlerService) {
    }

    public void setupEventHandlers(TextOverlayControls controls) {
        // When entering the text mode, we don't want to apply blur but write a text over the image.
        mainEventHandlerService.getImageView().setOnMouseDragged(e -> {
            if (!textOverlayService.isTextModeActive()) {
                blurProcessorService.handleApplyBlur(e, mainEventHandlerService);
            }
        });

        mainEventHandlerService.getImageView().setOnMouseClicked(e -> {
            if (textOverlayService.isTextModeActive()) {
                textOverlayService.handleImageClickForText(e, mainEventHandlerService);
            } else {
                blurProcessorService.handleApplyBlur(e, mainEventHandlerService);
            }
        });

        controls.btnActivateOverlay.setOnAction(e -> {
            textOverlayService.setTextModeActive(true);
            log.info("Text Mode Activated. Click on the image to add text.");
        });
    }
}
