package com.chilibytes.mystify.core.feature.pdf.service;

import com.chilibytes.mystify.general.service.ScriptProcessorService;
import com.chilibytes.mystify.config.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.chilibytes.mystify.ui.common.CustomDialog.showError;
import static com.chilibytes.mystify.ui.common.CustomDialog.showSuccess;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfMakerService {
    private final ApplicationProperties applicationProperties;
    private final ScriptProcessorService scriptProcessorService;

    private static final String PYTHON_IMAGE2PDF_SCRIPT_FILE = "pdf-maker/collage-pdf.py";
    private static final String PYTHON_IMAGE2PDF_SCRIPT_NAME = "Images to PDF Creator";
    private static final String PDF_EXTENSION_NAME = ".pdf";

    public void createPdfFromImages(String inputFolder, String outputFolder, String resultingFileNamePrefix) {
        try {
            inputFolder = inputFolder.isBlank() ? applicationProperties.getAppWorkspaceDefaultInput() : inputFolder;
            outputFolder = outputFolder.isBlank() ? applicationProperties.getAppWorkspaceDefaultOutput() : outputFolder;

            List<String> commandArguments = new ArrayList<>(
                    List.of(
                            applicationProperties.getAppScriptsPythonVersion(),
                            applicationProperties.getAppScriptsPythonBasePath() + PYTHON_IMAGE2PDF_SCRIPT_FILE,
                            inputFolder,
                            outputFolder + resultingFileNamePrefix + PDF_EXTENSION_NAME
                    )
            );

            ScriptProcessorService.ScriptComponents components = new ScriptProcessorService.ScriptComponents(PYTHON_IMAGE2PDF_SCRIPT_NAME, commandArguments);
            scriptProcessorService.executeScript(components);
            showSuccess("PDF file created successfully");
        } catch (Exception ex) {
            log.error("Error on createPdfFromImages(): {}", ex.getMessage(), ex);
            showError("The requested pdf file could not be generated", ex.getMessage(), ex);
        }
    }
}
