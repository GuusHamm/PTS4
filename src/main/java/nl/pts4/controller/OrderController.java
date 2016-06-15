package nl.pts4.controller;

import net.coobird.thumbnailator.filters.Watermark;
import net.coobird.thumbnailator.geometry.Positions;
import nl.pts4.email.EmailManager;
import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderLineDescriptionModel;
import nl.pts4.model.OrderModel;
import nl.pts4.model.PhotoModel;
import nl.pts4.model.ItemModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.print.DocFlavor;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
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
            return new RedirectView("order");
        }

        AccountModel user = MainController.getCurrentUser(request);
        Map<String, Object> map = new HashMap<>();
        map.put("photo", photo);
        map.put("effect", effect);
        map.put("item", item);
        map.put("user", user);
        List<OrderLineDescriptionModel> oldm = new ArrayList<>();

        DatabaseController.getInstance().createOrderModel(photo, effect, item, user.getUUID());

        redirectAttributes.addAttribute("cmd", "_cart");
        redirectAttributes.addAttribute("upload", "1");
        redirectAttributes.addAttribute("currency_code", "EUR");
        redirectAttributes.addAttribute("business", "woutie012006@hotmail.nl");
        OrderLineDescriptionModel model = new OrderLineDescriptionModel();

        int counter = 0;
        for (Map.Entry<String, String> entry : allRequestParams.entrySet()) {
            redirectAttributes.addAttribute(entry.getKey(),entry.getValue());

            if(entry.getKey().contains("item_name") ) {
                model = new OrderLineDescriptionModel(entry.getValue());
                if(entry.getValue().toLowerCase().contains("download")) {
                    PhotoModel pm = DatabaseController.getInstance().getPhotoByUUID(photo[counter]);
                    model.setShouldGetDigitalDownload(true);
                    model.setDigitalDownloadLink(pm.getFilePath());
                }
                counter ++;
            }
            if(entry.getKey().contains("amount_")){
                model.setAmount(entry.getValue());
                oldm.add(model);
            }
        }
        map.put("oldm", oldm);

        request.getSession().setAttribute(MainController.CART_ATTRIBUTE,null);

        emailManager.sendMessage("place-order2.vm", map, user.getEmail(), "Order Confirmation");

        return new RedirectView("https://www.sandbox.paypal.com/cgi-bin/webscr");

    }


    @RequestMapping(value = "/order/partofimage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE, params = {"photoUUID", "x", "y", "w", "h"})
    private @ResponseBody byte[] getPartOfImage(@RequestParam(value = "photoUUID") String photoUUID, @RequestParam(value = "x") int x, @RequestParam(value = "y") int y, @RequestParam(value = "w") int w, @RequestParam(value = "h") int h) {
        PhotoModel photoModel = DatabaseController.getInstance().getPhotoByUUID(UUID.fromString(photoUUID));

        String photoImageSource = "http://pts4.guushamm.tech/resources/" + photoModel.getFilePath();
        URL photoURL = null;
        BufferedImage photoImage = null;

        try {
            photoURL = new URL(photoImageSource);
            photoImage = ImageIO.read(photoURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (photoImage != null) {
            //Parameters:
            //x         get start x start at the right
            //y         get start y starts at the top
            //width     how much width
            //heigth    How much it should go down from the y parameter

            BufferedImage selectedImage = photoImage.getSubimage(x, y, w, h);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                ImageIO.write(selectedImage, "png", baos);
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] res = baos.toByteArray();
            return res;
        } else {
            return null;
        }
    }

    @RequestMapping(value = "/order/preview", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE, params = {"itemID", "photoUUID"})
    private @ResponseBody byte[] convertImageToByteArray(@RequestParam(value = "itemID") int itemID, @RequestParam(value = "photoUUID") String uuidString) {

        //http://pts4.guushamm.tech/order/preview?id=604773a5-b0c4-40ea-9483-00afc77841f9&rating=1

        ItemModel item = DatabaseController.getInstance().getItemByID(itemID);
        PhotoModel photoModel = DatabaseController.getInstance().getPhotoByUUID(UUID.fromString(uuidString));

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
        Watermark filter = new Watermark(Positions.CENTER, originalImage, 0.8f);
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
