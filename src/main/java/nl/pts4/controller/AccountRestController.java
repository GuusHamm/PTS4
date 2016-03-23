package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import nl.pts4.model.AccountRestModel;
import nl.pts4.model.SettingsRestModel;
import nl.pts4.security.HashConstants;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
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

    @RequestMapping(value = "/settings-rest", method = RequestMethod.POST)
    public SettingsRestModel settingsRest(@RequestParam(value = "name") String name,
                                          @RequestParam(value = "email") String email,
                                          @RequestParam(value = "oldPassword") String oldPassword,
                                          @RequestParam(value = "newPassword") String newPassword,
                                          @RequestParam(value = "newPasswordRepeat") String newPasswordRepeat,
                                          @CookieValue(value = AccountController.AccountCookie) String accountCookie) {
        AccountModel ac = DatabaseController.getInstance().getAccountByCookie(accountCookie);
        boolean hitChange = false, passwordInvalid = false;
        String message = "Data invalid or hasn't changed";

        if (!AccountController.checkPassword(ac, oldPassword)) {
            hitChange = false;
            message = "Password invalid";
            passwordInvalid = true;
        }

        if (!passwordInvalid) {

            if (!Objects.equals(oldPassword, "") && !Objects.equals(newPassword, "") && !Objects.equals(newPasswordRepeat, "")) {
                if (!Objects.equals(newPassword, newPasswordRepeat)) {
                    hitChange = false;
                    message = "Passwords not equal";
                }else{
                    String hash = SCryptUtil.scrypt(newPassword, HashConstants.N, HashConstants.r, HashConstants.p);
                    DatabaseController.getInstance().setAccountHash(ac, hash);
                    hitChange = true;
                    message = "Password has been changed";
                }
            }

            if (!Objects.equals(name, "") && !Objects.equals(ac.getName(), name)) {
                hitChange = true;
                message = "Name changed successfully from " + ac.getName() + " to " + name;
                DatabaseController.getInstance().setAccountName(ac, name);
            }

            if (!Objects.equals(email, "") && !Objects.equals(ac.getEmail(), email)) {
                hitChange = true;
                message = "Email changed successfully from " + ac.getEmail() + " to " + email;
                DatabaseController.getInstance().setAccountEmail(ac, email);
            }
        }

        return new SettingsRestModel(hitChange, message);
    }

}
