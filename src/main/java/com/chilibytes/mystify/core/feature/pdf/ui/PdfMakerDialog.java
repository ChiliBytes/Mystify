package com.chilibytes.mystify.core.feature.pdf.ui;

import com.chilibytes.mystify.core.feature.BaseFeatureDialog;
import com.chilibytes.mystify.core.feature.pdf.service.PdfEventHandlerService;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class PdfMakerDialog extends BaseFeatureDialog<PdfEventHandlerService, PdfEventHandlerService.PdfMakerDialogControls> {

    private final String createButtonText = "Create PDF";
    private final String fileNameLabel = "PDF file Name";
    private final String dialogTitle = "Image to PDF Maker";
    private final String selectButtonText = "Select source images folder";

    public PdfMakerDialog(PdfEventHandlerService pdfEventHandlerService) {
        super(pdfEventHandlerService);
    }

    @Override
    protected PdfEventHandlerService.PdfMakerDialogControls createControls() {
        return new PdfEventHandlerService.PdfMakerDialogControls(
                btnSelectInputFolder,
                btnSelectOutputFolder,
                txtInputFolderPath,
                txtOutputFolderPath,
                lblFileName,
                txtFileName,
                btnCreate,
                btnCancel
        );
    }

    @Override
    protected void configureEventHandlers(Stage pdfMakerStage) {
        PdfEventHandlerService.PdfMakerDialogControls pdfControls = createControls();
        eventHandlerService.setupEventHandlers(pdfMakerStage, pdfControls);
    }
}
