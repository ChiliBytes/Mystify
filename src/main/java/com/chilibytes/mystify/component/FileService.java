package com.chilibytes.mystify.component;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;

@Getter
@Slf4j
@Service
public class FileService {

    private String defaultExtension = "png";

    public Optional<Image> loadImage(Stage stage) {
        FileChooser fileChooser = createImageFileChooser();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Image image = new Image(file.toURI().toString());
                setDefaultExtensionFromFile(file);
                return Optional.of(image);
            } catch (Exception e) {
                log.error("Error on loadImage(): {}", e.getMessage());
                showError("Load Image", "An Error has occurred while loading the image", e);
            }
        }
        return Optional.empty();
    }

    public boolean saveImage(Stage stage, Image image, String defaultExtension) {
        FileChooser fileChooser = createSaveFileChooser(defaultExtension);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                saveImageToFile(image, file);
                return true;
            } catch (IOException e) {
                log.error("IOException on saveImage(): {}", e.getMessage(), e);
                showError("Save Image", "An IOException has occurred while saving the image", e);

            } catch (Exception ex) {
                log.error("Error on saveImage(): {}", ex.getMessage(), ex);
                showError("Save Image", "An error has occurred while saving the image", ex);
            }
        }
        return false;
    }

    private FileChooser createImageFileChooser() {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.PNG")
        );
        return fileChooser;
    }

    private FileChooser createSaveFileChooser(String defaultExt) {
        var fileChooser = new FileChooser();
        FileChooser.ExtensionFilter defaultFilter = new FileChooser.ExtensionFilter(
                defaultExt.toUpperCase() + " Files", "*." + defaultExt
        );

        fileChooser.getExtensionFilters().add(defaultFilter);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setSelectedExtensionFilter(defaultFilter);
        fileChooser.setInitialFileName("image." + defaultExt);

        return fileChooser;
    }

    private void saveImageToFile(Image image, File file) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        String formatName = getFileExtension(file).toLowerCase();

        if (formatName.equals("jpg") || formatName.equals("jpeg")) {
            BufferedImage optimizedImage = createOptimizedImage(bufferedImage);
            saveWithCompression(optimizedImage, file, 0.9f);
        } else {
            saveWithCompression(bufferedImage, file, 1.0f);
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

    private void saveWithCompression(BufferedImage image, File file, float quality) throws IOException {
        String format = getFileExtension(file).toLowerCase();

        if (format.equals("jpg") || format.equals("jpeg")) {
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

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? defaultExtension : fileName.substring(dotIndex + 1);
    }

    private void setDefaultExtensionFromFile(File file) {
        String loadedExtension = getFileExtension(file);
        if (loadedExtension.equals("jpg") || loadedExtension.equals("jpeg") ||
                loadedExtension.equals("png") || loadedExtension.equals("bmp")) {
            defaultExtension = loadedExtension;
        }
    }
}
