package com.chilibytes.mystify.core.feature.resize.ui;

import com.chilibytes.mystify.core.BaseDialog;
import com.chilibytes.mystify.core.feature.resize.service.ResizeImageEventHandlerService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createHBox;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createImageView;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createImageViewForThumbnail;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createLabel;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createSlider;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createStandardButton;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createTextArea;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createVbox;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResizeImageDialog extends BaseDialog {

    private Button btnSelectImage;
    private TextArea txtSelectImage;

    private Label lblImageQuality;
    private Slider sldImageQuality;

    private Label lblDimensionsTitle;
    private Label lblDimensionWidth;
    private Label lblDimensionHeight;

    private Label lblPreviewTitle;
    private ImageView imvPreview;

    private final ResizeImageEventHandlerService resizeImageEventHandlerService;

    @Override
    public void configureDialogControls() {
        this.btnSelectImage = createStandardButton("Select image to resize");
        this.txtSelectImage = createTextArea("");

        this.lblImageQuality = createLabel("Resulting image quality (%)");
        this.sldImageQuality = createSlider(0, 98, 98);

        this.lblDimensionsTitle = createLabel("Original image dimensions");
        this.lblDimensionWidth = createLabel("");
        this.lblDimensionHeight = createLabel("");

        this.lblPreviewTitle = createLabel("Selected image prev");
        this.imvPreview = createImageView();
    }

    @Override
    public List<Pane> configureDialogLayout() {

        VBox vbxImageSelection = createVbox(this.btnSelectImage, this.txtSelectImage);
        VBox vbxImageQuality = createVbox(this.lblImageQuality, this.sldImageQuality);
        VBox vbxImageDimensions = createVbox(lblDimensionsTitle, this.lblDimensionWidth, this.lblDimensionHeight);

        VBox vbxLeftGroup = createVbox(vbxImageSelection, vbxImageQuality, vbxImageDimensions);

        VBox vbxRightGroup = createVbox(this.lblPreviewTitle, createImageViewForThumbnail(this.imvPreview));

        HBox hbxLeftSide = createHBox(vbxLeftGroup);
        HBox hbxRightSide = createHBox(vbxRightGroup);
        hbxRightSide.setPadding(new Insets(10, 10, 10, 50));

        HBox hbxFinalGroup = createHBox(hbxLeftSide, hbxRightSide);
        return List.of(hbxFinalGroup);
    }

    @Override
    public void showDialog() {
        this.displayModal("Image Resizer", "Resize Image");
    }

    @Override
    public void configureEventHandlers() {
        ResizeImageEventHandlerService.ResizeImageControl controls = new ResizeImageEventHandlerService.ResizeImageControl(
                btnSelectImage, txtSelectImage, lblImageQuality, sldImageQuality, lblDimensionsTitle, lblDimensionWidth,
                lblDimensionHeight, lblPreviewTitle, imvPreview, getBtnOk(), getBtnCancel()
        );
        resizeImageEventHandlerService.setupEventHandlers(this.getStage(), controls);
    }
}
