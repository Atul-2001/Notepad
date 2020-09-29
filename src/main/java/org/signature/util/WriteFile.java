package org.signature.util;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile extends Service<Boolean> {

    private final JTextArea writingPad;
    private final File destination;

    public WriteFile(JTextArea writingPad, File destination) {
        this.writingPad = writingPad;
        this.destination = destination;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                if (destination != null) {
                    destination.createNewFile();
                    if (destination.isFile()) {
                        if (destination.canWrite()) {
                            updateMessage("Getting ready...");
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(destination.getAbsolutePath()))) {
                                char[] buffer = writingPad.getText().toCharArray();
                                int off = 0, len;
                                int totalSize = buffer.length;

                                if (totalSize / 1024 <= 64) {
                                    len = totalSize;
                                } else {
                                    len = 64 * 1024;
                                }

                                updateMessage("Saving file...");
                                do {
                                    writer.write(buffer, off, len);
                                    updateProgress(len, totalSize);
                                    off += len;
                                    len = Math.min(totalSize - len, len);
                                } while (off < totalSize);
                                updateMessage("Done!");
                                return true;
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("Can't Write");
                        }
                    } else {
                        System.out.println("Is not a file");
                    }
                } else {
                    System.out.println("Is null");
                }
                return false;
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                updateMessage("Cancelled!");
            }

            @Override
            protected void failed() {
                super.failed();
                updateMessage("Failed!");
            }
        };
    }
}
