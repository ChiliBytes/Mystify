package com.chilibytes.mystify.blur;

import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.util.stream.IntStream;

public class ImageProcessor {

    public WritableImage createWritableImageCopy(Image source) {
        var copy = new WritableImage((int) source.getWidth(), (int) source.getHeight());
        var reader = source.getPixelReader();
        var writer = copy.getPixelWriter();

        IntStream.range(0, (int) source.getHeight())
                .forEach(y ->
                        IntStream.range(0, (int) source.getWidth())
                                .forEach(x ->
                                        writer.setColor(x, y, reader.getColor(x, y))
                                )
                );
        return copy;
    }

    public void applyCircularBlur(WritableImage image, int centerX, int centerY, int radius, int blurRadius) {
        var reader = image.getPixelReader();
        var writer = image.getPixelWriter();
        final int radiusSquared = radius * radius;

        IntStream.rangeClosed(
                        Math.max(0, centerY - radius),
                        Math.min((int) image.getHeight() - 1, centerY + radius)
                )
                .forEach(y ->
                        IntStream.rangeClosed(
                                        Math.max(0, centerX - radius),
                                        Math.min((int) image.getWidth() - 1, centerX + radius)
                                )
                                .filter(x -> {
                                    int dx = x - centerX;
                                    int dy = y - centerY;
                                    return (dx * dx + dy * dy) <= radiusSquared;
                                })
                                .forEach(x -> {
                                    var blurredColor = calculateBlurredColor(image, x, y, blurRadius, reader);
                                    writer.setColor(x, y, blurredColor);
                                })
                );
    }

    public Color calculateBlurredColor(WritableImage image, int x, int y, int radius, PixelReader reader) {
        if (radius <= 1) return reader.getColor(x, y);

        double totalRed = 0;
        double totalGreen = 0;
        double totalBlue = 0;
        double totalAlpha = 0;
        int count = 0;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int sampleX = x + dx;
                int sampleY = y + dy;

                if (isWithinImageBounds(image, sampleX, sampleY)) {
                    var color = reader.getColor(sampleX, sampleY);
                    totalRed += color.getRed();
                    totalGreen += color.getGreen();
                    totalBlue += color.getBlue();
                    totalAlpha += color.getOpacity();
                    count++;
                }
            }
        }

        return count == 0 ? reader.getColor(x, y) :
                new Color(totalRed / count, totalGreen / count, totalBlue / count, totalAlpha / count);
    }

    public boolean isWithinImageBounds(WritableImage image, int x, int y) {
        return x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight();
    }

    public WritableImage createBlankImage(int width, int height) {
        WritableImage blankImage = new WritableImage(width, height);
        var writer = blankImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, Color.TRANSPARENT);
            }
        }

        return blankImage;
    }
}