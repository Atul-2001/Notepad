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
            protected synchronized Boolean call() throws Exception {
                if (destination != null) {
                    destination.createNewFile();
                    if (destination.isFile()) {
                        if (destination.canWrite()) {
                            updateMessage("Getting ready...");
                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(destination.getAbsolutePath()))) {
                                updateMessage("Saving file...");
                                writer.write(writingPad.getText());
//                                Files.write(destination.toPath(), writingPad.getText().getBytes(), StandardOpenOption.WRITE);
                                return true;
                            } catch (IOException e) {
                                System.out.println("Exception : " + e.getMessage());
                                failed();
                            }
                        } else {
                            updateMessage("Can't write");
                            System.out.println("Can't Write");
                            cancel(true);
                        }
                    } else {
                        System.out.println("Is not a file");
                        cancel(true);
                    }
                } else {
                    System.out.println("Is null");
                    cancel(true);
                }
                return false;
            }

            @Override
            protected synchronized void succeeded() {
                updateMessage("Done!");
                super.succeeded();
            }

            @Override
            protected synchronized void cancelled() {
                updateMessage("Cancelled!");
                super.cancelled();
            }

            @Override
            protected synchronized void failed() {
                updateMessage("Failed!");
                super.failed();
            }
        };
    }
}
