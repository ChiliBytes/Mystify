package com.chilibytes.mystify.core.feature.textoverlay.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.textoverlay.service.FontSettingsEventHandlerService;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createColorPicker;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createComboBox;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createHBox;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createSlider;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createTextArea;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createVbox;

@Component
@Slf4j
@RequiredArgsConstructor
public class FontSettingsDialog extends BaseDialog {

    private final FontSettingsEventHandlerService fontSettingsEventHandlerService;

    private Label lblFontFamily;
    private ComboBox<String> cbxFontFamily;

    private Label lblFontSize;
    private Slider sldFontSize;

    private Label lblFontColor;
    private ColorPicker cpFontColor;

    private Label lblPreview;
    private TextArea txtPreview;

    @Override
    public void configureDialogControls() {
        this.lblFontFamily = createLabel("Select the Font Family");
        this.cbxFontFamily = createComboBox(250);

        this.lblFontSize = createLabel("Font Size");
        this.sldFontSize = createSlider(0, 50, 12, 300);

        this.lblFontColor = createLabel("Font Color");
        this.cpFontColor = createColorPicker(Color.WHITE);

        this.lblPreview = createLabel("Live Preview");
        this.txtPreview = createTextArea("Live preview of your settings", 820, 50);
    }

    @Override
    public List<Pane> configureDialogLayout() {
        HBox fontSizeAndColor = createHBox(
                createVbox(this.lblFontFamily, this.cbxFontFamily),
                createVbox(this.lblFontColor, this.cpFontColor),
                createVbox(this.lblFontSize, this.sldFontSize)

        );
        return List.of(
                createVbox(fontSizeAndColor),
                createVbox(this.lblPreview, this.txtPreview)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("Font Settings", "Font Setting Parameters");
    }

    @Override
    public void configureEventHandlers() {
        FontSettingsEventHandlerService.FontSettingsControls controls = new FontSettingsEventHandlerService.FontSettingsControls(
                lblFontFamily, cbxFontFamily, lblFontSize, sldFontSize, lblFontColor,
                cpFontColor, lblPreview, txtPreview, this.getBtnOk(), this.getBtnCancel()
        );
        fontSettingsEventHandlerService.setupEventHandlers(this.getStage(), controls);
    }
}
