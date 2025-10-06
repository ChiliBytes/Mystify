package com.chilibytes.mystify.core.feature.pdf.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.pdf.service.PdfEventHandlerService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfMakerDialog extends BaseDialog {

    private final PdfEventHandlerService pdfEventHandlerService;

    private Button btnInputFolder;
    private TextArea txtInputFolderPath;

    private Button btnOutputFolder;
    private TextArea txtOutputFolderPath;

    private Label lblFileName;
    private TextArea txtFileName;

    @Override
    public void configureDialogControls() {
        this.btnInputFolder = createStandardButton("Select Input Folder");
        this.txtInputFolderPath = createTextArea("");

        this.btnOutputFolder = createStandardButton("Select Output Folder");
        this.txtOutputFolderPath = createTextArea("");

        this.lblFileName = createLabel("Resulting File Name");
        this.txtFileName = createTextArea("");

        this.getBtnOk().setText("Create PDF!");
    }

    @Override
    public List<VBox> configureDialogLayout() {
        return List.of(
                createVbox(this.btnInputFolder, this.txtInputFolderPath),
                createVbox(this.btnOutputFolder, this.txtOutputFolderPath),
                createVbox(this.lblFileName, this.txtFileName)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("PDF Create", "Create a PDF File from images");
    }

    @Override
    public void configureEventHandlers() {
        PdfEventHandlerService.PdfMakerDialogControls controls = new PdfEventHandlerService.PdfMakerDialogControls(
                this.btnInputFolder, this.txtInputFolderPath,
                this.btnOutputFolder, this.txtOutputFolderPath,
                this.lblFileName, this.txtFileName,
                this.getBtnOk(), this.getBtnCancel()
        );
        this.pdfEventHandlerService.setupEventHandlers(this.getStage(), controls);
    }
}
