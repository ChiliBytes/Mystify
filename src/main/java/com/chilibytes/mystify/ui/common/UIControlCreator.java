package com.chilibytes.mystify.ui.common;

import com.chilibytes.mystify.config.ApplicationProperties;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.annotation.PostConstruct;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UIControlCreator {

    private final ApplicationProperties applicationProperties;

    private static final String LABEL_STYLE = "-fx-text-fill: #ECF0F1; -fx-font-weight: bold;";
    private static final String CONTROLS_COLOR_STYLE = "-fx-background-color: #2C3E50; -fx-text-fill: white;";
    private static final String BUTTON_STYLE = "-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold;";
    private static final int DEFAULT_SLIDER_WIDTH = 300;
    private static final int DEFAULT_TEXTBOX_HEIGHT = 28;
    private static final int DEFAULT_COLOR_PICKER_WIDTH = 250;
    private static final int DEFAULT_COMBO_BOX_WIDTH = 100;
    private static final int DEFAULT_IMAGE_PREVIEW_SIZE = 300;

    // Adding this initialization so we can access the non-static field applicationProperties from a custom holder
    @PostConstruct
    public void init() {
        NonStaticApplicationPropertiesHolder.setApplicationProperties(applicationProperties);
    }

    public static ImageView createImageView() {
        ImageView view = new ImageView();
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    public static ScrollPane createScrollPane(ImageView imageView) {
        StackPane centerPane = new StackPane();
        centerPane.getChildren().add(imageView);
        StackPane.setAlignment(imageView, javafx.geometry.Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(centerPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        return scrollPane;
    }

    public static Button createIconButton(FontAwesomeIcon icon, String tooltipText) {
        ApplicationProperties applicationProperties = NonStaticApplicationPropertiesHolder.getApplicationProperties();

        Button button = new Button();

        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize(applicationProperties.getAppControlLeftPanelButtonSize());
        iconView.setFill(Color.WHITE);

        button.setGraphic(iconView);
        button.setStyle(applicationProperties.getAppControlLeftPanelButtonStyle());
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(50);

        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(button, tooltip);
        return button;
    }

    public static Button createStandardButton(String buttonText) {
        Button button = new Button(buttonText);
        button.setStyle(BUTTON_STYLE);
        button.setMinWidth(80);
        return button;
    }

    public static Slider createSlider(double min, double max, double value, double width) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(Boolean.TRUE);
        slider.setShowTickMarks(Boolean.FALSE);
        slider.setMajorTickUnit(10);
        slider.setBlockIncrement(5);
        slider.setMinWidth(width);
        slider.setMaxWidth(width);
        return slider;
    }

    public static Slider createSlider(double min, double max, double value) {
        return createSlider(min, max, value, DEFAULT_SLIDER_WIDTH);
    }

    public static Slider createSlider() {
        return createSlider(0, 100, 50, DEFAULT_SLIDER_WIDTH);
    }

    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle(LABEL_STYLE);
        return label;
    }

    public static Label createRawLabel(String text) {
        return new Label(text);
    }

    public static TextArea createReadOnlyTextArea(String text) {
        TextArea textArea = new TextArea(text);
        textArea.setEditable(Boolean.FALSE);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        return textArea;
    }

    public static TextArea createTextArea(String text) {
        return createTextArea(text, 400, DEFAULT_TEXTBOX_HEIGHT);
    }

    public static TextArea createTextArea(String text, double width, double height) {
        TextArea textArea = createReadOnlyTextArea(text);
        textArea.setEditable(Boolean.TRUE);
        textArea.setWrapText(true);
        textArea.setMinWidth(width);
        textArea.setMaxWidth(width);
        textArea.setMinHeight(height);
        textArea.setMaxHeight(height);
        return textArea;
    }


    public static ComboBox<String> createComboBox() {
        return createComboBox(DEFAULT_COMBO_BOX_WIDTH);
    }

    public static ComboBox<String> createComboBox(double width) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMinWidth(width);
        comboBox.setMaxWidth(width);
        return comboBox;
    }

    public static ColorPicker createColorPicker(Color defaultColor) {
        ColorPicker colorPicker = new ColorPicker(defaultColor);
        colorPicker.setMinWidth(DEFAULT_COLOR_PICKER_WIDTH);
        colorPicker.setMaxWidth(DEFAULT_COLOR_PICKER_WIDTH);
        return colorPicker;
    }

    public static ColorPicker createColorPicker(Color defaultColor, double width) {
        ColorPicker colorPicker = new ColorPicker(defaultColor);
        colorPicker.setMinWidth(width);
        colorPicker.setMaxWidth(width);
        return colorPicker;
    }


    public static HBox createFooter(Slider zoomSlider) {
        HBox zoomContainer = new HBox(10, zoomSlider);
        zoomContainer.setAlignment(Pos.CENTER_RIGHT);
        zoomContainer.setPadding(new Insets(10));
        zoomContainer.setStyle(CONTROLS_COLOR_STYLE);
        zoomContainer.setMinHeight(35);
        zoomContainer.setMaxHeight(45);

        return zoomContainer;
    }

    public static HBox createHBox(Node... nodes) {
        HBox newRow = new HBox(10, nodes);
        newRow.setAlignment(Pos.BASELINE_LEFT);
        return newRow;
    }

    public static VBox createVBoxForLeftPanel(Node... nodes) {
        VBox leftPaneControls = new VBox(12, nodes);
        leftPaneControls.setPadding(new Insets(15));
        leftPaneControls.setStyle(CONTROLS_COLOR_STYLE);
        leftPaneControls.setMinWidth(80);
        leftPaneControls.setMaxWidth(150);
        return leftPaneControls;
    }

    private static class NonStaticApplicationPropertiesHolder {
        @Getter
        @Setter
        private static ApplicationProperties applicationProperties;
    }

    public static Stage createModalStage(String modalTitle, int width, int height) {
        Stage modalStage = new Stage();
        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.setTitle(modalTitle);
        modalStage.setMinWidth(width);
        modalStage.setWidth(width);
        modalStage.setMinHeight(height);
        return modalStage;

    }

    public static Stage createModalStage(String modalTitle) {
        return createModalStage(modalTitle, 850, 320);

    }

    public static VBox createVbox(Node... nodes) {
        VBox dialogGroup = new VBox(5, nodes);
        dialogGroup.setAlignment(Pos.CENTER_LEFT);
        return dialogGroup;
    }

    public static Pane createImageViewForThumbnail(ImageView originalImageView) {
        Pane pnImgContainer = new Pane();

        originalImageView.setPreserveRatio(true);
        originalImageView.fitWidthProperty().bind(pnImgContainer.widthProperty());
        originalImageView.fitHeightProperty().bind(pnImgContainer.heightProperty());

        pnImgContainer.getChildren().add(originalImageView);
        pnImgContainer.setMaxSize(DEFAULT_IMAGE_PREVIEW_SIZE, DEFAULT_IMAGE_PREVIEW_SIZE);
        return pnImgContainer;
    }
}
