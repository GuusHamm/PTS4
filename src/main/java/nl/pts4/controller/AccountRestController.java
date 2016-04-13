package nl.pts4.controller;

import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.model.AccountModel;
import nl.pts4.model.AccountRestModel;
import nl.pts4.model.SettingsRestModel;
import nl.pts4.security.HashConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Teun
 */
@RestController
public class AccountRestController {

    @Autowired
    public MessageSource messageSource;

    /**
     * confirms if you can log in
     *
     * @param email    : The email which you use to log in
     * @param password : The password from the account
     * @return The AccountRestModel that the javascript will use to validate the login
     */
    @RequestMapping(value = "/login-rest", method = RequestMethod.POST)
    public AccountRestModel loginRest(@RequestParam(value = "email") String email,
                                      @RequestParam(value = "password") String password,
                                      HttpServletRequest request) {
        AccountRestModel arm = new AccountRestModel(email, password, request.getLocale(), messageSource);
        if (arm.isSuccess()) {
            AccountModel accountModel = DatabaseController.getInstance().getAccount(email);
            request.getSession().setAttribute(MainController.ACCOUNT_ATTRIBUTE, accountModel);
        }
        return arm;
    }

    /**
     * Changing your account variables
     *
     * @param name              : The username from the user
     * @param email             : The email from the user
     * @param oldPassword       : The old password for if you want to change your password
     * @param newPassword       : The new password
     * @param newPasswordRepeat : The new password once again for confirmation
     * @return
     */
    @RequestMapping(value = "/settings-rest", method = RequestMethod.POST)
    public SettingsRestModel settingsRest(@RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "email", required = false) String email,
                                          @RequestParam(value = "oldPassword", required = false) String oldPassword,
                                          @RequestParam(value = "newPassword", required = false) String newPassword,
                                          @RequestParam(value = "newPasswordRepeat", required = false) String newPasswordRepeat,
                                          @RequestParam(value = "theme", required = false) String theme,
                                          HttpServletRequest request) {
        AccountModel ac = MainController.getCurrentUser(request);
        boolean hitChange = false, passwordInvalid = false;
        String message = "Data invalid or hasn't changed";

        if (theme != null && !Objects.equals(theme, "") && !Objects.equals(ac.getTheme(), theme)) {
            hitChange = true;
            message = "Theme changed from " + ac.getTheme() + " to " + theme;
            DatabaseController.getInstance().setAccountTheme(ac, theme);
            return new SettingsRestModel(hitChange, message);
        }

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
                } else {
                    String hash = SCryptUtil.scrypt(newPassword, HashConstants.N, HashConstants.R, HashConstants.P);
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
