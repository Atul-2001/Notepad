package org.signature.model;

import java.io.File;

public final class TextFile {
    private final String filepath;
    private final String filename;
    private final String fileExtension;
    private long sizeInBytes;
    private int sizeInKB;
    private int sizeInMB;
    boolean isReadable;
    boolean isWriteable;

    public TextFile(String filepath, String filename, String fileExtension) {
        if (filepath == null || filename == null || fileExtension == null) {
            throw new NullPointerException("All fields are required. @Not NULL");
        } else {
            this.filepath = optimizePath(filepath);
            this.filename = filename;
            this.fileExtension = fileExtension.replaceFirst("^\\.", "");
            this.sizeInBytes = 0;
            this.sizeInKB = 0;
            this.sizeInMB = 0;
            isReadable = false;
            isWriteable = false;
        }
    }

    /**
     * parameter should not be null.
    * */
    public TextFile(File file) {
        this(file.getParent(), file.getName().substring(0, file.getName().lastIndexOf(".")), file.getName().substring(file.getName().lastIndexOf(".") +1));
        this.sizeInBytes = file.length();
        this.sizeInKB = (int) sizeInBytes/1024;
        this.sizeInMB = sizeInKB/1024;
        this.isReadable = file.canRead();
        this.isWriteable = file.canWrite();
    }

    private String optimizePath(String filepath) {
        if (filepath.lastIndexOf(File.separator) != filepath.length()-1) {
            return filepath + File.separator;
        } else {
            return filepath;
        }
    }

    public String getFilepath() {
        return filepath;
    }

    public String getFilename() {
        return filename;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public int getSizeInKB() {
        return sizeInKB;
    }

    public int getSizeInMB() {
        return sizeInMB;
    }

    public boolean isReadable() {
        return isReadable;
    }

    public boolean isWriteable() {
        return isWriteable;
    }

    public File toFile() {
        return new File(filepath + filename + "." + fileExtension);
    }

    @Override
    public String toString() {
        return filename + "." + fileExtension;
    }
}
