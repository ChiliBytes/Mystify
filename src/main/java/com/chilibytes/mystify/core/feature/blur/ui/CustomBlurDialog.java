package com.chilibytes.mystify.core.feature.blur.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.blur.service.CustomBlurEventHandlerService;
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
public class CustomBlurDialog extends BaseDialog {

    private final CustomBlurEventHandlerService customBlurEventHandlerService;

    private Label lblBlur;
    private Slider sldBlur;

    private Label lblBrush;
    private Slider sldBrush;

    private static final String BLUR_SLIDER_LABEL = "Blur Level: ";
    private static final String BRUSH_SLIDER_LABEL = "Brush Size: ";


    @Override
    public void configureDialogControls() {
        this.sldBlur = createSlider(0, 10, 0);
        this.sldBrush = createSlider();
        this.lblBlur = createLabel(BLUR_SLIDER_LABEL + (int) this.sldBlur.getValue());
        this.lblBrush = createLabel(BRUSH_SLIDER_LABEL + (int) this.sldBrush.getValue());
    }

    @Override
    public List<VBox> configureDialogLayout() {
        return List.of(
                createVbox(this.lblBlur, this.sldBlur),
                createVbox(this.lblBrush, this.sldBrush)
        );
    }

    @Override
    public void showDialog() {
        this.displayModal("Blur Settings", "Blur Settings", 350, 200);
    }

    @Override
    public void configureEventHandlers() {
        CustomBlurEventHandlerService.BlurDialogControls controls = new CustomBlurEventHandlerService.BlurDialogControls(
                this.lblBlur, this.sldBlur,
                this.lblBrush, this.sldBrush,
                this.getBtnOk(),
                this.getBtnCancel()

        );
        customBlurEventHandlerService.setupEventHandlers(this.getStage(), controls);
    }
}
