package org.signature.util;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import org.signature.model.TextFile;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ReadFile extends Task<Void> {

    private final JTextArea writingPad;
    private final File source;
    private final StringProperty EndOfLine;
    private String EOL_Type;

    public ReadFile(JTextArea writingPad, File source, StringProperty formattingType) {
        this.writingPad = writingPad;
        this.source = source;
        this.EndOfLine = formattingType;
        EOL_Type = EndOfLine.get();
    }

    @Override
    protected synchronized Void call() throws Exception {
        if (source != null && Files.exists(source.toPath())) {
            if (source.isFile()) {
                if (source.canRead()) {
                    if ((source.length() / (1024 * 1024)) <= 10) {
                        updateMessage("Getting ready...");
                        try (BufferedReader reader = Files.newBufferedReader(source.toPath()) /*new BufferedReader(new FileReader(source.getAbsolutePath()))*/) {
                            //clear the textArea
                            writingPad.setText("");

                            int workDone = 0;
                            long totalWork = source.length() + 1;
                            long filesize = source.length();

                            char[] buffer = new char[1024];
                            if (filesize / 1024 <= 2) {
                                buffer = new char[1024 * 2];
                            } else if (filesize / 1024 <= 4) {
                                buffer = new char[1024 * 4];
                            } else if (filesize / 1024 <= 8) {
                                buffer = new char[1024 * 8];
                            } else if (filesize / 1024 <= 16) {
                                buffer = new char[1024 * 16];
                            } else if (filesize / 1024 <= 32) {
                                buffer = new char[1024 * 32];
                            } else if (filesize / 1024 <= 64 || filesize / 1024 > 64) {
                                buffer = new char[1024 * 64];
                            }

                            int read;
                            updateMessage("Reading " + source.getName() + "...");
                            while ((read = reader.read(buffer)) >= 0) {

                                String value = new String(buffer, 0, read);

                                if (value.contains("\r\n")) {
                                    EOL_Type = TextFile.EOLFormat.WINDOW;
                                } else if (value.contains("\r")) {
                                    EOL_Type = TextFile.EOLFormat.CLASSIC_MACOS;
                                } else if (value.contains("\n")) {
                                    String os = System.getProperty("os.name");
                                    if (os.contains("Linux") || os.contains("linux")) {
                                        EOL_Type = TextFile.EOLFormat.LINUX;
                                    } else if (os.contains("Mac") || os.contains("mac") || os.contains("Unix") || os.contains("unix")) {
                                        EOL_Type = TextFile.EOLFormat.UNIX_MACOS;
                                    }
                                }

                                writingPad.append(value);
                                workDone += read;
                                updateProgress(workDone, totalWork);
                            }

                            updateMessage("Please wait...");
                            workDone += 1;
                            Platform.runLater(() -> EndOfLine.set(EOL_Type));
                            writingPad.setCaretPosition(0);
                            updateProgress(workDone, totalWork);
                            System.gc();
                            System.runFinalization();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            failed();
                        }
                    } else {
                        updateMessage("File is loo large.");
                        System.out.println("File is loo large.");
                        cancel(true);
                    }
                } else {
                    updateMessage("Can't read file.");
                    System.out.println("Can't read file.");
                    cancel(true);
                }
            } else {
                System.out.println("Is a directory.");
                cancel(true);
            }
        } else {
            System.out.println("Is null");
            cancel(true);
        }
        return null;
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
}
