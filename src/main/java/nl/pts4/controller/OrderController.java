package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderModel;
import nl.pts4.model.PhotoConfigurationModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * Created by GuusHamm on 16-3-2016.
 */
@Controller
public class OrderController {

	public OrderModel createNewOrder(AccountModel accountModel, PhotoConfigurationModel photoConfigurationModel){
		return null;
	}

	/**
	 * get all the orders and show them in order_overview
	 * @param m	: The model / template
     * @return order_overview to get the correct template
     */
	@RequestMapping(value = "/order-overview")
	public String orderView(
			@CookieValue(AccountController.AccountCookie) String am,
			Model m, HttpServletRequest request) {
		ArrayList<OrderModel> orders = new ArrayList<>();
		orders = (ArrayList<OrderModel>) DatabaseController.getInstance().getAllOrders();

		request.getSession();
//		Add some items to the orders list to show them
		m.addAttribute(MainController.TITLE_ATTRIBUTE, "Order overview");
		m.addAttribute("allOrders", orders);
		m.addAttribute(AccountController.AccountModelKey, DatabaseController.getInstance().getAccountByCookie(am));
		m.addAttribute("cart", request.getSession().getAttribute("Cart"));

		return "order-overview";
	}
}
