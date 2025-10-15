package com.chilibytes.mystify.ui.component;

import com.chilibytes.mystify.ui.MystifyApplication;
import com.chilibytes.mystify.ui.common.ButtonDecorator;
import com.chilibytes.mystify.ui.common.ControlDecorator;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createFooter;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createHBox;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createSlider;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createVBoxForLeftPanel;

@Component
@Slf4j
@RequiredArgsConstructor
public class PrincipalLayoutsBuilder {

    private Button undoButtonReference;
    private HBox zoomContainer;
    private VBox leftPaneControls;

    @Getter
    private Slider outerZoomSlider;

    //TODO: Fix the 7+ parameter issue
    public VBox createLeftControlsPanel(Button loadButton, Button saveButton,
                                        Button resetButton, Button clearButton,
                                        Button undoButton, Button btnTextOverlay,
                                        Button btnRotateClockwise, Button btnTest) {

        HBox buttonsRow1 = createHBox(loadButton);
        HBox buttonsRow2 = createHBox(saveButton);
        HBox buttonsRow3 = createHBox(resetButton);
        HBox buttonsRow4 = createHBox(clearButton);
        HBox buttonsRow5 = createHBox(undoButton);
        HBox buttonsRow6 = createHBox(btnTextOverlay);
        HBox buttonsRow7 = createHBox(btnRotateClockwise);
        HBox buttonsRow8 = createHBox(btnTest);

        this.undoButtonReference = undoButton;

        this.leftPaneControls = createVBoxForLeftPanel(buttonsRow1, buttonsRow2,
                buttonsRow3, buttonsRow4,
                buttonsRow5, buttonsRow6,
                buttonsRow7, buttonsRow8);

        return this.leftPaneControls;
    }

    public HBox createZoomFooter() {
        outerZoomSlider = createSlider(0, 300, 100);
        zoomContainer = createFooter(outerZoomSlider);
        return zoomContainer;
    }

    public void updateUndoPanelButtonState(boolean canUndo) {
        ControlDecorator buttonDecorator = new ButtonDecorator();
        Optional.ofNullable(undoButtonReference).ifPresent(button -> {
            if (canUndo) {
                buttonDecorator.setEnabledStyle(button);
                return;
            }
            buttonDecorator.setDisabledStyle(button);
        });
    }

    public void applySettingsToOuterPanels() {

        leftPaneControls.setPadding(new Insets(MystifyApplication.controlSettingsCache.getLeftPanePadding()));
        leftPaneControls.setMinWidth(MystifyApplication.controlSettingsCache.getLeftPaneMinWidth());
        leftPaneControls.setMaxWidth(MystifyApplication.controlSettingsCache.getLeftPaneMaxWidth());
        zoomContainer.setMinHeight(MystifyApplication.controlSettingsCache.getFooterMinHeight());
        zoomContainer.setMaxHeight(MystifyApplication.controlSettingsCache.getFooterMaxHeight());
    }
}
