package com.chilibytes.mystify.core.feature.video.service;

import com.chilibytes.mystify.general.service.ScriptProcessorService;
import com.chilibytes.mystify.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.chilibytes.mystify.ui.common.CustomDialog.showSuccess;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlideshowService {

    private final ScriptProcessorService scriptProcessorService;
    private final ApplicationProperties applicationProperties;

    private static final String PYTHON_SLIDESHOW_SCRIPT_FILE = "slideshow-from-images/video-creator.py";
    private static final String PYTHON_SLIDESHOW_SCRIPT_NAME = "Slideshow Creator";

    public void createSlideShow(String imagesSourceFolderPath, String outputFolder,String videoName ,float secondsBetween) {
        imagesSourceFolderPath = imagesSourceFolderPath.isBlank() ? applicationProperties.getAppWorkspaceDefaultInput() : imagesSourceFolderPath;
        outputFolder = outputFolder.isBlank() ? applicationProperties.getAppWorkspaceDefaultOutput() : outputFolder;

        List<String> commandArguments = List.of(
                applicationProperties.getAppScriptsPythonVersion(),
                applicationProperties.getAppScriptsPythonBasePath() + PYTHON_SLIDESHOW_SCRIPT_FILE,
                imagesSourceFolderPath,
                outputFolder,
                videoName + ".mp4",
                String.valueOf(secondsBetween)
        );

        ScriptProcessorService.ScriptComponents components = new ScriptProcessorService.ScriptComponents(PYTHON_SLIDESHOW_SCRIPT_NAME, commandArguments);
        scriptProcessorService.executeScript(components);
        showSuccess("Slideshow video created successfully");
    }
}
