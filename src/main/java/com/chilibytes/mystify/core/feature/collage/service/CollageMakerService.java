package com.chilibytes.mystify.core.feature.collage.service;

import com.chilibytes.mystify.general.service.ScriptProcessorService;
import com.chilibytes.mystify.general.service.FileService;
import com.chilibytes.mystify.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;
import static com.chilibytes.mystify.ui.common.CustomDialog.showSuccess;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollageMakerService {

    private final ScriptProcessorService scriptProcessorService;
    private final ApplicationProperties applicationProperties;
    private final FileService fileService;

    private static final String PYTHON_COLLAGE_SCRIPT_FILE = "collage-maker/collage_maker.py";
    private static final String PYTHON_COLLAGE_SCRIPT_NAME = "Collage Maker";
    private static final int DEFAULT_COLLAGE_WIDTH_SIZE = 2000; //This affects the resolution
    private static final int DEFAULT_COLLAGE_INITIAL_HEIGHT_SIZE = 1000;
    private static final String DEFAULT_COLLAGE_PREFIX_NAME = "My-Collage";
    private static final int MAX_IMAGES_FOR_STANDARD_MODE = 6;
    private static final String DEFAULT_COLLAGE_EXTENSION = ".jpg";

    private List<String> imagesInDirectory;

    public record ChunksInfo(int totalItems, int imagesPerCollage,
                             int resultingCollagesNumber, int remainingLast, boolean isOptimal) {

    }

    private ChunksInfo chunksInfo;

    public void createStandardCollage(String inputFolder, String outputFolder, String collageName,
                                      int collageWidth, int collageInitialHeight, boolean shuffle) {
        List<String> commandArguments = new java.util.ArrayList<>(
                List.of(
                        applicationProperties.getAppScriptsPythonVersion(),
                        applicationProperties.getAppScriptsPythonBasePath() + PYTHON_COLLAGE_SCRIPT_FILE,
                        "-f",
                        inputFolder,
                        "-o",
                        outputFolder + collageName,
                        "-w",
                        String.valueOf(collageWidth),
                        "-i",
                        String.valueOf(collageInitialHeight)
                )
        );

        if (shuffle) {
            commandArguments.add("-s");
        }

        ScriptProcessorService.ScriptComponents components = new ScriptProcessorService.ScriptComponents(PYTHON_COLLAGE_SCRIPT_NAME, commandArguments);
        scriptProcessorService.executeScript(components);
    }

    public void createCollage(String inputFolder, String outputFolder, String customCollagePrefix) {
        try {
            inputFolder = inputFolder.isBlank() ? applicationProperties.getAppWorkspaceDefaultInput() : inputFolder;
            outputFolder = outputFolder.isBlank() ? applicationProperties.getAppWorkspaceDefaultOutput() : outputFolder;

            this.imagesInDirectory = fileService.getAllImagesFromDirectory(inputFolder);
            String collagePrefix = getCollagesPrefix(customCollagePrefix);

            // Do not split in different collages when there are few images
            if (this.imagesInDirectory.size() <= MAX_IMAGES_FOR_STANDARD_MODE) {
                log.info("Creating the requested collage using standard mode");
                createStandardCollage(inputFolder, outputFolder, collagePrefix + DEFAULT_COLLAGE_EXTENSION);
            } else {
                // Split in different collages (Bulk Mode) when there are lots of images
                log.info("Creating the requested collage using bulk mode");
                createBulkCollage(inputFolder, outputFolder, collagePrefix);
            }
            showSuccess("Collage created successfully!");
        } catch (Exception ex) {
            log.error("Error on createCollage(): {}", ex.getMessage(), ex);
            showError("Error", "The requested collage could not be created", ex);
        }

    }

    private void createStandardCollage(String inputFolder, String outputFolder, String collageName) {
        this.createStandardCollage(inputFolder, outputFolder, collageName,
                DEFAULT_COLLAGE_WIDTH_SIZE, DEFAULT_COLLAGE_INITIAL_HEIGHT_SIZE, Boolean.FALSE);
    }

    private void createBulkCollage(String inputFolder, String outputFolder, String customCollagePrefix) {
        int imagesInDirectoryCount = this.imagesInDirectory.size();
        //Get the optimal distribution and create the distribution map
        getOptimalDistribution(imagesInDirectoryCount);
        Map<Integer, List<String>> collagePartsHashMap = createMapWithOptimalDistribution(imagesInDirectoryCount, imagesInDirectory);

        //Process the collage
        collagePartsHashMap.forEach((key, value) ->
                createTemporalWorkingDirectory(inputFolder, value).ifPresentOrElse(
                        directoryName -> {
                            createStandardCollage(directoryName, outputFolder, customCollagePrefix + "-" + (key + 1) + DEFAULT_COLLAGE_EXTENSION);
                            removeTemporalWorkingDirectory(directoryName);
                        }, () -> showError("Failed to create collages due to an error while creating the workspace")
                )
        );
    }

    private String getCollagesPrefix(String originalPrefix) {
        if (Objects.isNull(originalPrefix) || originalPrefix.isBlank()) {
            return DEFAULT_COLLAGE_PREFIX_NAME;
        }
        return originalPrefix;
    }

    private Optional<String> createTemporalWorkingDirectory(String baseInputFolder, List<String> currentImagesForCollage) {
        File workingDirectory = new File(baseInputFolder + "temp");

        if (workingDirectory.mkdir()) {
            String newFolderName = workingDirectory + "/";
            for (String s : currentImagesForCollage) {
                Path source = Paths.get(baseInputFolder + s);
                Path destination = Paths.get(newFolderName + s);
                try {
                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    log.error("IOException on createTemporalWorkingDirectory(): {}", e.getMessage(), e);
                }
            }
            return Optional.of(newFolderName);
        }
        return Optional.empty();
    }

    private Map<Integer, List<String>> createMapWithOptimalDistribution(int totalItems, List<String> allImages) {
        int imagesPerCollage = this.chunksInfo.imagesPerCollage;

        Map<Integer, List<String>> collagePartsHashMap = new HashMap<>();
        int remainin = totalItems;
        int lastAdded = 0;

        for (int i = 0; i <= totalItems; i++) {
            if (imagesPerCollage <= remainin) {
                collagePartsHashMap.put(i, allImages.subList(lastAdded, (lastAdded + imagesPerCollage)));
                lastAdded += imagesPerCollage;
                remainin -= imagesPerCollage;
            } else {
                collagePartsHashMap.put(i, allImages.subList(lastAdded, allImages.size()));
                break;
            }
        }
        return collagePartsHashMap;
    }

    private void removeTemporalWorkingDirectory(String temporalWorkingDirectoryName) {
        Path carpetaPath = Paths.get(temporalWorkingDirectoryName);

        try (Stream<Path> stream = Files.walk(carpetaPath)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.error("IOException on removeTemporalWorkingDirectory: {} ", e.getMessage(), e);
                        }
                    });
        } catch (Exception e) {
            log.error("Error on removeTemporalWorkingDirectory: {} ", e.getMessage(), e);
        }
    }

    private void computeItems(int totalItems, int imagesPerCollage) {
        int closestTarget = 0;
        int resultingCollagesNumber = 0;
        int minRemaining = 2;
        int maxRemaining = imagesPerCollage - 1;

        for (int i = totalItems; i > 1; i--) {
            int rest = totalItems / i;
            if (i == imagesPerCollage) {
                closestTarget = i * rest;
                resultingCollagesNumber = rest;
            }
        }

        int remaining = totalItems - closestTarget;
        boolean isOptimal;

        isOptimal = remaining == 0 ?
                Boolean.TRUE :
                (remaining >= minRemaining && remaining <= maxRemaining);

        chunksInfo = new ChunksInfo(totalItems, imagesPerCollage, resultingCollagesNumber, remaining, isOptimal);
    }

    public void getOptimalDistribution(int totalItems) {
        boolean foundOptimal;
        List<Integer> imagesPerCollateTests = List.of(5, 6, 7, 8);
        for (int i = 0; i <= imagesPerCollateTests.size(); i++) {
            computeItems(totalItems, imagesPerCollateTests.get(i));
            foundOptimal = chunksInfo.isOptimal;
            if (foundOptimal) {
                logDistributionResults(totalItems, imagesPerCollateTests, imagesPerCollateTests.get(i));
                break;
            }
        }
    }

    private void logDistributionResults(int totalItems, List<Integer> imagesPerCollateTests, int currentIndex) {
        log.info("""
                          For {} items, the next distribution was computed:
                          Used images per collage: {}
                          Resulting collages: {}
                          The remaining collage uses {} images
                          (Checked values: {})
                        """,
                totalItems,
                currentIndex,
                chunksInfo.resultingCollagesNumber + 1, //Adding the resulting one
                chunksInfo.remainingLast,
                imagesPerCollateTests
        );
    }
}
