package com.chilibytes.mystify.core.service;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Slf4j
@Service
public class ImageService {

    public WritableImage createWritableImageCopy(Image source) {
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
