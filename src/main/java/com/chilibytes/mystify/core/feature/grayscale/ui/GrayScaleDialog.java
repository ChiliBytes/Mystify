package com.chilibytes.mystify.core.feature.grayscale.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.grayscale.service.GrayScaleEventHandlerService;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createSlider;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createVbox;

@Component
@Slf4j
@RequiredArgsConstructor
public class GrayScaleDialog extends BaseDialog {

    private static final int MIN_GRAY_SCALE_FACTOR = -250;
    private static final int MAX_GRAY_SCALE_FACTOR = 250;

    private Label lblScaleFactor;
    private Slider sldScaleFactor;
    private final GrayScaleEventHandlerService grayScaleEventHandlerService;

    @Override
    public void configureDialogControls() {
        this.lblScaleFactor = createLabel("Scale Factor");
        this.sldScaleFactor = createSlider(MIN_GRAY_SCALE_FACTOR, MAX_GRAY_SCALE_FACTOR, 0);
    }

    @Override
    public List<VBox> configureDialogLayout() {
        return List.of(
                createVbox(lblScaleFactor, sldScaleFactor)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("Gray-Scale Image", "Setup Gray-Scale", 400, 100);
    }

    @Override
    public void configureEventHandlers() {
        GrayScaleEventHandlerService.GrayScaleControls controls = new GrayScaleEventHandlerService.GrayScaleControls(
                lblScaleFactor, sldScaleFactor, getBtnOk(), getBtnCancel()
        );
        grayScaleEventHandlerService.setupEventHandlers(this.getStage(), controls);
    }
}
