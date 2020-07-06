package com.filemetadata.file.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.filemetadata.dto.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Vinod Kandula
 */
public class FileMetadataHandler {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Fetches Directory & Files metadata recursively for the given directory path
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String listDirectoriesAndFilesMetadata(String path) throws Exception{
        File folder = new File(path);
        if(!folder.isDirectory()) {
            throw new InvalidDirectoryPathException(FileErrorCodes.INVALID_DIRECTORY_PATH, path);
        }

        FileMetadata fileMetadata = fileMetadata(folder);

        if (folder.isDirectory())
            traverseFolderRecursively(folder, fileMetadata);

        return objectMapper.writeValueAsString(fileMetadata);
    }

    private static void traverseFolderRecursively(File folder, FileMetadata fileMetadata) throws Exception {
        File[] fileList = folder.listFiles();
        // Iterate through and call this function for any sub-directories.
        for (File file : fileList) {
            FileMetadata child = fileMetadata(file);
            fileMetadata.addChild(child);
            if (file.isDirectory()) {
                traverseFolderRecursively(file, child);
            }
        }
    }

    /**
     * Fetches file metadata for the given file path
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String fileMetadata(String path) throws Exception {
        File file = new File(path);
        if(!file.isFile()) {
            throw new InvalidFilePathException(FileErrorCodes.INVALID_FILE_PATH, path);
        }
        return objectMapper.writeValueAsString(fileMetadata(new File(path)));
    }

    private static FileMetadata fileMetadata(File file) throws Exception {
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setCreationTime(attr.creationTime().toString());
        fileMetadata.setLastAccessTime(attr.lastAccessTime().toString());
        fileMetadata.setLastModifiedTime(attr.lastModifiedTime().toString());

        fileMetadata.setDirectory(attr.isDirectory());
        fileMetadata.setRegularFile(attr.isRegularFile());
        fileMetadata.setSymbolicLink(attr.isSymbolicLink());
        fileMetadata.setSize(attr.size());

        fileMetadata.setCanWrite(file.canWrite());
        fileMetadata.setCanRead(file.canRead());
        fileMetadata.setCanExecute(file.canExecute());
        fileMetadata.setAbsolute(file.isAbsolute());
        fileMetadata.setFile(file.isFile());
        fileMetadata.setHidden(file.isHidden());
        fileMetadata.setPath(file.getPath());
        fileMetadata.setName(file.getName());
        fileMetadata.setParent(file.getParent());
        fileMetadata.setCanonicalPath(file.getCanonicalPath());
        fileMetadata.setAbsolutePath(file.getAbsolutePath());

        return fileMetadata;
    }

    public static Set<String> listFilesUsingFileWalk(String dir, int depth) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
            return stream
                    .filter(file -> {
                        try {
                            return !Files.isDirectory(file) && !Files.isHidden(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toSet());
        }
    }

    public static Set<FileMetadata> listFiles(String dir, int depth) throws IOException {
        try (Stream<Path> stream = Files.walk(Paths.get(dir), depth)) {
            return stream
                     .map(path -> {
                        try {
                            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

                            FileMetadata fileAttributes = new FileMetadata();
                            fileAttributes.setCreationTime(attr.creationTime().toString());
                            fileAttributes.setLastAccessTime(attr.lastAccessTime().toString());
                            fileAttributes.setLastModifiedTime(attr.lastModifiedTime().toString());

                            fileAttributes.setDirectory(attr.isDirectory());
                            fileAttributes.setRegularFile(attr.isRegularFile());
                            fileAttributes.setSymbolicLink(attr.isSymbolicLink());
                            fileAttributes.setSize(attr.size());

                            File file = path.toFile();
                            fileAttributes.setCanWrite(file.canWrite());
                            fileAttributes.setCanRead(file.canRead());
                            fileAttributes.setCanExecute(file.canExecute());
                            fileAttributes.setAbsolute(file.isAbsolute());
                            fileAttributes.setFile(file.isFile());
                            fileAttributes.setHidden(file.isHidden());
                            fileAttributes.setPath(file.getPath());
                            fileAttributes.setName(file.getName());
                            fileAttributes.setParent(file.getParent());
                            fileAttributes.setCanonicalPath(file.getCanonicalPath());
                            fileAttributes.setAbsolutePath(file.getAbsolutePath());

                            return fileAttributes;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    })
                    .collect(Collectors.toSet());
        }
    }

    public static void main(String[] args) throws Exception {
        String path = "/Users/vinodkandula/engineering/poc/FileUtilityApp/src/main/resources/";

        System.out.println("=======================");
        System.out.println(fileMetadata(path));

        System.out.println("=======================");
        System.out.println(listDirectoriesAndFilesMetadata(path));
    }
}
