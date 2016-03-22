package nl.pts4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Teun on 20-3-2016.
 */
@Controller
public class MainController {

    public static final String TITLE_ATTRIBUTE = "title";

    @RequestMapping(value = "/")
    public String main(Model m, @CookieValue(value = AccountController.AccountCookie, required = false) String account) {
        m.addAttribute(MainController.TITLE_ATTRIBUTE, "Fotowinkel");
        m.addAttribute("user", DatabaseController.getInstance().getAccountByCookie(account));
        return "main";
    }

}
