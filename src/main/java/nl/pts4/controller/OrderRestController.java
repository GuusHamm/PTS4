package nl.pts4.controller;

import nl.pts4.model.OrderModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderRestController {

    @RequestMapping(value = "order-overview/ajax")
    public Map<String, Object> getOrders() {
        ArrayList<OrderModel> orders = (ArrayList<OrderModel>) DatabaseController.getInstance().getAllOrders();
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("data", orders);
        return mapping;
    }

}
