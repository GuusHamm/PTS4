package nl.pts4.controller;

import nl.pts4.model.OrderModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

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

        // Add some items to the orders list to show them
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Order overview");
        m.addAttribute("allOrders", orders);
        m.addAttribute(AccountController.AccountModelKey, MainController.getCurrentUser(request));
        m.addAttribute("cart", request.getSession().getAttribute("Cart"));

        return "order-overview";
    }

    @RequestMapping(value = "/order")
    public String order(Model m, HttpServletRequest request) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Order");
        m.addAttribute("cart", request.getSession().getAttribute("Cart"));
        m.addAttribute("effects", DatabaseController.getInstance().getEffects());
        m.addAttribute("items", DatabaseController.getInstance().getItems());
        m.addAttribute(AccountController.AccountModelKey, MainController.getCurrentUser(request));
        return "order";
    }
}
