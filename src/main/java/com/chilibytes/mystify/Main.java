package com.chilibytes.mystify;

import com.chilibytes.mystify.ui.ImageBlur;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ImageBlur app = new ImageBlur();
        app.start(primaryStage);
    }
}