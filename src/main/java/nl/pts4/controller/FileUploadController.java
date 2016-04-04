package nl.pts4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.LinkedList;

/**
 * Created by guushamm on 23-3-16.
 */
@Controller
public class FileUploadController {
    private LinkedList<String> allowedFileTypes;

    public FileUploadController() {
        this.allowedFileTypes = new LinkedList<>();
        this.allowedFileTypes.add("image/png");
        this.allowedFileTypes.add("image/bmp");
        this.allowedFileTypes.add("image/jpg");
        this.allowedFileTypes.add("image/jpeg");
    }

    @RequestMapping(value = "/multiupload")
    public String multiUpload(Model m, HttpServletRequest request) {
        m.addAttribute("cart", request.getSession().getAttribute("Cart"));
        return "multiupload";
    }

    @RequestMapping(value = "/multiupload", method = RequestMethod.POST)
    public String uploadMultiFile(@RequestParam("file") MultipartFile[] files, HttpServletRequest request, Model m) {
        StringBuilder message = new StringBuilder();
        StringBuilder warning = new StringBuilder();

        if (files != null) {
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.getOriginalFilename().equals("")) {
                    if (allowedFileTypes.contains(multipartFile.getContentType())) {

                        if (!writeFile(multipartFile)) {
                            m.addAttribute("error", "Something went wrong on the server, try again later");
                            return "multiupload";
                        }
                        message.append(String.format("File: %s succesfully uploaded\n", multipartFile.getOriginalFilename()));
                    } else {
                        warning.append(String.format("File: %s is of a unsupported format\n", multipartFile.getOriginalFilename()));
                    }
                }
            }
            if (!warning.toString().equals("")) {
                m.addAttribute("warning", warning.toString());
            }
            if (!message.toString().equals("")) {
                m.addAttribute("success", message.toString());
            }
            m.addAttribute("cart", request.getSession().getAttribute("Cart"));
            return "multiupload";
        } else {
            m.addAttribute("error", "You have to select a file for upload.");
            return "multiupload";
        }
    }

    private boolean writeFile(MultipartFile file) {
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(String.format("%s/Uploads/%s", System.getProperty("user.home"), file.getOriginalFilename()))));
            bufferedOutputStream.write(file.getBytes());
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
