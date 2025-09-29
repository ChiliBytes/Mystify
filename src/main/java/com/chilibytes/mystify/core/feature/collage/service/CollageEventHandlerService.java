package com.chilibytes.mystify.core.feature.collage.service;

import com.chilibytes.mystify.core.feature.collage.CollageMaker;
import com.chilibytes.mystify.core.feature.collage.ui.CollageDialog;
import javafx.scene.control.Button;
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

    private final CollageMaker collageMaker;

    private String selectedInputFolderPath;
    private String selectedOutputFolderPath;

    private static final String PATH_DELIMITER = "/";

    public record CollageDialogControls(Button createButton, Button cancelButton,
                                        Button selectInputFolder, TextArea inputFolderPath,
                                        Button selectOutputFolder, TextArea outputFolderPath, TextArea collageName) {
    }


    public void handleOpenCollageStage(CollageDialog collageDialog) {
        collageDialog.showCollageDialog();
    }

    private DirectoryChooser createFolderChooser(String chooserText) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(chooserText);
        return directoryChooser;
    }

    public void setupEventHandlers(Stage stage, CollageDialogControls collageDialogButtons) {

        collageDialogButtons.selectInputFolder.setOnAction(e -> {
            DirectoryChooser folderChooser = createFolderChooser("Select Input Folder");
            File selectedFolder = folderChooser.showDialog(stage);
            if (selectedFolder != null) {
                this.selectedInputFolderPath = selectedFolder.getAbsolutePath() + PATH_DELIMITER;
                collageDialogButtons.inputFolderPath.setText(this.selectedInputFolderPath);
            }
        });

        collageDialogButtons.selectOutputFolder.setOnAction(e -> {
            DirectoryChooser folderChooser = createFolderChooser("Select Output Folder");
            File selectedFolder = folderChooser.showDialog(stage);
            if (selectedFolder != null) {
                this.selectedOutputFolderPath = selectedFolder.getAbsolutePath() + PATH_DELIMITER;
                collageDialogButtons.outputFolderPath.setText(this.selectedOutputFolderPath);
            }
        });

        collageDialogButtons.createButton.setOnAction(e -> collageMaker.createCollage(collageDialogButtons.inputFolderPath.getText(),
                collageDialogButtons.outputFolderPath.getText(),
                collageDialogButtons.collageName.getText()));

        collageDialogButtons.cancelButton.setOnAction(e -> stage.close());
    }
}
