package com.chilibytes.mystify;

import com.chilibytes.mystify.ui.MystifyApplication;
import javafx.stage.Stage;

public class Main extends javafx.application.Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MystifyApplication app = new MystifyApplication();
        app.start(primaryStage);
    }
}
