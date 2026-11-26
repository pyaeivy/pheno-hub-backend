package com.example.demo.implementation;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageProvider {

    String upload (String key, MultipartFile file) throws IOException;
    Resource load (String key) throws IOException;
    void delete(String key) throws IOException;
}
