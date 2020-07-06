package com.filemetadata.controller;

import com.filemetadata.FileMetadataApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Vinod Kandula
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileMetadataApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileMetadataControllerIntegrationTests {

    private static final String LOCAL_PATH = Paths.get("").toAbsolutePath().toString();

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:" + port;
    }

    @Test
    public void givenExistentPath_whenConfirmsFileExists_thenCorrect() {
        Path p = Paths.get(LOCAL_PATH);
        assertTrue(Files.exists(p));
    }

    @Test
    public void givenNonexistentPath_whenConfirmsFileNotExists_thenCorrect() {
        Path p = Paths.get(LOCAL_PATH + "/inexistent_file.txt");
        assertTrue(Files.notExists(p));
    }

    @Test
    public void testFileMetadata() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/filemetadata/file?path="+LOCAL_PATH+"/src/main/resources/data.txt",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(LOCAL_PATH+"/src/main/resources/data.txt"));
    }

    @Test
    public void testDirectoryMetadata() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/filemetadata/directory?path="+LOCAL_PATH+"/src/main/resources/",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(LOCAL_PATH+"/src/main/resources/data.txt"));
    }

    @Test()
    public void givenWrongFilePath_whenMatchErrorResponse_thenCorrect() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/filemetadata/file?path="+LOCAL_PATH+"/src/main/resources/inexistent_file.txt",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("INVALID_FILE_PATH"));
    }

    @Test()
    public void givenWrongDirectoryPath_whenMatchErrorResponse_thenCorrect() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/filemetadata/directory?path="+LOCAL_PATH+"/src/main/resources/inexistent_directory",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("INVALID_DIRECTORY_PATH"));
    }

    @Test()
    public void givenEmptyFilePath_whenMatchErrorResponse_thenCorrect() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/filemetadata/file?path",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("path parameter is missing"));
    }

    @Test()
    public void givenEmptyDirectoryPath_whenMatchErrorResponse_thenCorrect() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(getRootUrl() + "/filemetadata/directory?path",
                HttpMethod.GET, entity, String.class);
        System.out.println(response.getBody());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("path parameter is missing"));
    }

}
