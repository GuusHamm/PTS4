package nl.pts4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
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
    public String multiUpload() {
        return "multiupload";
    }

    @RequestMapping(value = "/multiupload", method = RequestMethod.POST)
    public String uploadMultiFile(@RequestParam("file") MultipartFile[] files, @RequestParam("number") HttpServletResponse response, Model m, @CookieValue(AccountController.AccountCookie) String accountCookie) {
        StringBuilder message = new StringBuilder();
        StringBuilder warning = new StringBuilder();

        if (files != null) {
            for (MultipartFile multipartFile : files) {
                if (!multipartFile.getOriginalFilename().equals("")) {
                    if (allowedFileTypes.contains(multipartFile.getContentType())) {
                        UUID uuid = UUID.randomUUID();
                        Date date = new Date();
                        String path = writeFile(multipartFile, uuid, date);
                        if (!path.isEmpty()) {
                            DatabaseController.getInstance().createPhoto(uuid, path, DatabaseController.getInstance().getAccountByCookie(accountCookie).getUuid(), )
                        } else {
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
            return "multiupload";
        } else {
            m.addAttribute("error", "You have to select a file for upload.");
            return "multiupload";
        }
    }

    private String writeFile(MultipartFile file, UUID uuid, Date date) {
        String path = String.format("%s/Uploads/%s_%s", date.toString(), uuid);
        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(path)));
            bufferedOutputStream.write(file.getBytes());
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return path;
    }

}
