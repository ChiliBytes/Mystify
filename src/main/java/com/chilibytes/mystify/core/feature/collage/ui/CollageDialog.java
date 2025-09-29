package com.chilibytes.mystify.core.feature.collage.ui;

import com.chilibytes.mystify.core.feature.collage.service.CollageEventHandlerService;
import com.chilibytes.mystify.ui.common.UIControlCreator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createTextArea;

@Slf4j
@Component
@RequiredArgsConstructor
public class CollageDialog {

    private final CollageEventHandlerService collageEventHandlerService;

    private Button selectInputFolder;
    private Button selectOutputFolder;

    private TextArea inputFolderPath;
    private TextArea outputFolderPath;

    private Button createButton;
    private Button cancelButton;

    private Label collageNameLabel;
    private TextArea collageNameText;

    private static final String COLLAGE_NAME_LABEL = "Collage Name";
    private static final String CONTROLS_COLOR_STYLE = "-fx-background-color: #2C3E50; -fx-text-fill: white;";

    public void showCollageDialog() {
        Stage collageStage = configureCollageStage();
        configureSceneControls();

        CollageEventHandlerService.CollageDialogControls collageControls = new CollageEventHandlerService.CollageDialogControls(
                this.createButton, this.cancelButton, this.selectInputFolder,
                this.inputFolderPath, this.selectOutputFolder, this.outputFolderPath, this.collageNameText);

        collageEventHandlerService.setupEventHandlers(collageStage, collageControls);

        HBox buttonsBox = getButtonsLayout(createButton, cancelButton);
        VBox layout = getPrincipalLayout(buttonsBox);

        Scene settingsScene = new Scene(layout);
        collageStage.setScene(settingsScene);
        collageStage.showAndWait();

    }

    private Stage configureCollageStage() {
        return UIControlCreator.createModalStage("Collage Maker", 850, 320);
    }

    private void configureSceneControls() {
        this.selectInputFolder = createStandardButton("Select source images folder");
        this.selectOutputFolder = createStandardButton("Select output folder");
        this.createButton = createStandardButton("Create Collage");
        this.cancelButton = createStandardButton("Cancel");
        this.inputFolderPath = createTextArea("");
        this.outputFolderPath = createTextArea("");
        this.collageNameLabel = createLabel(COLLAGE_NAME_LABEL);
        this.collageNameText = createTextArea("");
    }

    private HBox getButtonsLayout(Button... buttons) {
        HBox buttonBox = new HBox(20, buttons);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        return buttonBox;
    }

    private VBox getPrincipalLayout(HBox buttonsBox) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle(CONTROLS_COLOR_STYLE);
        layout.setAlignment(Pos.CENTER);

        VBox inputValuesGroup = new VBox(5, selectInputFolder, inputFolderPath);
        inputValuesGroup.setAlignment(Pos.CENTER_LEFT);

        VBox outputValuesGroup = new VBox(5, selectOutputFolder, outputFolderPath);
        outputValuesGroup.setAlignment(Pos.CENTER_LEFT);

        VBox collageNameGroup = new VBox(5, collageNameLabel, collageNameText);
        outputValuesGroup.setAlignment(Pos.CENTER_LEFT);

        Label settingsTitleLabel = createLabel("Create Collage");
        layout.getChildren().addAll(settingsTitleLabel, inputValuesGroup, outputValuesGroup, collageNameGroup, buttonsBox);
        return layout;
    }
}
