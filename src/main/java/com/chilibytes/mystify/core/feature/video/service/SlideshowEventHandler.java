package com.chilibytes.mystify.core.feature.video.service;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class SlideshowEventHandler {

    private static final String PATH_DELIMITER = "/";

    public record SlideshowControls(Button btnInputFolder, TextArea txtInputFolder,
                                    Button btnOutputFolder, TextArea txtOutputFolder,
                                    Label lblVideoFileName, TextArea txtVideoFileName) {

    }

    public void configureEventHandlers(Stage stage, SlideshowControls controls) {
        controls.btnInputFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = createFolderChooser("Select Input Folder");
            File selectedFolder = directoryChooser.showDialog(stage);
            Optional.ofNullable(selectedFolder).ifPresent(folder -> {
                String selectedFolderPath = folder.getAbsolutePath() + PATH_DELIMITER;
                controls.txtInputFolder.setText(selectedFolderPath);
            });
        });

        controls.btnOutputFolder.setOnAction(e -> {
            DirectoryChooser directoryChooser = createFolderChooser("Select Output Folder");
            File selectedFolder = directoryChooser.showDialog(stage);
            Optional.ofNullable(selectedFolder).ifPresent(folder -> {
                String selectedFolderPath = folder.getAbsolutePath() + PATH_DELIMITER;
                controls.txtOutputFolder.setText(selectedFolderPath);
            });
        });
    }

    private DirectoryChooser createFolderChooser(String chooserText) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(chooserText);
        return directoryChooser;
    }
}
