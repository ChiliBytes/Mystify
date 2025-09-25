package com.chilibytes.mystify.ui.common;

import javafx.scene.control.Button;
import javafx.scene.Node;


public class ButtonDecorator implements ControlDecorator {

    private static final String BUTTON_STYLE = "-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold;";
    private static final String BUTTON_DISABLED_STYLE = "-fx-background-color: #7F8C8D; -fx-text-fill: #BDC3C7; -fx-font-weight: bold;";

    @Override
    public void setEnabledStyle(Node node) {
        Button buttonToDecorate = (Button) node;
        buttonToDecorate.setDisable(false);
        buttonToDecorate.setStyle(BUTTON_STYLE);

    }

    @Override
    public void setDisabledStyle(Node node) {
        Button buttonToDecorate = (Button) node;
        buttonToDecorate.setDisable(true);
        buttonToDecorate.setStyle(BUTTON_DISABLED_STYLE);
    }
}
