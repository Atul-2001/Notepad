package org.signature.util;

import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.signature.model.TextFile;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteFile extends Service<Boolean> {

    private final JTextArea writingPad;
    private final File destination;
    private final StringProperty EndOfLine;

    public WriteFile(JTextArea writingPad, File destination, StringProperty formattingType) {
        this.writingPad = writingPad;
        this.destination = destination;
        this.EndOfLine = formattingType;
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
                            try (BufferedWriter writer = Files.newBufferedWriter(destination.toPath(), StandardOpenOption.WRITE) /*new BufferedWriter(new FileWriter(destination.getAbsolutePath()))*/) {
                                updateMessage("Saving file...");

                                String content = writingPad.getText();

                                if (content.startsWith(".LOG")) {
                                    LocalDateTime dateTime = LocalDateTime.now();
                                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm  dd-MM-yyyy");
                                    content += System.lineSeparator() + dateTime.format(formatter) + System.lineSeparator();
                                }

                                switch (EndOfLine.get()) {
                                    case TextFile.EOLFormat.WINDOW:
                                        content = content.replaceAll("([^\\r][\\n]|[\\r][^\\n])", "\r\n");
                                        break;
                                    case TextFile.EOLFormat.LINUX:
                                    case TextFile.EOLFormat.UNIX_MACOS:
                                        content = content.replaceAll("([\\r][\\n]|[\\r])", "\n");
                                        break;
                                    case TextFile.EOLFormat.CLASSIC_MACOS:
                                        content = content.replaceAll("([\\r][\\n]|[\\n])", "\r");
                                        break;
                                }

                                writer.write(content);
//                                Files.write(destination.toPath(), writingPad.getText().getBytes(), StandardOpenOption.WRITE);
                                return true;
                            } catch (IOException e) {
                                System.out.println("Write Exception : " + e.getLocalizedMessage());
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
            }

            @Override
            protected synchronized void cancelled() {
                updateMessage("Cancelled!");
            }

            @Override
            protected synchronized void failed() {
                updateMessage("Failed!");
            }
        };
    }
}
