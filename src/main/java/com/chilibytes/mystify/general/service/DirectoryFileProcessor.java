package com.chilibytes.mystify.general.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectoryFileProcessor {

    public Optional<String> createTemporalWorkingDirectory(String baseInputFolder, List<String> filesToCopyToWorkingDirectory) {
        File workingDirectory = new File(baseInputFolder + "temp");

        if (workingDirectory.mkdir()) {
            String newFolderName = workingDirectory + "/";
            for (String s : filesToCopyToWorkingDirectory) {
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

    public void removeTemporalWorkingDirectory(String temporalWorkingDirectoryName) {
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
}
