package nl.pts4.controller;

import net.coobird.thumbnailator.filters.Watermark;
import net.coobird.thumbnailator.geometry.Positions;
import nl.pts4.email.EmailManager;
import nl.pts4.model.AccountModel;
import nl.pts4.model.ItemModel;
import nl.pts4.model.PhotoModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
@Controller
public class OrderController {

    private EmailManager emailManager = new EmailManager();


    /**
     * get all the orders and show them in order_overview
     *
     * @param m : The model / template
     * @return order_overview to get the correct template
     */
    @RequestMapping(value = "/order-overview")
    public String orderView(Model m, HttpServletRequest request, HttpServletResponse response) {
        if (!MainController.assertUserIsPrivileged(request, response, true)) {
            return null;
        }
        m = MainController.addDefaultAttributesToModel(m, "Orders", request, response);

        return "order-overview";
    }

    @RequestMapping(value = "/order")
    public String order(Model m, HttpServletRequest request, HttpServletResponse response) {
        MainController.assertUserIsSignedIn(request, response);
        m = MainController.addDefaultAttributesToModel(m, "Order", request, response);

        m.addAttribute("effects", DatabaseController.getInstance().getEffects());
        m.addAttribute("items", DatabaseController.getInstance().getItems());

        return "order";
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public RedirectView placeOrder(@RequestParam UUID[] photo, @RequestParam int[] effect, @RequestParam int[] item, Model m, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, @RequestParam Map<String, String> allRequestParams) throws IOException {
        if (!MainController.assertUserIsSignedIn(request, response)) {
            return null;
        }
        m = MainController.addDefaultAttributesToModel(m, "Order", request, response);

        if (!(photo.length == item.length) && !(item.length == effect.length)) {
            m.addAttribute(MainController.ERROR_ATTRIBUTE, "Well congrats, you like breaking things don't you");
//            return "order";
            return new RedirectView("order");
        }

        AccountModel user = MainController.getCurrentUser(request);
        Map<String, Object> map = new HashMap<>();
        map.put("photo", photo);
        map.put("effect", effect);
        map.put("item", item);
        map.put("user", user);

        int id = DatabaseController.getInstance().createOrderModel(photo, effect, item, user.getUUID());
        request.getSession().setAttribute(MainController.CART_ATTRIBUTE,null);
        emailManager.sendMessage("place-order.vm", map, user.getEmail(), "Order Confirmation");
        redirectAttributes.addAttribute("cmd", "_cart");
        redirectAttributes.addAttribute("upload", "1");
        redirectAttributes.addAttribute("business", "woutie012006@hotmail.nl");

        for (Map.Entry<String, String> entry : allRequestParams.entrySet()) {
            redirectAttributes.addAttribute(entry.getKey(),entry.getValue());
            System.out.println(entry.getKey()+entry.getValue() );
        }

        return new RedirectView("https://www.sandbox.paypal.com/cgi-bin/webscr");

    }

    @RequestMapping(value = "/order/preview", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
    private @ResponseBody byte[] convertImageToByteArray() {
        ItemModel item = DatabaseController.getInstance().getItemByID(4);
        PhotoModel photoModel = DatabaseController.getInstance().getPhotoByUUID(UUID.fromString("604773a5-b0c4-40ea-9483-00afc77841f9"));

        BufferedImage itemImage = null;
        BufferedImage overlayImage = null;


        String itemImagesource = "http://pts4.guushamm.tech/resources/" + item.getThumbnailPath();
        String photoImageSource = "http://pts4.guushamm.tech/resources/" + photoModel.getFilePathLowRes();

        URL itemUrl = null;
        URL photoURL = null;
        try {
            itemUrl = new URL(itemImagesource);
            photoURL = new URL(photoImageSource);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (itemUrl != null && photoURL != null) {
            try {
                itemImage = ImageIO.read(itemUrl);
                overlayImage = ImageIO.read(photoURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BufferedImage finalImage = putImageOverItem(overlayImage, itemImage);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(finalImage, "png", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] res = baos.toByteArray();
        return res;
    }

    private BufferedImage putImageOverItem(BufferedImage originalImage, BufferedImage itemImage) {
        if (originalImage == null || itemImage == null) {
            return null;
        }
        Watermark filter = new Watermark(Positions.CENTER, originalImage, 1f);
        return filter.apply(itemImage);
    }


//    public BufferedImage resizeImageFromFile(File file, int width, int height) {
//        BufferedImage image = null;
//
//        try {
//            image = ImageIO.read(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        int type;
//        type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image.getType();
//        BufferedImage resizedImage = new BufferedImage(width, height, type);
//        Graphics2D g = resizedImage.createGraphics();
//        g.drawImage(image, 0, 0, width, height, null);
//        g.dispose();
//        return resizedImage;
//    }
}
