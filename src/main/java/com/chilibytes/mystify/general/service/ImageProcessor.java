package com.chilibytes.mystify.general.service;

import com.chilibytes.mystify.config.ApplicationProperties;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
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
}
