package com.acheron.fileservice.service;

import com.acheron.fileservice.api.FileApi;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileSaver {
    private static final Logger log = LoggerFactory.getLogger(FileSaver.class);
    private final String bucketName = "container-61d6e.firebasestorage.app";
    private Storage storage;

    public FileSaver() {
        try {
            this.storage = StorageOptions
                    .newBuilder()
                    .setCredentials(
                            ServiceAccountCredentials
                                    .fromStream(
                                            new ClassPathResource("container-61d6e-firebase-adminsdk-fbsvc-304c7c9f8d.json")
                                                    .getInputStream())).build().getService();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public String uploadImage(String folder, MultipartFile file) throws IOException {
        String fileName = folder + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType() != null ? file.getContentType() : "media")
                .build();

        storage.create(blobInfo, file.getBytes());

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        String url = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + encodedFileName + "?alt=media";

        log.info("Uploaded file to Firebase: {}", url);
        return url;
    }
    public List<String> uploadFiles(List<MultipartFile> files, FileApi.EventType metaData)  {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = metaData.getValue() + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType() != null ? file.getContentType() : "media")
                    .build();

            try {
                storage.create(blobInfo, file.getBytes());
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }

            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            String url = "https://firebasestorage.googleapis.com/v0/b/" + bucketName + "/o/" + encodedFileName + "?alt=media";
            urls.add(url);
            log.info("Uploaded file to Firebase: {}", url);
        }
        log.info("Uploaded files to Firebase");
        return urls;
    }

    public boolean deleteImage(String url) {
        try {
            String[] parts = url.split("/", 5);
            if (parts.length < 5) {
                log.error("Invalid URL: {}", url);
                return false;
            }
            String key = parts[4].replace("?alt=media", "");

            BlobId blobId = BlobId.of(bucketName, key);
            boolean deleted = storage.delete(blobId);

            if (deleted) {
                log.info("Deleted file: {}", url);
            } else {
                log.warn("File not found: {}", url);
            }
            return deleted;
        } catch (Exception e) {
            log.error("Failed to delete file {}: {}", url, e.getMessage());
            return false;
        }
    }
}
