package nl.pts4.controller;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import nl.pts4.model.AccountModel;
import nl.pts4.model.AccountRestModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.text.CollationKey;
import java.util.UUID;

/**
 * @author Teun
 */
@RestController
public class AccountRestController {

    private final int WEEK = 60 * 60 * 24 * 7;

    @RequestMapping(value = "/login-rest", method = RequestMethod.POST)
    public AccountRestModel loginRest(@RequestParam(value = "email", required = true) String email,
                                      @RequestParam(value = "password", required = true) String password,
                                      HttpServletResponse response) {
        AccountRestModel arm = new AccountRestModel(email, password);
        if (arm.isSuccess()) {
            String id = UUID.randomUUID().toString();
            Cookie session = new Cookie(AccountController.AccountCookie, id);
            session.setMaxAge(WEEK);
            response.addCookie(session);
        }
        return arm;
    }

}
