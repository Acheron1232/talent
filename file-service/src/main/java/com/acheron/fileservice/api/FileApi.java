package com.acheron.fileservice.api;

import com.acheron.fileservice.service.FileSaver;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class FileApi {

    private final FileSaver fileSaver;

    public  FileApi(FileSaver fileSaver) {
        this.fileSaver = fileSaver;
    }

    @PostMapping(value = "/save_files")
    public List<String> saveFile(@RequestParam("files") List<MultipartFile> files, @RequestPart("metadata") MetaData metadata){

        return fileSaver.uploadFiles(files,EventType.valueOf(metadata.type.toUpperCase()) );
    }

    public record MetaData(

            String type
    ){}

    public enum EventType{
        SHORTS_VIDEO("shorts_video"),SHORTS_IMAGES("shorts_images"), POST("posts"), AVATAR("avatars");

        private final String value;
        EventType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }

    }
}
