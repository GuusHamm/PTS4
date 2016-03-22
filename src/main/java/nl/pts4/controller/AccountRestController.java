package nl.pts4.controller;

import nl.pts4.model.AccountModel;
import nl.pts4.model.AccountRestModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author Teun
 */
@RestController
public class AccountRestController {

    public static final int WEEK = 60 * 60 * 24 * 7;

    @RequestMapping(value = "/login-rest", method = RequestMethod.POST)
    public AccountRestModel loginRest(@RequestParam(value = "email") String email,
                                      @RequestParam(value = "password") String password,
                                      HttpServletResponse response) {
        AccountRestModel arm = new AccountRestModel(email, password);
        if (arm.isSuccess()) {
            UUID id = UUID.randomUUID();
            Cookie session = new Cookie(AccountController.AccountCookie, id.toString());
            session.setMaxAge(WEEK);
            response.addCookie(session);

            AccountModel am = DatabaseController.getInstance().getAccount(email);
            DatabaseController.getInstance().createUserCookie(am, id);
        }
        return arm;
    }

}
