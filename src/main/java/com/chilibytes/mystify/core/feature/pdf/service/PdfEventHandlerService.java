package com.chilibytes.mystify.core.feature.pdf.service;

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

    private final PdfMakerService pdfMakerService;
    private static final String PATH_DELIMITER = "/";

    public record PdfMakerDialogControls(Button btnInputFolder, TextArea txtInputFolderPath,
                                         Button btnOutputFolder, TextArea txtOutputFolderPath,
                                         Label lblPdfFileName, TextArea txtPdfFileName,
                                         Button btnCreate, Button btnCancel) {
    }

    private DirectoryChooser createFolderChooser(String chooserText) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(chooserText);
        return directoryChooser;
    }

    public void setupEventHandlers(Stage stage, PdfMakerDialogControls pdfMakerDialogControls) {
        pdfMakerDialogControls.btnInputFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = createFolderChooser("Select Input Folder");
            File selectedFolder = directoryChooser.showDialog(stage);
            Optional.ofNullable(selectedFolder).ifPresent(folder -> {
                String selectedFolderPath = folder.getAbsolutePath() + PATH_DELIMITER;
                pdfMakerDialogControls.txtInputFolderPath.setText(selectedFolderPath);
            });
        });

        pdfMakerDialogControls.btnOutputFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = createFolderChooser("Select Output Folder");
            File selectedFolder = directoryChooser.showDialog(stage);
            Optional.ofNullable(selectedFolder).ifPresent(folder -> {
                String selectedFolderPath = folder.getAbsolutePath() + PATH_DELIMITER;
                pdfMakerDialogControls.txtOutputFolderPath.setText(selectedFolderPath);
            });
        });

        pdfMakerDialogControls.btnCreate.setOnAction(e ->
                pdfMakerService.createPdfFromImages(pdfMakerDialogControls.txtInputFolderPath.getText(),
                        pdfMakerDialogControls.txtOutputFolderPath.getText(),
                        pdfMakerDialogControls.txtPdfFileName.getText())

        );

    }
}
