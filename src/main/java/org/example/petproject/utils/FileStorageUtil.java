package org.example.petproject.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileStorageUtil {
    // Thư mục lưu uploads (relative với working dir của app)
    private static final String UPLOAD_DIR = "uploads";

    public static String storeImage(File file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        // Lấy đuôi file
        String ext = getFileExtension(file.getName());
        String newFileName = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);
        Path target = uploadPath.resolve(newFileName);
        try (InputStream in = new FileInputStream(file)) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        // Trả về URI để hiển thị bằng ImageView
        return target.toUri().toString();
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex >= 0) ? fileName.substring(dotIndex + 1) : "";
    }
}