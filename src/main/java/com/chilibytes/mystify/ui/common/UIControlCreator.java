package com.chilibytes.mystify.ui.common;

import com.chilibytes.mystify.config.ApplicationProperties;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.annotation.PostConstruct;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    public static Slider createSlider(double min, double max, double value, double mindWidth, double maxWidth) {
        Slider slider = new Slider(min, max, value);
        slider.setShowTickLabels(Boolean.TRUE);
        slider.setShowTickMarks(Boolean.FALSE);
        slider.setMajorTickUnit(10);
        slider.setBlockIncrement(5);
        slider.setMinWidth(mindWidth);
        slider.setMaxWidth(maxWidth);
        return slider;
    }

    public static Slider createSlider(double min, double max, double value) {
        return createSlider(min, max, value, 250, 250);
    }

    public static Slider createSlider() {
        return createSlider(0, 100, 50, 300, 300);
    }

    public static Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle(LABEL_STYLE);
        return label;
    }

    public static Label createRawLabel(String text) {
        return new Label(text);
    }

    public static TextArea createRawTextArea(String text) {
        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        return textArea;
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

    public static HBox createHBoxForLeftPanel(Node nodes) {
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
}
