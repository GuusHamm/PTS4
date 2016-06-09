package nl.pts4.controller;

import nl.pts4.email.EmailManager;
import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderLineDescriptionModel;
import nl.pts4.model.OrderModel;
import nl.pts4.model.PhotoModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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

        emailManager.sendMessage("place-order.vm", map, user.getEmail(), "Order Confirmation");

        return new RedirectView("https://www.sandbox.paypal.com/cgi-bin/webscr");

    }
}
