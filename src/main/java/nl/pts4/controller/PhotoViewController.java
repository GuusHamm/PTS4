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
import org.springframework.web.bind.annotation.RequestMethod;
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
        if (!MainController.assertUserIsSignedIn(request,response)){
            return null;
        }

        AccountModel accountModel = MainController.getCurrentUser(request);
        List<PhotoModel> photos = DatabaseController.getInstance().getPhotosOfAccount(accountModel.getUUID());

        m = MainController.addDefaultAttributesToModel(m, "Photos", request, response);

        if (photos.size() > 0){
            m.addAttribute("photos", photos.toArray(new PhotoModel[photos.size()]));
        }

        return "photoview";
    }

    /**
     * Show all the photos
     *
     * @param m       : The model / template
     * @return photoview to get the correct template
     */
    @RequestMapping("/photographer/photos")
    public String photograherPhotosGet(Model m, HttpServletRequest request, HttpServletResponse response) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) {
            return null;
        }

        AccountModel accountModel = MainController.getCurrentUser(request);
        List<PhotoModel> photos = DatabaseController.getInstance().getPhotosOfPhotographer(accountModel.getUUID());

        m = MainController.addDefaultAttributesToModel(m, "Photos", request, response);
        m.addAttribute("photos", photos.toArray(new PhotoModel[photos.size()]));



        return "photoview";
    }

    @RequestMapping(value = "/photos", params = {"id"})
    public void photosAddToCart(@RequestParam(value = "id", required = false) UUID id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!MainController.assertUserIsSignedIn(request, response)) {
            return;
        }
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

        response.sendRedirect(request.getHeader("referer"));
    }

    @RequestMapping(value = "/remove_photos", params = {"id"})
    public void photosRemoveFromCart(@RequestParam(value = "id", required = false) UUID id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LinkedList<PhotoModel> cart;
        if (request.getSession().getAttribute(MainController.CART_ATTRIBUTE) == null) {
            cart = new LinkedList<>();
        } else {
            cart = (LinkedList<PhotoModel>) request.getSession().getAttribute(MainController.CART_ATTRIBUTE);
        }
        if (id != null) {
            for(PhotoModel item: cart) {

                if (item.getUuid().equals(id)){
                    cart.remove(item);
                    break;
                }
            }
            request.getSession().setAttribute(MainController.CART_ATTRIBUTE, cart);
        }
        // TODO Add translation
        request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, "Photo succesfully removed from your cart");

        response.sendRedirect(request.getHeader("referer"));
    }

    @RequestMapping(value = "changephoto", params = {"id"})
    public String changePhoto(Model m, @RequestParam(value = "id", required = false) UUID id, HttpServletRequest request, HttpServletResponse response) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) {
            return null;
        }

        MainController.addDefaultAttributesToModel(m, "Change photo", request, response);
        m.addAttribute("photo", DatabaseController.getInstance().getPhotoByUUID(id));
        return "change_photo";
    }

    @RequestMapping(value = "ratephoto", params = {"id","rating"})
    public String addRating(Model m, @RequestParam(value = "id") UUID id, @RequestParam(value = "rating") int rating, HttpServletRequest request, HttpServletResponse response) {
		if (!MainController.assertUserIsSignedIn(request, response)) {
			return null;
		}

        MainController.addDefaultAttributesToModel(m, "Rate photo", request, response);

        if (rating != 1 && rating != -1){
            m.addAttribute(MainController.ERROR_ATTRIBUTE,"Whoops that's not a valid rating");
            return "photo";
        }

        if (DatabaseController.getInstance().insertRating(MainController.getCurrentUser(request).getUUID(),id,rating)) {
            // TODO Add translation
            if (rating == 1){
                request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, "Photo Upvoted Succesfully");
            }else if (rating == -1){
                request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, "Photo Downvoted Succesfully");
            }
		} else {
			// TODO Add translation
			request.getSession().setAttribute(MainController.ERROR_ATTRIBUTE, "Oops something went wrong");
		}

		try {
			response.sendRedirect("/photos");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
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
        request.getSession().removeAttribute(MainController.CART_ATTRIBUTE);
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

    @RequestMapping(value = "changephoto", method = RequestMethod.POST)
    public String changePhoto(
            @RequestParam(value = "photoId") UUID id,
            @RequestParam(value = "price") double price,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) return null;

        if (price <= 0) {
            model.addAttribute(MainController.ERROR_ATTRIBUTE, messageSource.getMessage("change.photoError", null, request.getLocale()) + String.valueOf(price));
            return "change_photo";
        }
        DatabaseController.getInstance().updatePhotoPrice(id, price);


        return "photos";
    }

}
