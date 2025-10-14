package com.chilibytes.mystify.ui.component;

import com.chilibytes.mystify.core.feature.textoverlay.service.TextOverlayEventHandlerService;
import com.chilibytes.mystify.general.service.MainEventHandlerService;
import com.chilibytes.mystify.general.service.UndoService;
import com.chilibytes.mystify.general.service.ZoomService;
import com.chilibytes.mystify.config.ApplicationProperties;
import com.chilibytes.mystify.ui.common.UIControlCreator;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.chilibytes.mystify.ui.common.UIControlCreator.createImageView;
import static com.chilibytes.mystify.ui.common.UIControlCreator.createScrollPane;

@Slf4j
@Service
@RequiredArgsConstructor
public class UIService {

    private final ZoomService zoomService;
    private final ApplicationProperties applicationProperties;
    private final PrincipalLayoutsBuilder principalLayoutsBuilder;
    private final MenuBarBuilder menuBarBuilder;
    private final MainEventHandlerService mainEventHandlerService;
    private final TextOverlayEventHandlerService textOverlayEventHandlerService;
    private final UndoService undoService;

    private ImageView imageView;

    private static final String APP_ICON_RESOURCE = "/icons/msf.png";

    Button loadButton;
    Button saveButton;
    Button resetButton;
    Button clearButton;
    Button undoButton;
    Button btnTextOverlay;

    public void initializeUI(Stage primaryStage) {
        log.info("Initializing App UI ...");
        log.info("App version code is {} and version name is {}", applicationProperties.getVersionCode(), applicationProperties.getVersionName());
        primaryStage.setTitle(applicationProperties.getName() + applicationProperties.getVersionName());

        VBox controlsPanel = initializeLeftPane();
        imageView = createImageView();
        ScrollPane scrollPane = createScrollPane(imageView);

        MenuBar menuBar = menuBarBuilder.createMenuBar(primaryStage);
        HBox footer = principalLayoutsBuilder.createZoomFooter();

        BorderPane root = buildAllBorderControls(controlsPanel, menuBar, scrollPane, footer);
        buildAndShowScene(primaryStage, root);

        MainEventHandlerService.CommonApplicationButtons commonApplicationButtons = new MainEventHandlerService.CommonApplicationButtons(loadButton, saveButton, resetButton,
                clearButton, undoButton);

        MainEventHandlerService.CommonApplicationControls commonApplicationControls = new MainEventHandlerService.CommonApplicationControls(imageView,
                principalLayoutsBuilder.getOuterZoomSlider());

        mainEventHandlerService.setupEventHandlers(primaryStage, commonApplicationButtons, commonApplicationControls);

        TextOverlayEventHandlerService.TextOverlayControls textOverlayControls = new TextOverlayEventHandlerService.TextOverlayControls(this.btnTextOverlay,
                this.mainEventHandlerService);

        textOverlayEventHandlerService.setupEventHandlers(textOverlayControls);

        applySettingsToControls();
    }

    private VBox initializeLeftPane() {
        this.loadButton = UIControlCreator.createIconButton(FontAwesomeIcon.FOLDER_OPEN, "Load Image");
        this.saveButton = UIControlCreator.createIconButton(FontAwesomeIcon.SAVE, "Save Image");
        this.resetButton = UIControlCreator.createIconButton(FontAwesomeIcon.REFRESH, "Reset Image");
        this.clearButton = UIControlCreator.createIconButton(FontAwesomeIcon.ERASER, "Clear Image");
        this.undoButton = UIControlCreator.createIconButton(FontAwesomeIcon.UNDO, "Undo");
        this.btnTextOverlay = UIControlCreator.createIconButton(FontAwesomeIcon.FILE_WORD_ALT, "Text Overlay");
        undoButton.setDisable(true);
        return principalLayoutsBuilder.createLeftControlsPanel(loadButton, saveButton, resetButton, clearButton, undoButton, btnTextOverlay);
    }

    private BorderPane buildAllBorderControls(VBox leftControlsPane, MenuBar topMenuBar,
                                              ScrollPane centerScroll, HBox bottomFooter) {
        BorderPane root = new BorderPane();
        root.setLeft(leftControlsPane);
        root.setTop(topMenuBar);
        root.setCenter(centerScroll);
        root.setBottom(bottomFooter);
        return root;
    }

    private void buildAndShowScene(Stage stage, BorderPane root) {
        Scene scene = new Scene(root, 1600, 1000);
        setupKeyboardShortcuts(scene);
        loadAppIcon(stage);
        stage.setScene(scene);
        stage.show();
    }

    private void setupKeyboardShortcuts(Scene scene) {
        KeyCombination undoShortcut = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
        scene.getAccelerators().put(undoShortcut, () -> undoService.handleUndo(mainEventHandlerService));
    }

    private void loadAppIcon(Stage primaryStage) {
        java.util.Optional.ofNullable(getClass().getResourceAsStream(APP_ICON_RESOURCE)).ifPresentOrElse(
                inputStream -> primaryStage.getIcons().add(new Image(inputStream)),
                () -> log.warn("The specified application icon could not be loaded, using defaults")
        );
    }

    private void applySettingsToControls() {
        principalLayoutsBuilder.applySettingsToOuterPanels();
        zoomService.applyZoom(imageView);
    }
}
