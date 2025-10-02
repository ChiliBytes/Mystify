package com.chilibytes.mystify.core.feature.video.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.video.service.SlideshowEventHandler;
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
    private final SlideshowEventHandler slideshowEventHandler;

    private Button btnInputFolder;
    private TextArea txtInputFolder;

    private Button btnOutputFolder;
    private TextArea txtOutputFolder;

    private Label lblVideoFileName;
    private TextArea txtVideoFileName;

    @Override
    public void configureDialogControls() {
        this.btnInputFolder = createStandardButton("Select Input Folder");
        this.txtInputFolder = createTextArea("");

        this.btnOutputFolder = createStandardButton("Select Output Folder");
        this.txtOutputFolder = createTextArea("");

        this.lblVideoFileName = createLabel("Resulting video file name");
        this.txtVideoFileName = createTextArea("");
    }

    @Override
    public List<VBox> configureDialogLayout() {
        return List.of(
                createVbox(this.btnInputFolder, this.txtInputFolder),
                createVbox(this.btnOutputFolder, this.txtOutputFolder),
                createVbox(this.lblVideoFileName, this.txtVideoFileName)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("Slideshow", "Slideshow creator from images");
    }

    @Override
    public void configureEventHandlers() {
        SlideshowEventHandler.SlideshowControls controls = new SlideshowEventHandler.SlideshowControls(
                btnInputFolder, txtInputFolder,
                btnOutputFolder, txtOutputFolder,
                lblVideoFileName, txtVideoFileName
        );
        slideshowEventHandler.configureEventHandlers(this.getStage(), controls);
    }
}
