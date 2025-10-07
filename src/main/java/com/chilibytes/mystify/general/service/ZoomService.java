package com.chilibytes.mystify.general.service;

import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Service
public class ZoomService {

    private double zoomLevel = 1.0;

    public void applyZoom(ImageView imageView) {
        if (imageView != null && imageView.getImage() != null) {
            imageView.setFitWidth(imageView.getImage().getWidth() * zoomLevel);
            imageView.setFitHeight(imageView.getImage().getHeight() * zoomLevel);
        }
    }

    public void resetZoom() {
        zoomLevel = 1.0;
    }
}
