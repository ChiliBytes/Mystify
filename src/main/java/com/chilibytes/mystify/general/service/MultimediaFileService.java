package com.chilibytes.mystify.general.service;

import com.chilibytes.mystify.config.ApplicationProperties;
import com.chilibytes.mystify.core.feature.imageoverlay.service.ImageOverlayService;
import com.chilibytes.mystify.ui.component.PrincipalLayoutsBuilder;
import jakarta.annotation.PostConstruct;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;

@Getter
@Slf4j
@Service
@RequiredArgsConstructor
public class MultimediaFileService {

    private String defaultExtension = "png";

    private List<String> imagesAllowedExtensions;
    private List<String> imagesAllowedWildCards;

    private final ApplicationProperties applicationProperties;
    private final ImageOverlayService imageOverlayService;
    private final UndoService undoService;
    private final PrincipalLayoutsBuilder principalLayoutsBuilder;
    private final ZoomService zoomService;

    @PostConstruct
    private void getAllowedExtensions() {
        this.imagesAllowedExtensions = applicationProperties.getAppImagesAllowedExtensions();
        this.imagesAllowedWildCards = applicationProperties.getAppImagesAllowedWildCards(this.imagesAllowedExtensions);
    }

    private List<String> getAllowedWildcardsPart(String extension) {
        return imagesAllowedWildCards.stream()
                .filter(ext -> ext.equalsIgnoreCase(extension))
                .map(ext -> "*." + ext)
                .toList();
    }

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

    public void processLoadedImage(MainEventHandlerService mainEventHandlerService) {
        ImageView imageView = mainEventHandlerService.getImageView();
        WritableImage currentImage = createWritableImageCopy(mainEventHandlerService);
        mainEventHandlerService.setCurrentImage(currentImage);
        imageView.setImage(currentImage);
        zoomService.resetZoom();
        zoomService.applyZoom(imageView);
        principalLayoutsBuilder.getOuterZoomSlider().setValue(100);
        undoService.clearHistory();
        principalLayoutsBuilder.updateUndoPanelButtonState(false);
        mainEventHandlerService.setImageView(imageView);
    }

    public boolean saveImage(Stage stage, Image image, String defaultExtension) {
        FileChooser fileChooser = createSaveFileChooser(defaultExtension);
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                Image imageToSave = SharedConstant.isMultiLayerImage() ?
                        imageOverlayService.unifyImage() :
                        image;
                saveImageToFile(imageToSave, file);
                SharedConstant.setMultiLayerImage(false);
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

    public void handleResetImage(MainEventHandlerService mainEventHandlerService) {
        Image originalImage = mainEventHandlerService.getOriginalImage();
        if (originalImage != null) {
            undoService.saveState(mainEventHandlerService.getCurrentImage());
            WritableImage currentImage = createWritableImageCopy(mainEventHandlerService);
            mainEventHandlerService.setCurrentImage(currentImage);
            mainEventHandlerService.getImageView().setImage(currentImage);
        }
    }

    private FileChooser createImageFileChooser() {
        var fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", this.imagesAllowedWildCards)
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
                new FileChooser.ExtensionFilter("PNG Files", getAllowedWildcardsPart("*.png")),
                new FileChooser.ExtensionFilter("JPG Files", getAllowedWildcardsPart("*.jpg")),
                new FileChooser.ExtensionFilter("JPEG Files", getAllowedWildcardsPart("*.jpeg")),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        fileChooser.setSelectedExtensionFilter(defaultFilter);
        fileChooser.setInitialFileName("image." + defaultExt);

        return fileChooser;
    }

    private void saveImageToFile(Image image, File file) throws IOException {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        String formatName = getFileExtension(file).toLowerCase();

        if (formatName.toLowerCase().contains("jp")) {
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

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? defaultExtension : fileName.substring(dotIndex + 1);
    }

    private void setDefaultExtensionFromFile(File file) {
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
                    Comparator.comparingInt(MultimediaFileService::extractNumber)));

            for (File file : imageFiles) {
                imageList.add(file.getName());
            }
        }
        return imageList;
    }

    private static int extractNumber(String name) {
        try {
            String num = name.replaceAll("\\D+", "");
            return num.isEmpty() ? 0 : Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void clearImage(MainEventHandlerService mainEventHandlerService) {
        WritableImage currentImage = mainEventHandlerService.getCurrentImage();
        if (!Objects.isNull(currentImage)) {
            undoService.saveState(currentImage);
            WritableImage blankImage = createBlankImage((int) currentImage.getWidth(), (int) currentImage.getHeight());
            mainEventHandlerService.setCurrentImage(blankImage);
            mainEventHandlerService.getImageView().setImage(blankImage);
            ((Pane) mainEventHandlerService.getImageView().getParent())
                    .getChildren()
                    .removeIf(control -> !(control instanceof ImageView));
        }
    }

    private WritableImage createBlankImage(int width, int height) {
        WritableImage blankImage = new WritableImage(width, height);
        var writer = blankImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                writer.setColor(x, y, Color.TRANSPARENT);
            }
        }

        return blankImage;
    }

    public WritableImage createWritableImageCopy(MainEventHandlerService mainEventHandlerService) {
        Image source = mainEventHandlerService.getOriginalImage();
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
}
