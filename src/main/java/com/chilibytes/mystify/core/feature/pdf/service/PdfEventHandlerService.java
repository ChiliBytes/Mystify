package com.chilibytes.mystify.core.feature.pdf.service;

import com.chilibytes.mystify.core.feature.pdf.PdfMaker;
import com.chilibytes.mystify.core.feature.pdf.ui.PdfMakerDialog;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfEventHandlerService {

    private final PdfMaker pdfMaker;
    private static final String PATH_DELIMITER = "/";

    public record PdfMakerDialogControls(Button btnSelectInputFolder, Button btnSelectOutputFolder,
                                         TextArea txtInputFolderPath, TextArea txtOutputFolderPath,
                                         Label lblPdfFileName, TextArea txtPdfFileName,
                                         Button btnCreate, Button btnCancel) {
    }

    public void handleOpenCollageStage(PdfMakerDialog pdfMakerDialog) {
        pdfMakerDialog.showDialog();
    }

    private DirectoryChooser createFolderChooser(String chooserText) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(chooserText);
        return directoryChooser;
    }

    public void setupEventHandlers(Stage stage, PdfMakerDialogControls pdfMakerDialogControls) {
        pdfMakerDialogControls.btnSelectInputFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = createFolderChooser("Select Input Folder");
            File selectedFolder = directoryChooser.showDialog(stage);
            Optional.ofNullable(selectedFolder).ifPresent(folder -> {
                String selectedFolderPath = folder.getAbsolutePath() + PATH_DELIMITER;
                pdfMakerDialogControls.txtInputFolderPath.setText(selectedFolderPath);
            });
        });

        pdfMakerDialogControls.btnSelectOutputFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = createFolderChooser("Select Output Folder");
            File selectedFolder = directoryChooser.showDialog(stage);
            Optional.ofNullable(selectedFolder).ifPresent(folder -> {
                String selectedFolderPath = folder.getAbsolutePath() + PATH_DELIMITER;
                pdfMakerDialogControls.txtOutputFolderPath.setText(selectedFolderPath);
            });
        });

        pdfMakerDialogControls.btnCreate.setOnAction(e ->
                pdfMaker.createPdfFromImages(pdfMakerDialogControls.txtInputFolderPath.getText(),
                        pdfMakerDialogControls.txtOutputFolderPath.getText(),
                        pdfMakerDialogControls.txtPdfFileName.getText())

        );

        pdfMakerDialogControls.btnCancel.setOnAction(e -> stage.close());
    }
}
