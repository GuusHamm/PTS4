package nl.pts4.controller;

import net.coobird.thumbnailator.filters.Caption;
import net.coobird.thumbnailator.geometry.Position;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by guushamm on 23-3-16.
 */
@Controller
public class FileUploadController {
    public final static String StaticLocation = "/src/main/img/";
    public final static String ImageLocation = String.format("%s/src/main/resources/static/images/", System.getProperty("user.dir"));
    private List<String> allowedFileTypes;

    public FileUploadController() {
        this.allowedFileTypes = new ArrayList<>(5);
        this.allowedFileTypes.add("image/png");
        this.allowedFileTypes.add("image/bmp");
        this.allowedFileTypes.add("image/jpg");
        this.allowedFileTypes.add("image/jpeg");
        this.allowedFileTypes.add("image/gif");
    }

    @RequestMapping(value = "/multiupload")
    public String multiUpload(Model m, HttpServletRequest request, HttpServletResponse response) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) {
            return null;
        }

        m = MainController.addDefaultAttributesToModel(m, "Upload a file", request, response);
        return "multiupload";
    }

    @RequestMapping(value = "/multiupload", method = RequestMethod.POST)
    public String uploadMultiFile(@RequestParam("file") MultipartFile[] files, HttpServletRequest request, HttpServletResponse response, Model m) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) {
            return null;
        }

        m = MainController.addDefaultAttributesToModel(m, "Upload a file", request, response);

        StringBuilder message = new StringBuilder();
        StringBuilder warning = new StringBuilder();

        UUID uuid = UUID.randomUUID();

        if (files != null) {
            for (MultipartFile multipartFile : files) {
                if (multipartFile.isEmpty()) {
                    continue;
                }
                if (allowedFileTypes.contains(multipartFile.getContentType())) {
                    String fileName = writeFile(multipartFile, uuid);
                    String fileNameLowRes = writeBufferedImage(multipartFile, uuid);
                    if (fileName.isEmpty() || fileNameLowRes.isEmpty()) {
                        m.addAttribute("error", "Something went wrong on the server, try again later");
                        return "multiupload";
                    }
                    message.append(String.format("File: %s succesfully uploaded\n", multipartFile.getOriginalFilename()));
                    // TODO Check last parameter
                    DatabaseController.getInstance().createPhoto(uuid, fileName, MainController.getCurrentUser(request).getUUID(), DatabaseController.getInstance().getRandomChildUUID(), fileNameLowRes);
                } else {
                    warning.append(String.format("File: %s is of a unsupported format\n", multipartFile.getOriginalFilename()));
                }
            }
            if (warning.length() > 0) {
                m.addAttribute("warning", warning.toString());
            }
            if (message.length() > 0) {
                m.addAttribute("success", message.toString());
            }
        } else {
            m.addAttribute("error", "You have to select a multiPartFile for upload.");
        }
        m.addAttribute("cart", request.getSession().getAttribute("Cart"));

        return "multiupload";
    }

    public String uploadItemThumbnail(MultipartFile file) {
        UUID uuid = UUID.randomUUID();
        String fileName = "";

        if (file != null && !file.isEmpty() && allowedFileTypes.contains(file.getContentType())) {
            fileName = writeFile(file, uuid);
        }
        return fileName;

    }

    private String writeFile(MultipartFile multiPartFile, UUID uuid) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String filename = String.format("%s_%s.%s", simpleDateFormat.format(new Date()), uuid, multiPartFile.getContentType().substring(multiPartFile.getContentType().indexOf("/") + 1));

        try {
            File file = new File(String.format("%s/src/main/resources/static/images/%s", System.getProperty("user.dir"), filename));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            bufferedOutputStream.write(multiPartFile.getBytes());
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

    private String writeBufferedImage(MultipartFile multiPartFile, UUID uuid) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String filename = String.format("%s_%s.%s", simpleDateFormat.format(new Date()), uuid + "LowRes", multiPartFile.getContentType().substring(multiPartFile.getContentType().indexOf("/") + 1));
        System.out.println(filename);
        System.out.println();
        try {
            File file = new File(String.format("%s/src/main/resources/static/images/%s", System.getProperty("user.dir"), filename));
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            BufferedImage bufferedImage = resizeImageFromFile(convertMultipartFile(multiPartFile), 640, 480);

            // Set up the caption properties
            String caption = "PhotoShop";
            Font font = new Font("Monospaced", Font.PLAIN, 42);
            Color c = Color.black;
            Position position = Positions.CENTER;
            int insetPixels = 0;
            // Apply caption to the image
            Caption filter = new Caption(caption, font, c, position, insetPixels);
            BufferedImage captionedImage = filter.apply(bufferedImage);



            ImageIO.write(captionedImage, "jpg", bufferedOutputStream);

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

    @Deprecated
    private boolean convertFileToJpg(String filePath) {
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(filePath.substring(0, filePath.indexOf(".")) + ".jpg");
            BufferedImage bufferedImage = ImageIO.read(fileInputStream);
            ImageIO.write(bufferedImage, "JPG", fileOutputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private File convertMultipartFile(MultipartFile multipartFile) {
        FileOutputStream fos = null;
        File convFile = new File(multipartFile.getOriginalFilename());
        try {
            convFile.createNewFile();
            fos = new FileOutputStream(convFile);
            fos.write(multipartFile.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return convFile;
    }


    public BufferedImage resizeImageFromFile(File file, int width, int height) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int type;
        type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

}
