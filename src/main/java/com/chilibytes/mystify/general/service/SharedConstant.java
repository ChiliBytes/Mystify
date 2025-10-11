package com.chilibytes.mystify.general.service;

import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

public class SharedConstant {

    private SharedConstant() {

    }

    @Getter
    @Setter
    private static boolean multiLayerImage = false;

    @Getter
    @Setter
    private static String globalFontStyle = "";

    @Getter
    @Setter
    private static String globalFontFamily = "Arial";

    @Getter
    @Setter
    private static int globalFontSize = 12;

    @Getter
    @Setter
    private static Color globalFontColor = Color.BLACK;
}


