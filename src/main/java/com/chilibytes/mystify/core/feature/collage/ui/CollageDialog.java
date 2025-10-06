package com.chilibytes.mystify.core.feature.collage.ui;


import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.collage.service.CollageEventHandlerService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createTextArea;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createVbox;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class CollageDialog extends BaseDialog {

    private final CollageEventHandlerService collageEventHandlerService;

    private Button btnInputFolder;
    private TextArea txtInputFolder;

    private Button btnOutputFolder;
    private TextArea txtOutputFolder;

    private Label lblResultingFileName;
    private TextArea txtResultingFileName;


    @Override
    public void configureDialogControls() {
        this.btnInputFolder = createStandardButton("Select Input Folder");
        this.txtInputFolder = createTextArea("");

        this.btnOutputFolder = createStandardButton("Select Output Folder");
        this.txtOutputFolder = createTextArea("");

        this.lblResultingFileName = createLabel("Collage Name");
        this.txtResultingFileName = createTextArea("");

        this.getBtnOk().setText("Create Collage");

    }

    @Override
    public List<VBox> configureDialogLayout() {
        return List.of(
                createVbox(this.btnInputFolder, this.txtInputFolder),
                createVbox(this.btnOutputFolder, this.txtOutputFolder),
                createVbox(this.lblResultingFileName, this.txtResultingFileName)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("Collage Maker", "Create a New Collage");
    }

    @Override
    public void configureEventHandlers() {
        CollageEventHandlerService.CollageDialogControls controls = new CollageEventHandlerService.CollageDialogControls(
                this.btnInputFolder, this.txtInputFolder,
                this.btnOutputFolder, this.txtOutputFolder,
                this.lblResultingFileName, this.txtResultingFileName,
                this.getBtnOk(), this.getBtnCancel()
        );
        collageEventHandlerService.setupEventHandlers(this.getStage(), controls);
    }
}
