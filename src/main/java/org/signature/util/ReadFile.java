package org.signature.util;

import javafx.concurrent.Task;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class ReadFile extends Task<Void> {

    private final JTextArea writingPad;
    private final File source;

    public ReadFile(JTextArea writingPad, File source) {
        this.writingPad = writingPad;
        this.source = source;
    }

    @Override
    protected synchronized Void call() throws Exception {
        if (source != null && Files.exists(source.toPath())) {
            if (source.isFile()) {
                if (source.canRead()) {
                    if ((source.length() / (1024 * 1024)) <= 10) {
                        updateMessage("Getting ready...");
                        try (BufferedReader reader = new BufferedReader(new FileReader(source.getAbsolutePath()))) {

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
                            StringBuilder data = new StringBuilder();

                            updateMessage("Reading " + source.getName() + "...");
                            while ((read = reader.read(buffer)) >= 0) {
                                data.append(buffer, 0, read);
                                workDone += read;
                                updateProgress(workDone, totalWork);
                            }

                            updateMessage("Please wait...");
                            workDone += 1;
                            writingPad.setText(data.toString());
                            writingPad.setCaretPosition(0);
                            updateProgress(workDone, totalWork);
                            System.gc();
                            System.runFinalization();
                        } catch (IOException e) {
                            System.out.println(e.getMessage());
                            failed();
                        }
                    } else {
                        System.out.println("File is loo large.");
                        failed();
                    }
                } else {
                    System.out.println("Can't read file.");
                    failed();
                }
            } else {
                System.out.println("Is a directory.");
                failed();
            }
        } else {
            System.out.println("Is null");
            failed();
        }
        return null;
    }

    @Override
    protected synchronized void succeeded() {
        super.succeeded();
        updateMessage("Done!");
    }

    @Override
    protected synchronized void cancelled() {
        super.cancelled();
        updateMessage("Cancelled!");
    }

    @Override
    protected synchronized void failed() {
        super.failed();
        updateMessage("Failed!");
    }
}
