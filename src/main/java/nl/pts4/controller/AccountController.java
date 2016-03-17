package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by Teun on 16-3-2016.
 */
@Controller
public class AccountController {

    public static final String AccountCookie = "ACC_SESSION";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model m){
        m.addAttribute("title", "Login");
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(value = "email", required = true) String email,
                        @RequestParam(value = "password", required = true) String password,
                        Model m) {
        m.addAttribute("title", "Login");
        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register(Model m) {
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam(value = "email", required = true) String email,
                           @RequestParam(value = "password", required = true) String password,
                           @RequestParam(value = "name", required = true) String name,
                           Model m) {
        m.addAttribute("title", "Register");
        return "register";
    }

    public static boolean checkPassword(AccountModel account, String password) {
        if (account == null) return false;
        return SCryptUtil.check(password, account.getHash());
    }
}
