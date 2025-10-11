package com.chilibytes.mystify.core.feature.textoverlay.service;

import com.chilibytes.mystify.general.service.CommonEventHandlerService;
import com.chilibytes.mystify.general.service.SharedConstant;
import com.chilibytes.mystify.general.service.UndoService;
import com.chilibytes.mystify.ui.component.PrincipalLayoutsBuilder;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TextOverlayService {

    private Point2D lastClickPosition = null;
    private TextField activeTextField = null;

    private final PrincipalLayoutsBuilder principalLayoutsBuilder;
    @Getter
    @Setter
    private boolean textModeActive;

    private final UndoService undoService;

    public void handleImageClickForText(MouseEvent event,
                                        CommonEventHandlerService commonEventHandlerService) {
        // Saving the last click relative position from the ImageView
        this.lastClickPosition = new Point2D(event.getX(), event.getY());

        // Deactivating the text mode after the click
        textModeActive = false;

        drawText(commonEventHandlerService);
    }

    public void drawText(CommonEventHandlerService commonEventHandlerService) {
        // Ensuring the ImageView is contained inside a Pane/StackPane in order to overlay the text
        if (commonEventHandlerService.getCurrentImage() == null || lastClickPosition == null || !(commonEventHandlerService.getImageView().getParent() instanceof Pane imageContainer)) {
            log.error("Could not draw text: currentImage is null, no click position, or ImageView parent is not a Pane.");
            return;
        }

        // 1. Remove a previous TextField (If exists) to avoid multiple active TextFields
        if (activeTextField != null) {
            imageContainer.getChildren().remove(activeTextField);
        }

        // 2. Create the TextField
        TextField textField = new TextField();
        textField.setPromptText("Your text here...");
        textField.setPrefWidth(200);
        textField.setMaxWidth(200);

        // Setting up the position based on the user's click (relative position to the ImageView)
        textField.setLayoutX(lastClickPosition.getX() - (textField.getPrefWidth() / 2));
        textField.setLayoutY(lastClickPosition.getY());

        // Store the active TextField reference
        this.activeTextField = textField;

        // 3. Finalizing text when ENTER is pressed
        textField.setOnAction(e -> finalizeText(textField, imageContainer, commonEventHandlerService));

        // 4. Finalizing the text when the TextField loss the focus
        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            // Lost focus
            if (!newVal) {
                finalizeText(textField, imageContainer, commonEventHandlerService);
            }
        });

        // 5. Adding the TextField to the ImageView container
        imageContainer.getChildren().add(textField);

        // 6. Active the text focus again so the user can write another text immediately
        textField.requestFocus();
    }

    private void finalizeText(TextField textField, Pane imageContainer,
                              CommonEventHandlerService commonEventHandlerService) {

        WritableImage currentImage = commonEventHandlerService.getCurrentImage();
        ImageView imageView = commonEventHandlerService.getImageView();

        // Process the text only if the TextFiled is active and contains text
        if (textField.getText() != null && !textField.getText().trim().isEmpty() && textField == activeTextField) {

            // 1. Save the current status (For Undo)
            undoService.saveState(currentImage);

            // 2. Creating a temp canvas to draw the text using the original image dimensions (to prevent a bad scaling)
            Canvas canvas = new Canvas(currentImage.getWidth(), currentImage.getHeight());
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // 3. Draw the current image on the canvas
            gc.drawImage(currentImage, 0, 0);

            // 4. Text configurations
            gc.setFill(SharedConstant.getGlobalFontColor());
            gc.setFont(Font.font(SharedConstant.getGlobalFontFamily(), SharedConstant.getGlobalFontSize()));
            gc.setTextAlign(TextAlignment.LEFT);

            // 5.  Get the text position in the original imaged (Not Zoomed),
            // We need the scale relation of the ImageView to the WritableImage
            double imageWidth = currentImage.getWidth();

            double imageViewFitWidth = imageView.getBoundsInLocal().getWidth();

            // Avoiding a Zero-Division error and using 1.0 if dimensions are not available
            double scaleFactor = (imageViewFitWidth > 0 && imageWidth > 0) ? (imageWidth / imageViewFitWidth) : 1.0;

            // Multiply the lastClickPosition by the scaling-factor to get the position with the WritableImage
            double drawX = lastClickPosition.getX() * scaleFactor;

            // +30 So the text will be placed under the TextArea control
            double drawY = (lastClickPosition.getY() + 30) * scaleFactor;

            // 6. Drawing the text
            gc.fillText(textField.getText(), drawX, drawY);

            // 7. Get back the Canvas into a WritableImage
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);

            // 8. Updating currentImage and ImageView objects
            currentImage = canvas.snapshot(params, null);

            commonEventHandlerService.setCurrentImage(currentImage);
            imageView.setImage(currentImage);

            // 9. Updating the Undo button
            principalLayoutsBuilder.updateUndoPanelButtonState(undoService.canUndo());
        }

        // 10. Clean and remove the TextField from the container
        imageContainer.getChildren().remove(textField);
        if (textField == activeTextField) {
            activeTextField = null;
        }

        // 11. Back to the normal mode
        textModeActive = false;
    }
}
