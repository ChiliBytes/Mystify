package com.chilibytes.mystify.core;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createModalStage;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;

@Component
@Slf4j
@RequiredArgsConstructor
public abstract class BaseDialog {

    private Button btnCreate;
    private Button btnCancel;

    @Getter
    private Stage stage;

    private static final String CONTROLS_COLOR_STYLE = "-fx-background-color: #2C3E50; -fx-text-fill: white;";

    public void displayModal(String windowTitle, String dialogTitle) {
        this.configureDialogControls();
        Stage dialogStage = createModalStage(windowTitle, 850, 320);
        configureFooterButtons();

        this.stage = dialogStage;
        this.configureEventHandlers();

        VBox layout = createDialog(dialogTitle);

        Scene settingsScene = new Scene(layout);
        dialogStage.setScene(settingsScene);

        this.btnCancel.setOnAction(e -> dialogStage.close());

        dialogStage.showAndWait();
    }

    public void displayModal(String windowTitle) {
       this.displayModal(windowTitle, windowTitle);
    }

    private void configureFooterButtons() {
        this.btnCreate = createStandardButton("Ok");
        this.btnCancel = createStandardButton("Cancel");
    }

    private HBox configureFooterLayout(Button... buttons) {
        HBox buttonBox = new HBox(20, buttons);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        return buttonBox;
    }

    private VBox createDialog(String dialogTitle) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle(CONTROLS_COLOR_STYLE);
        layout.setAlignment(Pos.CENTER);

        HBox footerButtonsBox = configureFooterLayout(this.btnCreate, this.btnCancel);
        Label dialogLabel = createLabel(dialogTitle);

        List<Node> dialogNodes = new ArrayList<>();
        dialogNodes.add(dialogLabel);
        dialogNodes.addAll(this.configureDialogLayout());
        dialogNodes.add(footerButtonsBox);

        dialogNodes.forEach(node -> layout.getChildren().addAll(node));

        return layout;
    }

    public abstract void configureDialogControls();
    public abstract List<VBox> configureDialogLayout();
    public abstract void showDialog();
    public abstract void configureEventHandlers();

}
