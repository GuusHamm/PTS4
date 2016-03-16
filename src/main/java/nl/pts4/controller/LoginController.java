package nl.pts4.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Teun on 16-3-2016.
 */
@Controller
public class LoginController {

    @RequestMapping("/login")
    public String login(Model m) {
        return "login";
    }

    @RequestMapping("/register")
    public String register(Model m) {
        return "register";
    }

}
