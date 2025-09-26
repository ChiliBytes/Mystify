package com.chilibytes.mystify.core.feature.collage;

import com.chilibytes.mystify.common.service.ScriptProcessorService;
import com.chilibytes.mystify.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CollageMaker {

    private final ScriptProcessorService scriptProcessorService;
    private final ApplicationProperties applicationProperties;

    private static final String PYTHON_COLLAGE_SCRIPT_FILE = "collage-maker/collage_maker.py";
    private static final String PYTHON_COLLAGE_SCRIPT_NAME = "Collage Maker";
    private static final int DEFAULT_COLLAGE_SIZE = 1_000;

    public void createCollage(String inputFolder, String outputFolder, String collageName,
                              int collageWidth, int collageInitialHeight, boolean shuffle) {
        List<String> commandArguments = new java.util.ArrayList<>(
                List.of(
                        applicationProperties.getAppScriptsPythonVersion(),
                        applicationProperties.getAppScriptsPythonBasePath() + PYTHON_COLLAGE_SCRIPT_FILE,
                        "-f",
                        inputFolder,
                        "-o",
                        outputFolder + collageName + ".png",
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

    public void createCollage(String inputFolder, String outputFolder, String collageName) {
        this.createCollage(inputFolder, outputFolder, collageName, DEFAULT_COLLAGE_SIZE, DEFAULT_COLLAGE_SIZE, Boolean.FALSE);
    }
}
