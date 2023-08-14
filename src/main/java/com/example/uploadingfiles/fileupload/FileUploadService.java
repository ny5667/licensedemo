package com.example.uploadingfiles.fileupload;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    String fileUpload(MultipartFile multipartFile);

}
