package com.chilibytes.mystify.core.feature;

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

import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createModalStage;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createTextArea;

@RequiredArgsConstructor
public abstract class BaseFeatureDialog<S, C> {

    protected final S eventHandlerService;

    protected Button btnSelectInputFolder;
    protected Button btnSelectOutputFolder;
    protected TextArea txtInputFolderPath;
    protected TextArea txtOutputFolderPath;
    protected Label lblFileName;
    protected TextArea txtFileName;
    protected Button btnCreate;
    protected Button btnCancel;

    private static final String CONTROLS_COLOR_STYLE = "-fx-background-color: #2C3E50; -fx-text-fill: white;";

    public void showDialog() {
        Stage featureStage = configureStage(getDialogTitle());
        configureSceneControls();
        configureEventHandlers(featureStage);

        HBox buttonsBox = getButtonsLayout(btnCreate, btnCancel);
        VBox layout = getPrincipalLayout(buttonsBox);

        displayScene(featureStage, layout);
    }

    protected Stage configureStage(String title) {
        return createModalStage(title);
    }

    protected void configureSceneControls() {
        this.btnSelectInputFolder = createStandardButton(getSelectButtonText());
        this.btnSelectOutputFolder = createStandardButton("Select output folder");

        this.txtInputFolderPath = createTextArea("");
        this.txtOutputFolderPath = createTextArea("");

        this.lblFileName = createLabel(getFileNameLabel());
        this.txtFileName = createTextArea("");

        this.btnCreate = createStandardButton(getCreateButtonText());
        this.btnCancel = createStandardButton("Cancel");
    }

    protected HBox getButtonsLayout(Button... buttons) {
        HBox buttonBox = new HBox(20, buttons);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        return buttonBox;
    }

    protected VBox getPrincipalLayout(HBox buttonsBox) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle(CONTROLS_COLOR_STYLE);
        layout.setAlignment(Pos.CENTER);

        VBox inputValuesGroup = new VBox(5, btnSelectInputFolder, txtInputFolderPath);
        inputValuesGroup.setAlignment(Pos.CENTER_LEFT);

        VBox outputValuesGroup = new VBox(5, btnSelectOutputFolder, txtOutputFolderPath);
        outputValuesGroup.setAlignment(Pos.CENTER_LEFT);

        VBox fileNameGroup = new VBox(5, lblFileName, txtFileName);
        fileNameGroup.setAlignment(Pos.CENTER_LEFT);

        Label settingsTitleLabel = createLabel(getDialogTitle());
        layout.getChildren().addAll(settingsTitleLabel, inputValuesGroup, outputValuesGroup, fileNameGroup, buttonsBox);
        return layout;
    }

    protected void displayScene(Stage stage, VBox layout) {
        Scene settingsScene = new Scene(layout);
        stage.setScene(settingsScene);
        stage.showAndWait();
    }

    protected abstract String getFileNameLabel();

    protected abstract String getDialogTitle();

    protected abstract String getSelectButtonText();

    protected abstract String getCreateButtonText();

    protected abstract C createControls();

    protected abstract void configureEventHandlers(Stage stage);
}
