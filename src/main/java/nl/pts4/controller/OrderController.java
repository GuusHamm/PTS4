package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderModel;
import nl.pts4.model.PhotoConfigurationModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

/**
 * Created by GuusHamm on 16-3-2016.
 */
@Controller
public class OrderController {

	public OrderModel createNewOrder(AccountModel accountModel, PhotoConfigurationModel photoConfigurationModel){
		return null;
	}

	@RequestMapping(value = "/order-overview")
	public String orderView(Model m) {
		ArrayList<OrderModel> orders = new ArrayList<>();
		orders = (ArrayList<OrderModel>) DatabaseController.getInstance().getAllOrders();
//		Add some items to the orders list to show them
		m.addAttribute(MainController.TITLE_ATTRIBUTE, "Order overview");
		m.addAttribute("allOrders", orders);

		return "order-overview";
	}
}
