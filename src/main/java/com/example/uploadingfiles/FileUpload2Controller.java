package com.example.uploadingfiles;

import com.example.uploadingfiles.fileupload.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class FileUpload2Controller {


    private final FileUploadService fileUploadService;

    @Autowired
    public FileUpload2Controller(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        return "fileUpload";
    }

    @PostMapping("/")
    public String fileUpload(@RequestParam("file") MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        String fileContent = fileUploadService.fileUpload(file);
        redirectAttributes.addFlashAttribute("message",
                fileContent);
        return "redirect:/";
    }

}
