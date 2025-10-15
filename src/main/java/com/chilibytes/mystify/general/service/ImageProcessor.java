package com.chilibytes.mystify.general.service;

import com.chilibytes.mystify.config.ApplicationProperties;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ImageProcessor {

    @Getter
    private String defaultExtension = "png";

    private List<String> imagesAllowedExtensions;
    private List<String> imagesAllowedWildCards;
    private final ApplicationProperties applicationProperties;

    @PostConstruct
    private void getAllowedExtensions() {
        this.imagesAllowedExtensions = applicationProperties.getAppImagesAllowedExtensions();
        this.imagesAllowedWildCards = applicationProperties.getAppImagesAllowedWildCards(this.imagesAllowedExtensions);
    }

    public FileChooser createImageFileChooser() {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", this.imagesAllowedWildCards)
        );
        return fileChooser;
    }

    public FileChooser createSaveFileChooser(String defaultExt) {
        var fileChooser = new FileChooser();
        FileChooser.ExtensionFilter defaultFilter = new FileChooser.ExtensionFilter(
                defaultExt.toUpperCase() + " Files", "*." + defaultExt
        );

        fileChooser.getExtensionFilters().add(defaultFilter);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", getAllowedWildcardsPart("*.png")),
                new FileChooser.ExtensionFilter("JPG Files", getAllowedWildcardsPart("*.jpg")),
                new FileChooser.ExtensionFilter("JPEG Files", getAllowedWildcardsPart("*.jpeg")),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setSelectedExtensionFilter(defaultFilter);
        fileChooser.setInitialFileName("image." + defaultExt);

        return fileChooser;
    }

    public void saveImageToFile(Image image, File file) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        String formatName = getFileExtension(file).toLowerCase();

        if (formatName.toLowerCase().contains("jp")) {
            BufferedImage optimizedImage = createOptimizedImage(bufferedImage);
            saveWithCompression(optimizedImage, file, 0.9f);
        } else {
            saveWithCompression(bufferedImage, file, 1.0f);
        }
    }

    public void setDefaultExtensionFromFile(File file) {
        String loadedExtension = getFileExtension(file);
        if (this.imagesAllowedExtensions.contains(loadedExtension)) {
            defaultExtension = loadedExtension;
        }
    }

    public List<String> getAllImagesFromDirectory(String collagePath) {
        File dir = new File(collagePath);
        List<String> imageList = new ArrayList<>();
        if (!dir.exists() || !dir.isDirectory()) {
            log.warn("Directory does not exists: {}", collagePath);
            return imageList;
        }

        File[] imageFiles = dir.listFiles((file, name) -> {
            String lowerName = name.toLowerCase();
            return lowerName.endsWith(".jpg") ||
                    lowerName.endsWith(".jpeg") ||
                    lowerName.endsWith(".png");
        });

        if (imageFiles != null) {
            Arrays.sort(imageFiles, Comparator.comparing(File::getName,
                    Comparator.comparingInt(this::extractNumber)));

            for (File file : imageFiles) {
                imageList.add(file.getName());
            }
        }
        return imageList;
    }

    private List<String> getAllowedWildcardsPart(String extension) {
        return imagesAllowedWildCards.stream()
                .filter(ext -> ext.equalsIgnoreCase(extension))
                .map(ext -> "*." + ext)
                .toList();
    }

    private int extractNumber(String name) {
        try {
            String num = name.replaceAll("\\D+", "");
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }


    private BufferedImage createOptimizedImage(BufferedImage source) {
        BufferedImage result = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics2 = result.createGraphics();
        try {
            graphics2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2.drawImage(source, 0, 0, null);
        } finally {
            graphics2.dispose();
        }
        return result;
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? defaultExtension : fileName.substring(dotIndex + 1);
    }

    private void saveWithCompression(BufferedImage image, File file, float quality) throws IOException {
        String format = getFileExtension(file).toLowerCase();

        if (format.toLowerCase().contains("jp")) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
            if (writers.hasNext()) {
                ImageWriter writer = writers.next();
                ImageWriteParam writeParam = writer.getDefaultWriteParam();

                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    writeParam.setCompressionQuality(quality);
                }

                try (ImageOutputStream output = ImageIO.createImageOutputStream(file)) {
                    writer.setOutput(output);
                    writer.write(null, new IIOImage(image, null, null), writeParam);
                }
                writer.dispose();
                return;
            }
        }
        ImageIO.write(image, format, file);
    }

    public static WritableImage rotateImageClockwise(ImageView imageView) {
        if (imageView == null || imageView.getImage() == null) {
            throw new NullPointerException("No imageview defined");
        }

        Image originalImage = imageView.getImage();
        int width = (int) originalImage.getWidth();
        int height = (int) originalImage.getHeight();

        // Create a new Image switching the original dimensions (now the height is the width)
        WritableImage rotatedImage = new WritableImage(height, width);
        PixelReader pixelReader = originalImage.getPixelReader();
        PixelWriter pixelWriter = rotatedImage.getPixelWriter();

        // Rotate 90 degrees (Clockwise)
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Clockwise rotation formula
                int newX = height - 1 - y;
                pixelWriter.setArgb(newX, x, pixelReader.getArgb(x, y));
            }
        }
        return rotatedImage;
    }


    public static WritableImage downgradeImage(String pathToHDImage, int reduceTo) {
        if (pathToHDImage == null || reduceTo <= 0 || reduceTo > 100) {
            throw new IllegalArgumentException("Scale factor must be between 1 and 100");
        }

        try {
            File file = new File(pathToHDImage.replace("file:", ""));
            if (!file.exists()) {
                throw new IllegalArgumentException("File not found: " + pathToHDImage);
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                // Calculate dimensions directly
                Image originalImage = new Image(fis);

                double originalWidth = originalImage.getWidth();
                double originalHeight = originalImage.getHeight();

                // Calculate new dimensions
                double scale = reduceTo / 100.0;
                int targetWidth = Math.max(1, (int) (originalWidth * scale));
                int targetHeight = Math.max(1, (int) (originalHeight * scale));

                log.info("Resizing: {}x{} -> {}x{} ({})", (int) originalWidth, (int) originalHeight, targetWidth, targetHeight, reduceTo);

                // Load resized image
                try (FileInputStream fis2 = new FileInputStream(file)) {
                    Image resizedImage = new Image(fis2, targetWidth, targetHeight, true, true);

                    // Convert to WritableImage
                    WritableImage writableImage = new WritableImage(targetWidth, targetHeight);
                    PixelWriter writer = writableImage.getPixelWriter();
                    PixelReader reader = resizedImage.getPixelReader();

                    for (int y = 0; y < targetHeight; y++) {
                        for (int x = 0; x < targetWidth; x++) {
                            writer.setArgb(x, y, reader.getArgb(x, y));
                        }
                    }

                    return writableImage;
                }
            }

        } catch (Exception e) {
            log.error("Error on downgradeImage(): {}", e.getMessage(), e);
            return null;
        }
    }
}
