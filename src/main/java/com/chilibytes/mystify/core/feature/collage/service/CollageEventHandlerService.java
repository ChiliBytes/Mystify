package com.chilibytes.mystify.core.feature.collage.service;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollageEventHandlerService {

    private final CollageMakerService collageMakerService;

    private static final String PATH_DELIMITER = "/";

    public record CollageDialogControls(Button btnSelectInputFolder, TextArea txtInputFolderPath
            , Button btnSelectOutputFolder, TextArea txtOutputFolderPath,
                                        Label lblCollageName, TextArea txtCollageName,
                                        Button btnCreate, Button btnCancel) {
    }

    private DirectoryChooser createFolderChooser(String chooserText) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(chooserText);
        return directoryChooser;
    }

    public void setupEventHandlers(Stage stage, CollageDialogControls collageDialogControls) {

        collageDialogControls.btnSelectInputFolder.setOnAction(e -> {
            DirectoryChooser folderChooser = createFolderChooser("Select Input Folder");
            File selectedFolder = folderChooser.showDialog(stage);
            if (selectedFolder != null) {
                String selectedInputFolderPath = selectedFolder.getAbsolutePath() + PATH_DELIMITER;
                collageDialogControls.txtInputFolderPath.setText(selectedInputFolderPath);
            }
        });

        collageDialogControls.btnSelectOutputFolder.setOnAction(e -> {
            DirectoryChooser folderChooser = createFolderChooser("Select Output Folder");
            File selectedFolder = folderChooser.showDialog(stage);
            if (selectedFolder != null) {
                String selectedOutputFolderPath = selectedFolder.getAbsolutePath() + PATH_DELIMITER;
                collageDialogControls.txtOutputFolderPath.setText(selectedOutputFolderPath);
            }
        });

        collageDialogControls.btnCreate.setOnAction(e -> collageMakerService.createCollage(collageDialogControls.txtInputFolderPath.getText(),
                collageDialogControls.txtOutputFolderPath.getText(),
                collageDialogControls.txtCollageName.getText()));

        collageDialogControls.btnCancel.setOnAction(e -> stage.close());
    }
}
