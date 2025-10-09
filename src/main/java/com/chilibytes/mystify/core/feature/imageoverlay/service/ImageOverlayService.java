package com.chilibytes.mystify.core.feature.imageoverlay.service;

import com.chilibytes.mystify.general.service.SharedConstant;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageOverlayService {

    private StackPane parentStackPane;


    public void putImage(String pathToOverlayImage, ImageView imageView) {
        if (!(imageView.getParent() instanceof StackPane)) {
            log.error("Error on putImage(): The target ImageView must be a child of any SackPane object");

        }
        this.parentStackPane = (StackPane) imageView.getParent();
        Image overlayImage = new Image(pathToOverlayImage);
        DragAndResizeService resizableOverlay = new DragAndResizeService(overlayImage);
        parentStackPane.getChildren().add(resizableOverlay);

        //TODO: Will not work if the user continues editing on the same attempt
        SharedConstant.setMultiLayerImage(true);
    }

    public WritableImage unifyImage() {
        ImageView baseImageView = (ImageView) parentStackPane.getChildren().getFirst();
        Image baseImage = baseImageView.getImage();

        double width = baseImage.getWidth();
        double height = baseImage.getHeight();

        // Save the ImageView and it's parent dimensions
        //TODO: If the user zooms the ImageView, Then when saving the image is affected
        double originalMaxWidth = parentStackPane.getMaxWidth();
        double originalMaxHeight = parentStackPane.getMaxHeight();
        double originalPrefWidth = parentStackPane.getPrefWidth();
        double originalPrefHeight = parentStackPane.getPrefHeight();

        WritableImage mergedImage = new WritableImage(
                (int) Math.rint(width),
                (int) Math.rint(height)
        );

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        // Temp dimensions (for merging purposes)
        parentStackPane.setMaxSize(width, height);
        parentStackPane.setPrefSize(width, height);

        this.parentStackPane.snapshot(params, mergedImage);

        // Back the ImageView and it's parent to the original dimensions
        parentStackPane.setMaxSize(originalMaxWidth, originalMaxHeight);
        parentStackPane.setPrefSize(originalPrefWidth, originalPrefHeight);
        return mergedImage;
    }
}
