package com.chilibytes.mystify.core.feature.blur.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.blur.service.AutoBlurEventHandlerService;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoBlurDialog extends BaseDialog {

    private Label lblTitle;
    private Slider sldBlurLevel;

    private final AutoBlurEventHandlerService autoBlurEventHandlerService;

    @Override
    public void configureDialogControls() {
        this.lblTitle = createLabel("Blur Level");
        this.sldBlurLevel = createSlider(0, 100, 0);
    }

    @Override
    public List<VBox> configureDialogLayout() {
        return List.of(
                createVbox(this.lblTitle, this.sldBlurLevel)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("Blur Effect", "Apply Blur to Image", 400, 100);
    }

    @Override
    public void configureEventHandlers() {
        AutoBlurEventHandlerService.AutoBlurDialogControls controls = new AutoBlurEventHandlerService.AutoBlurDialogControls(
                this.lblTitle, this.sldBlurLevel, this.getBtnOk(), this.getBtnCancel()
        );
        autoBlurEventHandlerService.setupEventHandlers(controls);
    }
}
