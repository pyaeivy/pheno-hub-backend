package com.example.demo.implementation;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@ConditionalOnProperty(
        name = "app.storage.provider",
        havingValue = "local",
        matchIfMissing = true
)
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class LocalStorageProvider implements StorageProvider{
    private final Path rootLocation;

    public LocalStorageProvider(
            @Value("${app.storage.local.path}") String uploadPath
    ) throws IOException{
        this.rootLocation = Paths.get(uploadPath).toAbsolutePath().normalize();
        Files.createDirectories(rootLocation);
    }

    private Path resolvePath(String key) throws IOException{
        Path path = rootLocation.resolve(key).normalize();
        if (!path.startsWith(rootLocation)){
            throw new SecurityException("Invalid Storage path!");
        }
        return path;
    }

    @Override
    public String upload(String key, MultipartFile file) throws IOException {
        Path destination = resolvePath(key);
        Files.createDirectories(destination.getParent());
        try(InputStream input = file.getInputStream()){
            Files.copy(
                    input,
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }
        return key;
    }

    @Override
    public Resource load(String key) throws IOException {
        Path file = resolvePath(key);
        Resource resource = new UrlResource(file.toUri());

        if(!resource.exists() && !resource.isReadable()){
            throw new FileNotFoundException("File not found!");
        }
        return resource;
    }

    @Override
    public void delete(String key) throws IOException {
        Files.deleteIfExists(resolvePath(key));
    }
}
