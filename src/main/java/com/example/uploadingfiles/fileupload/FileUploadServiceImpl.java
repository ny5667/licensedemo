package com.example.uploadingfiles.fileupload;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Override
    public String fileUpload(MultipartFile multipartFile) {
        // 将上传的文件保存到临时目录
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFileName = UUID.randomUUID().toString();

        File zipFile = new File(tempDir, tempFileName + ".zip");
        try {
            multipartFile.transferTo(zipFile);
        } catch (IOException e) {
            throw new StorageFileNotFoundException("处理文件上传时发生错误", e);
        }

        // 解压缩文件
        String extractFolderName = tempFileName + "_extracted";
        File extractFolder = new File(tempDir, extractFolderName);
        try {
            extractZipFile(new File(tempDir, tempFileName + ".zip"), extractFolder);
        } catch (IOException e) {
            e.printStackTrace();
            return "处理文件上传时发生错误";
        }

        // 查找文件路径
        String targetFileName = "NacosLicense.java";
        File targetFile = findFile(extractFolder, targetFileName);

        if (targetFile != null) {
            try {
                return Files.readString(targetFile.toPath());
            } catch (IOException e) {
                throw new StorageFileNotFoundException("Could not read file: " + targetFile.getName(), e);
            }
        } else {
            return "未找到名为 " + targetFileName + " 的文件";
        }
    }


    private void extractZipFile(File zipFile, File extractFolder) throws IOException {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    Path entryPath = extractFolder.toPath().resolve(entry.getName());
                    Files.createDirectories(entryPath.getParent());
                    Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    zipInputStream.closeEntry();
                }
            }
        }
    }

    private File findFile(File directory, String fileName) {
        Path dirPath = directory.toPath();

        try (Stream<Path> files = Files.walk(dirPath)) {
            Optional<Path> fileOptional = files
                    .filter(path -> path.toFile().isFile())
                    .filter(path -> path.getFileName().toString().endsWith(fileName))
                    .findFirst();

            if (fileOptional.isPresent()) {
                return fileOptional.get().toFile();
            }
        } catch (IOException e) {
            throw new StorageFileNotFoundException("Could not read file: " + fileName, e);
        }

        return null;
    }


}
