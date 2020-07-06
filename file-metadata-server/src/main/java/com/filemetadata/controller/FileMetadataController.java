package com.filemetadata.controller;

import com.filemetadata.file.handler.FileMetadataHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * API to fetch Directory & Files metadata recursively for the given path
 *
 * @author Vinod Kandula
 */

@RestController
@RequestMapping(path = "/filemetadata")
@Slf4j
public class FileMetadataController {
    /**
     * API to fetch File metadata for the given file path
     *
     * @param path
     * @return
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping(path = "/file")
    public String fileInfo(@NotNull @RequestParam(value = "path") String path) throws Exception {
        if(path.isEmpty())
            throw new MissingServletRequestParameterException("path", "String.class");

        return FileMetadataHandler.fileMetadata(path);
    }

    /**
     * API to fetch Directory & Files metadata recursively for the given directory path
     *
     * @param path
     * @return
     * @throws Exception
     */
    @CrossOrigin
    @GetMapping(path = "/directory")
    public String directoriesAndFilesInfo(@NotNull @RequestParam(value = "path") String path) throws Exception {
        if(path.isEmpty())
            throw new MissingServletRequestParameterException("path", "String.class");

        return FileMetadataHandler.listDirectoriesAndFilesMetadata(path);
    }

}
