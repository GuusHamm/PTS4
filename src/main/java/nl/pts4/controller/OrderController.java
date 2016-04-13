package nl.pts4.controller;

import nl.pts4.model.OrderModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by GuusHamm on 16-3-2016.
 */
@Controller
public class OrderController {

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
        ArrayList<OrderModel> orders = (ArrayList<OrderModel>) DatabaseController.getInstance().getAllOrders();

        m = MainController.addDefaultAttributesToModel(m, "Orders", request, response);
        m.addAttribute("allOrders", orders);

        return "order-overview";
    }

    @RequestMapping(value = "/order")
    public String order(Model m, HttpServletRequest request, HttpServletResponse response) {
        m = MainController.addDefaultAttributesToModel(m, "Order", request, response);

        m.addAttribute("effects", DatabaseController.getInstance().getEffects());
        m.addAttribute("items", DatabaseController.getInstance().getItems());

        return "order";
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public String placeOrder(@RequestParam UUID[] photo, @RequestParam int[] effect, @RequestParam int[] item, Model m, HttpServletRequest request, HttpServletResponse response) {
        m = MainController.addDefaultAttributesToModel(m, "Order", request, response);

        if (!(photo.length == item.length) && !(item.length == effect.length)) {
            m.addAttribute(MainController.ERROR_ATTRIBUTE, "Well congrats, you like breaking things don't you");
        }


        DatabaseController.getInstance().createOrderModel(photo, effect, item, MainController.getCurrentUser(request).getUUID());
        return "order";
    }
}
