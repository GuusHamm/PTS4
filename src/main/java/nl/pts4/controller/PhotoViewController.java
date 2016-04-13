package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.PhotoModel;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Teun on 23-3-2016.
 */
@Controller
public class PhotoViewController {

    @Autowired
    private MessageSource messageSource;

    /**
     * Show all the photos
     *
     * @param m       : The model / template
     * @return photoview to get the correct template
     */
    @RequestMapping("/photos")
    public String photosGet(Model m, HttpServletRequest request, HttpServletResponse response) {
        AccountModel accountModel = MainController.getCurrentUser(request);
        List<PhotoModel> photos = DatabaseController.getInstance().getPhotos();

        m = MainController.addDefaultAttributesToModel(m, "Photos", request, response);
        m.addAttribute("photos", photos.toArray(new PhotoModel[photos.size()]));



        return "photoview";
    }

    @RequestMapping(value = "/photos", params = {"id"})
    public void photosAddToCart(@RequestParam(value = "id", required = false) UUID id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LinkedList<PhotoModel> cart;
        if (request.getSession().getAttribute(MainController.CART_ATTRIBUTE) == null) {
            cart = new LinkedList<>();
        } else {
            cart = (LinkedList<PhotoModel>) request.getSession().getAttribute(MainController.CART_ATTRIBUTE);
        }
        if (id != null) {
            cart.add(DatabaseController.getInstance().getPhotoByUUID(id));
            request.getSession().setAttribute(MainController.CART_ATTRIBUTE, cart);
        }
        request.getSession().setAttribute(MainController.CART_ATTRIBUTE, cart);
        // TODO Add translation
        request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, "Photo succesfully added to your cart");

        response.sendRedirect("/photos");
    }

    @RequestMapping(value = "changephoto", params = {"id"})
    public String changePhoto(@RequestParam(value = "id", required = false) UUID id, HttpServletRequest request, HttpServletResponse response) {
        return "change_photo";
    }

    @RequestMapping(value = "deletephoto", params = {"id"})
    public void deletePhoto(@RequestParam(value = "id", required = false) UUID id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (id != null) {
            if (DatabaseController.getInstance().deletePhoto(id)) {
                // TODO Add translation
                request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, "Photo Succesfully Removed");
            } else {
                // TODO Add translation
                request.getSession().setAttribute(MainController.ERROR_ATTRIBUTE, "Oops something went wrong");
            }
        }

        response.sendRedirect("/photos");
    }

    @RequestMapping(value = "/clearcart")
    public void clearCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("Cart");
        response.sendRedirect(request.getHeader("referer"));
    }

    public BufferedImage reduceImageQuility(File imageSource) throws ImageReadException, IOException {
        BufferedImage image = Imaging.getBufferedImage(imageSource);

        BufferedImage resizedImage = resizeImage(image, image.getWidth() / 10, image.getHeight() / 10);

        return resizedImage;
    }

    public BufferedImage reduceImageQuility(BufferedImage imageSource) throws ImageReadException, IOException {
        BufferedImage resizedImage = resizeImage(imageSource, imageSource.getWidth() / 10, imageSource.getHeight() / 10);
        return resizedImage;
    }

    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        int type;
        type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

}
