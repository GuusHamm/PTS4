package nl.pts4.controller;

import nl.pts4.email.EmailManager;
import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        MainController.assertUserIsPrivileged(request, response, true);
        m = MainController.addDefaultAttributesToModel(m, "Order", request, response);

        m.addAttribute("effects", DatabaseController.getInstance().getEffects());
        m.addAttribute("items", DatabaseController.getInstance().getItems());

        return "order";
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public String placeOrder(@RequestParam UUID[] photo, @RequestParam int[] effect, @RequestParam int[] item, Model m, HttpServletRequest request, HttpServletResponse response) throws IOException {
        MainController.assertUserIsPrivileged(request, response, true);
        m = MainController.addDefaultAttributesToModel(m, "Order", request, response);

        if (!(photo.length == item.length) && !(item.length == effect.length)) {
            m.addAttribute(MainController.ERROR_ATTRIBUTE, "Well congrats, you like breaking things don't you");
            return "order";
        }

        AccountModel user = MainController.getCurrentUser(request);
        Map<String, Object> map = new HashMap<>();
        map.put("photo", photo);
        map.put("effect", effect);
        map.put("item", item);
        map.put("user", user);

        int id = DatabaseController.getInstance().createOrderModel(photo, effect, item, user.getUUID());

        emailManager.sendMessage("place-order.vm", map, user.getEmail(), "Order Confirmation");

        request.getSession().setAttribute(MainController.SUCCESS_ATTRIBUTE, "Succesfully placed order, order number is " + id);

        request.getSession().setAttribute(MainController.CART_ATTRIBUTE, null);

        m = MainController.addDefaultAttributesToModel(m, "Order", request, response);

        response.sendRedirect("/https://www.sandbox.paypal.com/cgi-bin/webscr");
        return null;
    }
}
