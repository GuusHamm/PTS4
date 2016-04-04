package nl.pts4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

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

        UUID uuid = UUID.randomUUID();

        if (files != null) {
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.getOriginalFilename().equals("")) {
                    if (allowedFileTypes.contains(multipartFile.getContentType())) {
                        String fileName = writeFile(multipartFile, uuid);
                        if (fileName.isEmpty()) {
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

    private String writeFile(MultipartFile file, UUID uuid) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String filename = String.format("%s_%s.%s", simpleDateFormat.format(new Date()), uuid, file.getContentType().substring(file.getContentType().indexOf("/") + 1));

        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(String.format("%s/Uploads/%s", System.getProperty("user.home"), filename))));
            bufferedOutputStream.write(file.getBytes());
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return filename;
    }

}
