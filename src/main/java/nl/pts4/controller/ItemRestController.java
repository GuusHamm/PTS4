package nl.pts4.controller;

import nl.pts4.model.PriceModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ItemRestController {

    @RequestMapping("/order/price")
    public PriceModel getPrice(HttpServletRequest request) {
        return PriceModel.readAll(request);
    }

}
