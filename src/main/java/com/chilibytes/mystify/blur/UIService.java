package com.chilibytes.mystify.blur;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class UIService {

    private final ImageProcessor imageProcessor;
    private final FileService fileService;
    private final UndoService undoService;
    private final ZoomService zoomService;
    private final SettingsService settingsService;

    private ImageView imageView;
    private Image originalImage;
    private WritableImage currentImage;
    private Slider blurSlider;
    private Slider brushSlider;
    private Label radiusLabel;
    private Label brushSizeLabel;
    private int brushSize = 20;
    private int blurRadius = 15;
    private Slider zoomSlider;
    private Label zoomLabel;
    private Button undoButtonReference;
    private boolean isDragging = false;

    private static final String CONTROLS_COLOR_STYLE = "-fx-background-color: #2C3E50; -fx-text-fill: white;";
    private static final String BUTTON_STYLE = "-fx-background-color: #E67E22; -fx-text-fill: white; -fx-font-weight: bold;";
    private static final String BUTTON_DISABLED_STYLE = "-fx-background-color: #7F8C8D; -fx-text-fill: #BDC3C7; -fx-font-weight: bold;";
    private static final String LABEL_STYLE = "-fx-text-fill: #ECF0F1; -fx-font-weight: bold;";
    private static final String APP_TITLE = "ChilliBytes - Mystify Image Editor 1.0";
    private static final String ZOOM_VALUE_TEXT = "Zoom: %.0f%%";

    private static final String BLUR_SLIDER_LABEL = "Radius: ";
    private static final String BRUSH_SLIDER_LABEL = "Brush Size: ";


    public UIService(ImageProcessor imageProcessor, FileService fileService,
                     UndoService undoService, ZoomService zoomService,
                     SettingsService settingsService) {
        this.imageProcessor = imageProcessor;
        this.fileService = fileService;
        this.undoService = undoService;
        this.zoomService = zoomService;
        this.settingsService = settingsService;
    }

    public void initializeUI(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);

        var loadButton = createIconButton(FontAwesomeIcon.FOLDER_OPEN, "Load Image");
        var saveButton = createIconButton(FontAwesomeIcon.SAVE, "Save Image");
        var resetButton = createIconButton(FontAwesomeIcon.REFRESH, "Reset Image");
        var clearButton = createIconButton(FontAwesomeIcon.ERASER, "Clear Image");
        var undoButton = createIconButton(FontAwesomeIcon.UNDO, "Undo");
        undoButton.setDisable(true);
        this.undoButtonReference = undoButton;

        configureSliders();
        imageView = createImageView();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        StackPane centerPane = new StackPane();
        centerPane.getChildren().add(imageView);
        StackPane.setAlignment(imageView, Pos.CENTER);
        scrollPane.setContent(centerPane);

        // Creating the left pane
        var controlsPanel = createControlsPanel(loadButton, saveButton, resetButton, clearButton, undoButton);

        // Creating the main upper menu
        MenuBar menuBar = createMenuBar(primaryStage);
        menuBar.useSystemMenuBarProperty().set(false);

        // Creating the footer alongside the zoom slider
        HBox footer = createZoomFooter();

        var root = new BorderPane();
        root.setTop(menuBar);
        root.setLeft(controlsPanel);
        root.setCenter(scrollPane);
        root.setBottom(footer);

        Scene scene = new Scene(root, 1600, 1000);

        // Configuring shortcut events
        setupKeyboardShortcuts(scene);

        primaryStage.setScene(scene);
        primaryStage.show();

        setupEventHandlers(primaryStage, loadButton, saveButton, resetButton, clearButton, undoButton);
        setupZoomHandlers();

        // Load configurations
        loadSettings();
    }

    private HBox createZoomFooter() {
        zoomSlider.setMinWidth(200);
        zoomSlider.setMaxWidth(300);

        HBox zoomContainer = new HBox(10, zoomSlider, zoomLabel);
        zoomContainer.setAlignment(Pos.CENTER_RIGHT);
        zoomContainer.setPadding(new Insets(10));
        zoomContainer.setStyle(CONTROLS_COLOR_STYLE);

        return zoomContainer;
    }

    private MenuBar createMenuBar(Stage primaryStage) {
        MenuItem loadImageItem = new MenuItem("Load Image");
        loadImageItem.setOnAction(e -> handleLoadImage(primaryStage));

        MenuItem saveImageItem = new MenuItem("Save Image");
        saveImageItem.setOnAction(e -> handleSaveImage(primaryStage));

        Menu fileMenu = new Menu("File");
        fileMenu.getItems().addAll(loadImageItem, saveImageItem);

        MenuItem resetImageItem = new MenuItem("Reset Image");
        resetImageItem.setOnAction(e -> handleResetImage());

        MenuItem clearImageItem = new MenuItem("Clear Image");
        clearImageItem.setOnAction(e -> handleClearImage());

        Menu editMenu = new Menu("Edit");
        editMenu.getItems().addAll(resetImageItem, clearImageItem);

        Menu settingsMenu = new Menu("Settings");
        MenuItem levelsItem = new MenuItem("Levels");
        levelsItem.setOnAction(e -> showSettingsDialog());
        settingsMenu.getItems().add(levelsItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, editMenu, settingsMenu);

        return menuBar;
    }

    private VBox createControlsPanel(Button loadButton, Button saveButton, Button resetButton, Button clearButton, Button undoButton) {
        var buttonsRow1 = new HBox(10, loadButton);
        buttonsRow1.setAlignment(Pos.BASELINE_LEFT);

        var buttonsRow2 = new HBox(10, saveButton);
        buttonsRow2.setAlignment(Pos.BASELINE_LEFT);

        var buttonsRow3 = new HBox(10, resetButton);
        buttonsRow3.setAlignment(Pos.BASELINE_LEFT);

        var buttonsRow4 = new HBox(10, clearButton);
        buttonsRow4.setAlignment(Pos.BASELINE_LEFT);

        var buttonsRow5 = new HBox(10, undoButton);
        buttonsRow5.setAlignment(Pos.BASELINE_LEFT);

        var controls = new VBox(12, buttonsRow1, buttonsRow2, buttonsRow3, buttonsRow4, buttonsRow5);

        controls.setPadding(new Insets(15));
        controls.setStyle(CONTROLS_COLOR_STYLE);
        controls.setMinWidth(80);
        controls.setMaxWidth(150);

        return controls;
    }

    private Button createIconButton(FontAwesomeIcon icon, String tooltipText) {
        var button = new Button();

        FontAwesomeIconView iconView = new FontAwesomeIconView(icon);
        iconView.setSize("24px");
        iconView.setFill(Color.WHITE);

        button.setGraphic(iconView);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setMinHeight(50);

        Tooltip tooltip = new Tooltip(tooltipText);
        Tooltip.install(button, tooltip);

        return button;
    }

    private Label createStyledLabel(String text) {
        var label = new Label(text);
        label.setStyle(LABEL_STYLE);
        return label;
    }

    private ImageView createImageView() {
        var view = new ImageView();
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    private void configureSliders() {
        blurSlider = new Slider(1, 50, 15);
        configureSlider(blurSlider);
        radiusLabel = createStyledLabel(BLUR_SLIDER_LABEL + (int) blurSlider.getValue() + "px");

        brushSlider = new Slider(5, 100, 20);
        configureSlider(brushSlider);
        brushSizeLabel = createStyledLabel(BRUSH_SLIDER_LABEL + brushSize + "px");

        zoomSlider = new Slider(10, 400, 100);
        configureSlider(zoomSlider);
        zoomLabel = createStyledLabel("Zoom: 100%");
    }

    private void configureSlider(Slider slider) {
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(10);
        slider.setBlockIncrement(5);
        slider.setMinWidth(150);
        slider.setMaxWidth(150);
    }

    private void setupKeyboardShortcuts(Scene scene) {
        KeyCombination undoShortcut = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(undoShortcut, this::handleUndo);
    }

    private void setupEventHandlers(Stage stage, Button loadButton, Button saveButton, Button resetButton, Button clearButton, Button undoButton) {
        loadButton.setOnAction(e -> handleLoadImage(stage));
        saveButton.setOnAction(e -> handleSaveImage(stage));
        resetButton.setOnAction(e -> handleResetImage());
        clearButton.setOnAction(e -> handleClearImage());
        undoButton.setOnAction(e -> handleUndo());

        blurSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            blurRadius = newVal.intValue();
            radiusLabel.setText(BLUR_SLIDER_LABEL + blurRadius + "px");
        });

        brushSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            brushSize = newVal.intValue();
            brushSizeLabel.setText(BRUSH_SLIDER_LABEL + brushSize + "px");
        });

        imageView.setOnMouseDragged(this::handleApplyBlur);
        imageView.setOnMouseClicked(this::handleApplyBlur);
        imageView.setOnMouseReleased(e -> isDragging = false);
        imageView.setOnMouseExited(e -> isDragging = false);

        undoButton.setVisible(false);
    }

    private void setupZoomHandlers() {
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            zoomService.setZoomLevel(newVal.doubleValue() / 100.0);
            zoomService.applyZoom(imageView);
            zoomLabel.setText(String.format(ZOOM_VALUE_TEXT, newVal.doubleValue()));
        });
    }

    public void handleLoadImage(Stage stage) {
        Optional<Image> loadedImage = fileService.loadImage(stage);
        loadedImage.ifPresent(this::processLoadedImage);
    }

    public void handleSaveImage(Stage stage) {
        if (currentImage != null) {
            boolean saved = fileService.saveImage(stage, currentImage, fileService.getDefaultExtension());
            if (saved) {
                showAlert("Success", "Image saved successfully");
            }
        } else {
            showAlert("Warning", "No images to save found!");
        }
    }

    public void handleApplyBlur(MouseEvent event) {
        if (currentImage != null) {
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED ||
                    (event.getEventType() == MouseEvent.MOUSE_DRAGGED && !isDragging)) {
                undoService.saveState(currentImage);
                isDragging = true;
            }
            double imageX = convertToImageX(event.getX());
            double imageY = convertToImageY(event.getY());

            if (imageProcessor.isWithinImageBounds(currentImage, (int) imageX, (int) imageY)) {
                imageProcessor.applyCircularBlur(currentImage, (int) imageX, (int) imageY, brushSize, blurRadius);
            }
        }
    }

    public void handleUndo() {
        if (undoService.canUndo()) {
            currentImage = undoService.undo(currentImage);
            imageView.setImage(currentImage);
            updateUndoButtonState();
        }
    }

    public void handleResetImage() {
        if (originalImage != null) {
            undoService.saveState(currentImage);
            currentImage = imageProcessor.createWritableImageCopy(originalImage);
            imageView.setImage(currentImage);
        }
    }

    public void handleClearImage() {
        if (currentImage != null) {
            undoService.saveState(currentImage);
            currentImage = imageProcessor.createBlankImage((int) currentImage.getWidth(), (int) currentImage.getHeight());
            imageView.setImage(currentImage);
        }
    }

    private double convertToImageX(double mouseX) {
        if (imageView.getImage() == null) return mouseX;

        double viewWidth = imageView.getBoundsInLocal().getWidth();
        double imageWidth = imageView.getImage().getWidth();
        double scaleX = imageWidth / viewWidth;
        double offsetX = 0;

        if (imageView.getFitWidth() > 0 && viewWidth > imageView.getFitWidth()) {
            offsetX = (viewWidth - imageView.getFitWidth()) / 2;
        }

        return (mouseX - offsetX) * scaleX;
    }

    private double convertToImageY(double mouseY) {
        if (imageView.getImage() == null) return mouseY;

        double viewHeight = imageView.getBoundsInLocal().getHeight();
        double imageHeight = imageView.getImage().getHeight();
        double scaleY = imageHeight / viewHeight;
        double offsetY = 0;

        if (imageView.getFitHeight() > 0 && viewHeight > imageView.getFitHeight()) {
            offsetY = (viewHeight - imageView.getFitHeight()) / 2;
        }

        return (mouseY - offsetY) * scaleY;
    }

    private void processLoadedImage(Image image) {
        originalImage = image;
        currentImage = imageProcessor.createWritableImageCopy(image);
        imageView.setImage(currentImage);
        zoomService.resetZoom();
        zoomService.applyZoom(imageView);
        undoService.clearHistory();
        updateUndoButtonState();
    }

    private void updateUndoButtonState() {
        if (undoButtonReference != null) {
            if (undoService.canUndo()) {
                undoButtonReference.setDisable(false);
                undoButtonReference.setStyle(BUTTON_STYLE);
            } else {
                undoButtonReference.setDisable(true);
                undoButtonReference.setStyle(BUTTON_DISABLED_STYLE);
            }
        }
    }

    private void loadSettings() {
        SettingsService.Settings settings = settingsService.loadSettings();
        blurRadius = settings.getBlurLevel();
        brushSize = settings.getBrushSize();
        zoomService.setZoomLevel(settings.getZoomLevel());

        blurSlider.setValue(blurRadius);
        brushSlider.setValue(brushSize);
        zoomSlider.setValue(settings.getZoomLevel() * 100);

        radiusLabel.setText(BLUR_SLIDER_LABEL + blurRadius + "px");
        brushSizeLabel.setText(BRUSH_SLIDER_LABEL + brushSize + "px");
        zoomLabel.setText(String.format(ZOOM_VALUE_TEXT, settings.getZoomLevel() * 100));

        zoomService.applyZoom(imageView);
    }

    private void showSettingsDialog() {

        // Create the modal dialog
        Stage settingsStage = new Stage();
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Settings - Levels");
        settingsStage.setMinWidth(350);
        settingsStage.setMinHeight(320);

        // Use existing sliders but create copies for the dialog so that the original values are not modified until "OK" is pressed.
        Slider blurDialogSlider = createDialogSlider(blurSlider);
        Slider brushDialogSlider = createDialogSlider(brushSlider);
        Slider zoomDialogSlider = createDialogSlider(zoomSlider);

        // Set the current values
        blurDialogSlider.setValue(blurRadius);
        brushDialogSlider.setValue(brushSize);
        zoomDialogSlider.setValue(zoomService.getZoomLevel() * 100);

        // Labels for display the configured values
        Label blurLabel = new Label("Blur Level: " + (int) blurDialogSlider.getValue());
        blurLabel.setStyle(LABEL_STYLE);

        Label brushLabel = new Label(BRUSH_SLIDER_LABEL + (int) brushDialogSlider.getValue());
        brushLabel.setStyle(LABEL_STYLE);

        Label zoomValueLabel = new Label("Zoom: " + (int) zoomDialogSlider.getValue() + "%");
        zoomValueLabel.setStyle(LABEL_STYLE);

        // Listeners for update the labels text
        blurDialogSlider.valueProperty().addListener((obs, oldVal, newVal) -> blurLabel.setText("Blur Level: " + newVal.intValue()));
        brushDialogSlider.valueProperty().addListener((obs, oldVal, newVal) -> brushLabel.setText(BRUSH_SLIDER_LABEL + newVal.intValue()));
        zoomDialogSlider.valueProperty().addListener((obs, oldVal, newVal) -> zoomValueLabel.setText("Zoom: " + newVal.intValue() + "%"));

        // Styled buttons
        Button acceptButton = new Button("Ok");
        acceptButton.setStyle(BUTTON_STYLE);
        acceptButton.setMinWidth(80);

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(BUTTON_STYLE);
        cancelButton.setMinWidth(80);

        acceptButton.setOnAction(e -> {
            // Apply changes to the main sliders
            applySettingsFromDialog(blurDialogSlider, brushDialogSlider, zoomDialogSlider);
            settingsStage.close();
        });

        cancelButton.setOnAction(e -> settingsStage.close());

        // Create a container for the buttons
        HBox buttonBox = new HBox(20, acceptButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));

        // principal Layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle(CONTROLS_COLOR_STYLE);
        layout.setAlignment(Pos.CENTER);

        // Group every control with its label
        VBox blurGroup = new VBox(5, blurLabel, blurDialogSlider);
        blurGroup.setAlignment(Pos.CENTER_LEFT);

        VBox brushGroup = new VBox(5, brushLabel, brushDialogSlider);
        brushGroup.setAlignment(Pos.CENTER_LEFT);

        VBox zoomGroup = new VBox(5, zoomValueLabel, zoomDialogSlider);
        zoomGroup.setAlignment(Pos.CENTER_LEFT);

        layout.getChildren().addAll(
                new Label("Level Settings"),
                blurGroup,
                brushGroup,
                zoomGroup,
                buttonBox
        );

        Scene settingsScene = new Scene(layout);
        settingsStage.setScene(settingsScene);
        settingsStage.showAndWait();
    }

    // Create copy sliders based on the originals
    private Slider createDialogSlider(Slider originalSlider) {
        Slider dialogSlider = new Slider(originalSlider.getMin(), originalSlider.getMax(), originalSlider.getValue());
        dialogSlider.setShowTickLabels(originalSlider.isShowTickLabels());
        dialogSlider.setShowTickMarks(originalSlider.isShowTickMarks());
        dialogSlider.setMajorTickUnit(originalSlider.getMajorTickUnit());
        dialogSlider.setBlockIncrement(originalSlider.getBlockIncrement());
        dialogSlider.setMinWidth(250);
        dialogSlider.setMaxWidth(250);
        return dialogSlider;
    }

    // Apply the changes from the dialog
    private void applySettingsFromDialog(Slider blurDialogSlider, Slider brushDialogSlider, Slider zoomDialogSlider) {
        // Save the values
        blurRadius = (int) blurDialogSlider.getValue();
        brushSize = (int) brushDialogSlider.getValue();
        double newZoomLevel = zoomDialogSlider.getValue() / 100.0;
        zoomService.setZoomLevel(newZoomLevel);

        // Update principal sliders
        blurSlider.setValue(blurRadius);
        brushSlider.setValue(brushSize);
        zoomSlider.setValue(newZoomLevel * 100);

        // Update labels
        radiusLabel.setText(BLUR_SLIDER_LABEL + blurRadius + "px");
        brushSizeLabel.setText(BRUSH_SLIDER_LABEL + brushSize + "px");
        zoomLabel.setText(String.format(ZOOM_VALUE_TEXT, newZoomLevel * 100));

        // Apply zoom
        zoomService.applyZoom(imageView);

        // Persist the configuration
        settingsService.saveSettings(blurRadius, brushSize, newZoomLevel);
    }

    private void showAlert(String title, String message) {
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

