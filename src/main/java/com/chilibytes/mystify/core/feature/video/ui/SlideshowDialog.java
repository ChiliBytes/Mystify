package com.chilibytes.mystify.core.feature.video.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.video.service.SlideshowEventHandlerService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createTextArea;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createVbox;

@Component
@Slf4j
@RequiredArgsConstructor
public class SlideshowDialog extends BaseDialog {
    private final SlideshowEventHandlerService slideshowEventHandlerService;

    private Button btnInputFolder;
    private TextArea txtInputFolder;

    private Button btnOutputFolder;
    private TextArea txtOutputFolder;

    private Label lblSecondsBetween;
    private TextArea txtSecondsBetween;

    private Label lblVideoFileName;
    private TextArea txtVideoFileName;

    @Override
    public void configureDialogControls() {
        this.btnInputFolder = createStandardButton("Select Input Folder");
        this.txtInputFolder = createTextArea("");

        this.btnOutputFolder = createStandardButton("Select Output Folder");
        this.txtOutputFolder = createTextArea("");

        this.lblSecondsBetween = createLabel("Seconds between images");
        this.txtSecondsBetween = createTextArea("");

        this.lblVideoFileName = createLabel("Resulting video file name");
        this.txtVideoFileName = createTextArea("");

        this.getBtnOk().setText("Create Slideshow");
    }

    @Override
    public List<VBox> configureDialogLayout() {
        return List.of(
                createVbox(this.btnInputFolder, this.txtInputFolder),
                createVbox(this.btnOutputFolder, this.txtOutputFolder),
                createVbox(this.lblSecondsBetween, this.txtSecondsBetween),
                createVbox(this.lblVideoFileName, this.txtVideoFileName)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("Slideshow", "Slideshow creator from images");
    }

    @Override
    public void configureEventHandlers() {
        SlideshowEventHandlerService.SlideshowControls controls = new SlideshowEventHandlerService.SlideshowControls(
                this.btnInputFolder, this.txtInputFolder,
                this.btnOutputFolder, this.txtOutputFolder,
                this.lblSecondsBetween, this.txtSecondsBetween,
                this.lblVideoFileName, this.txtVideoFileName,
                this.getBtnOk(), this.getBtnCancel()
        );
        slideshowEventHandlerService.configureEventHandlers(this.getStage(), controls);
    }
}
