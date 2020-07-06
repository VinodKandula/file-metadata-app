package com.filemetadata.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vinod Kandula
 */
@Data
public class FileMetadata {

    private String name;
    private String path;
    private long size;
    private String parent;
    private String absolutePath;
    private boolean isAbsolute;
    private String canonicalPath;
    private String creationTime;
    private String lastAccessTime;
    private String lastModifiedTime;
    private boolean isDirectory;
    private boolean isFile;
    private boolean isHidden;
    private boolean isRegularFile;
    private boolean isSymbolicLink;
    private boolean canRead;
    private boolean canWrite;
    private boolean canExecute;

    List<FileMetadata> children;

    public FileMetadata() {
        this.children = new ArrayList<>();
    }

    public void addChild(FileMetadata fileAttributes) {
        this.children.add(fileAttributes);
    }

}
