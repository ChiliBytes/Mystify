package com.chilibytes.mystify.core.feature.imageoverlay.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.imageoverlay.service.ImageOverlayEventHandlerService;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createTextArea;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createVbox;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageOverlayDialog extends BaseDialog {

    private Button btnImagePath;
    private TextArea txtImagePath;

    private final ImageOverlayEventHandlerService imageOverlayEventHandlerService;

    @Override
    public void configureDialogControls() {
        this.btnImagePath = createStandardButton("Select the image to overlay");
        this.txtImagePath = createTextArea("");
    }

    @Override
    public List<Pane> configureDialogLayout() {
        return List.of(createVbox(
                btnImagePath,
                txtImagePath));
    }

    @Override
    public void showDialog() {
        this.displayModal("Overlay Image", 600, 100);
    }

    @Override
    public void configureEventHandlers() {
        ImageOverlayEventHandlerService.ImageOverlayControls controls = new ImageOverlayEventHandlerService.ImageOverlayControls(
                this.btnImagePath, this.txtImagePath, this.getBtnOk(), this.getBtnCancel()
        );
        imageOverlayEventHandlerService.setupEventHandlers(this.getStage(), controls);
    }
}
