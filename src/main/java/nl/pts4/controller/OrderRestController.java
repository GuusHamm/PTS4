package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.OrderModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderRestController {

	@RequestMapping(value = "order-overview/ajax")
	public Map<String, Object> getOrders(HttpServletRequest httpServletRequest) {
		ArrayList<OrderModel> orders;
		AccountModel accountModel = MainController.getCurrentUser(httpServletRequest);
		if (accountModel.getAccountTypeEnum().equals(AccountModel.AccountTypeEnum.customer)) {
			orders = (ArrayList<OrderModel>) DatabaseController.getInstance().getAllAccountOrders(MainController.getCurrentUser(httpServletRequest).getUUID());
		} else {
			orders = (ArrayList<OrderModel>) DatabaseController.getInstance().getAllPhotographerOrders(MainController.getCurrentUser(httpServletRequest).getUUID());
		}

		Map<String, Object> mapping = new HashMap<>();
		mapping.put("data", orders);
		return mapping;
	}

}
