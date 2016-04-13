package nl.pts4.model;

import nl.pts4.controller.AccountController;
import nl.pts4.controller.DatabaseController;
import org.springframework.context.MessageSource;

import java.util.Locale;

/**
 * Created by Teun on 16-3-2016.
 */
public class AccountRestModel {

    private boolean success;
    private String message = "No message specified";

    public AccountRestModel(String email, String password, Locale locale, MessageSource messageSource) {
        AccountModel accountModel = DatabaseController.getInstance().getAccount(email);
        boolean correct = AccountController.checkPassword(accountModel, password);
        if (!correct) {
            message = messageSource.getMessage("error.authentication", null, locale);
        } else {
            message = messageSource.getMessage("login.success", null, locale);
            success = true;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
