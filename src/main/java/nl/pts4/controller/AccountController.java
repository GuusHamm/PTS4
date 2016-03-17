package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import nl.pts4.model.AccountRestModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

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
        m.addAttribute("title", "Register");
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(@RequestParam(value = "email", required = true) String email,
                           @RequestParam(value = "password", required = true) String password,
                           @RequestParam(value = "name", required = true) String name,
                           HttpServletResponse response,
                           Model m) {
        AccountModel accountModel = DatabaseController.getInstance().createAccount(name, email, password);
        UUID id = UUID.randomUUID();
        Cookie session = new Cookie(AccountController.AccountCookie, id.toString());
        session.setMaxAge(AccountRestController.WEEK);
        response.addCookie(session);

        DatabaseController.getInstance().createUserCookie(accountModel, id);

        m.addAttribute("title", "Register");
        return "register";
    }

    public static boolean checkPassword(AccountModel account, String password) {
        if (account == null) return false;
        return SCryptUtil.check(password, account.getHash());
    }
}
