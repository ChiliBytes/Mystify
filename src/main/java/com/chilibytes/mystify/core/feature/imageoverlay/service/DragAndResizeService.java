package com.chilibytes.mystify.core.feature.imageoverlay.service;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DragAndResizeService extends Group {

    public enum ResizeHandle {
        NONE, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST,
        NORTH, SOUTH, EAST, WEST
    }

    private final ImageView imageView;
    private Point2D lastMousePosition;
    private ResizeHandle activeHandle = ResizeHandle.NONE;
    private Point2D dragAnchorPoint;

    private static final double GRIP_SIZE = 6;

    public DragAndResizeService(Image image) {
        this.imageView = new ImageView(image);
        this.getChildren().add(this.imageView);

        // Initialize the ImageView size (Mandatory for the resizing feature)
        this.imageView.setFitWidth(image.getWidth());
        this.imageView.setFitHeight(image.getHeight());
        this.imageView.setPreserveRatio(true);

        //This is important to allow resizing on transparent-background images
        this.imageView.setPickOnBounds(true);

        setupInteractionEvents();
    }

    private void setupInteractionEvents() {
        this.setOnMousePressed(event -> {
            lastMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
            activeHandle = getHandle(event.getX(), event.getY());

            if (activeHandle == ResizeHandle.NONE && event.getButton() == MouseButton.PRIMARY) {
                // Drag and Drop event start
                dragAnchorPoint = new Point2D(event.getSceneX() - this.getTranslateX(),
                        event.getSceneY() - this.getTranslateY());
            }
            event.consume();
        });

        this.setOnMouseDragged(event -> {
            if (activeHandle != ResizeHandle.NONE) {
                // Resizing logic
                double mouseDeltaX = event.getSceneX() - lastMousePosition.getX();
                double mouseDeltaY = event.getSceneY() - lastMousePosition.getY();

                switch (activeHandle) {
                    case NORTH -> resizeNorth(mouseDeltaY);
                    case SOUTH -> resizeSouth(mouseDeltaY);
                    case EAST -> resizeEast(mouseDeltaX);
                    case WEST -> resizeWest(mouseDeltaX);
                    case NORTH_WEST -> {
                        resizeNorth(mouseDeltaY);
                        resizeWest(mouseDeltaX);
                    }
                    case NORTH_EAST -> {
                        resizeNorth(mouseDeltaY);
                        resizeEast(mouseDeltaX);
                    }
                    case SOUTH_WEST -> {
                        resizeSouth(mouseDeltaY);
                        resizeWest(mouseDeltaX);
                    }
                    case SOUTH_EAST -> {
                        resizeSouth(mouseDeltaY);
                        resizeEast(mouseDeltaX);
                    }
                    default -> log.warn("Unknown position");

                }

                // Update the cursor position for the next resizing event
                lastMousePosition = new Point2D(event.getSceneX(), event.getSceneY());

            } else if (dragAnchorPoint != null && event.getButton() == MouseButton.PRIMARY) {
                // Dragging logic
                this.setTranslateX(event.getSceneX() - dragAnchorPoint.getX());
                this.setTranslateY(event.getSceneY() - dragAnchorPoint.getY());
            }

            event.consume();
        });

        this.setOnMouseReleased(event -> {
            // Finalize resizing and dragging
            activeHandle = ResizeHandle.NONE;
            dragAnchorPoint = null;
            event.consume();
        });

        this.setOnMouseMoved(event -> {
            ResizeHandle handle = getHandle(event.getX(), event.getY());
            // Set up the cursor, arrow if we are in a border, otherwise use the hand.
            switch (handle) {
                case NORTH, SOUTH -> this.setCursor(Cursor.N_RESIZE);
                case EAST, WEST -> this.setCursor(Cursor.E_RESIZE);
                case NORTH_WEST, SOUTH_EAST -> this.setCursor(Cursor.NW_RESIZE);
                case NORTH_EAST, SOUTH_WEST -> this.setCursor(Cursor.NE_RESIZE);
                // Using the hand if we are not resizing
                default -> this.setCursor(Cursor.HAND);
            }
            event.consume();
        });
    }

    // Resize methods by point
    private void resizeNorth(double deltaY) {
        double newHeight = imageView.getFitHeight() - deltaY;
        if (newHeight > 10) {
            imageView.setFitHeight(newHeight);
            this.setTranslateY(this.getTranslateY() + deltaY);
        }
    }

    private void resizeSouth(double deltaY) {
        double newHeight = imageView.getFitHeight() + deltaY;
        if (newHeight > 10) {
            imageView.setFitHeight(newHeight);
        }
    }

    private void resizeEast(double deltaX) {
        double newWidth = imageView.getFitWidth() + deltaX;
        if (newWidth > 10) {
            imageView.setFitWidth(newWidth);
        }
    }

    private void resizeWest(double deltaX) {
        double newWidth = imageView.getFitWidth() - deltaX;
        if (newWidth > 10) {
            imageView.setFitWidth(newWidth);
            this.setTranslateX(this.getTranslateX() + deltaX);
        }
    }

    // Border direction
    private ResizeHandle getHandle(double x, double y) {
        double w = imageView.getFitWidth();
        double h = imageView.getFitHeight();

        boolean top = y < GRIP_SIZE;
        boolean bottom = y > h - GRIP_SIZE;
        boolean left = x < GRIP_SIZE;
        boolean right = x > w - GRIP_SIZE;

        if (top && left) return ResizeHandle.NORTH_WEST;
        if (top && right) return ResizeHandle.NORTH_EAST;
        if (bottom && left) return ResizeHandle.SOUTH_WEST;
        if (bottom && right) return ResizeHandle.SOUTH_EAST;

        if (top) return ResizeHandle.NORTH;
        if (bottom) return ResizeHandle.SOUTH;
        if (left) return ResizeHandle.WEST;
        if (right) return ResizeHandle.EAST;

        return ResizeHandle.NONE;
    }
}