package com.chilibytes.mystify.blur;

import javafx.scene.image.ImageView;

public class ZoomService {

    private double zoomLevel = 1.0;

    public void applyZoom(ImageView imageView) {
        if (imageView.getImage() != null) {
            imageView.setScaleX(zoomLevel);
            imageView.setScaleY(zoomLevel);
        }
    }

    public void resetZoom() {
        zoomLevel = 1.0;
    }

    public double getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = Math.clamp(zoomLevel, 0.1, 5.0);
    }

}