package com.chilibytes.mystify.general.service;

import javafx.scene.image.WritableImage;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;

@Service
public class UndoService {

    private final Deque<WritableImage> undoStack = new ArrayDeque<>();
    private static final int MAX_UNDO_HISTORY = 20;

    public void saveState(WritableImage image) {
        if (image != null) {
            WritableImage copy = createImageCopy(image);
            undoStack.push(copy);

            if (undoStack.size() > MAX_UNDO_HISTORY) {
                trimUndoHistory();
            }
        }
    }

    public WritableImage undo(WritableImage currentImage) {
        if (!undoStack.isEmpty()) {
            return undoStack.pop();
        }
        return currentImage;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public void clearHistory() {
        undoStack.clear();
    }

    private WritableImage createImageCopy(WritableImage source) {
        var copy = new WritableImage((int) source.getWidth(), (int) source.getHeight());
        var reader = source.getPixelReader();
        var writer = copy.getPixelWriter();

        for (int y = 0; y < source.getHeight(); y++) {
            for (int x = 0; x < source.getWidth(); x++) {
                writer.setColor(x, y, reader.getColor(x, y));
            }
        }
        return copy;
    }

    private void trimUndoHistory() {
        Deque<WritableImage> tempStack = new ArrayDeque<>();
        while (undoStack.size() > MAX_UNDO_HISTORY - 1) {
            tempStack.push(undoStack.pop());
        }
        undoStack.clear();
        while (!tempStack.isEmpty()) {
            undoStack.push(tempStack.pop());
        }
    }
}
