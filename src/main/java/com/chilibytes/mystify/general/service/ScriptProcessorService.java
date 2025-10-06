package com.chilibytes.mystify.general.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@Service
public class ScriptProcessorService {

    public record ScriptComponents(String scriptName, List<String>commands) {
    }

    public  void executeScript(ScriptComponents scriptComponents) {

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(scriptComponents.commands);
            // Combine stdout and stderr
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Read the script output
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            log.info("=== Executing {} via Python ===", scriptComponents.scriptName);
            while ((line = reader.readLine()) != null) {
                log.info("[PYTHON] " + line);
            }

            int exitCode = process.waitFor();
            log.info("Processing {} finished with code {} " ,scriptComponents.scriptName, exitCode);

        } catch (Exception e) {
            log.error("An Exception has occurred while processing {}: {}" , scriptComponents.scriptName ,e.getMessage());
        }
    }
}
