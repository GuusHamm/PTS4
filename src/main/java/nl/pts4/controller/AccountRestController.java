package nl.pts4.controller;

import nl.pts4.model.AccountRestModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Teun
 */
@RestController
public class AccountRestController {

    @RequestMapping(value = "/login-rest", method = RequestMethod.POST)
    public AccountRestModel loginRest(@RequestParam(value = "email", required = true) String email,
                                      @RequestParam(value = "password", required = true) String password) {
        return new AccountRestModel(email, password);
    }

}
