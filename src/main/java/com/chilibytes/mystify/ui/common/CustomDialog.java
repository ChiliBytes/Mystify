package com.chilibytes.mystify.ui.common;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createRawLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createRawTextArea;

public class CustomDialog {

    private CustomDialog() {

    }

    public static void showSuccess(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showSuccess(String message) {
        showSuccess("Success", message);
    }

    public static void showError(String title, String message) {
        var alert = createSimpleErrorDialog(title, message);
        alert.showAndWait();
    }

    public static void showError(String message) {
        showError("Error", message);
    }

    public static void showError(String title, String message, Throwable exception) {
        Alert alert = createDetailedErrorDialog(title, message);


        Optional.ofNullable(exception).ifPresent(thrownException -> {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            thrownException.printStackTrace(pw);
            String exceptionText = sw.toString();

            Label label = createRawLabel("Error Details");
            TextArea textArea = createRawTextArea(exceptionText);

            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expContent = new GridPane();
            expContent.setMaxWidth(Double.MAX_VALUE);
            expContent.add(label, 0, 0);
            expContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expContent);
            alert.getDialogPane().setExpanded(false);
            alert.getDialogPane().expandedProperty().addListener(
                    (obs, wasExpanded, isNowExpanded) -> alert.getDialogPane().requestLayout());
        });

        alert.showAndWait();
    }

    public static Alert createDetailedErrorDialog(String title, String message) {
        Alert exceptionAlert = createSimpleErrorDialog(title, message);
        exceptionAlert.setHeaderText(null);
        return exceptionAlert;
    }

    private static Alert createSimpleErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert;
    }
}
