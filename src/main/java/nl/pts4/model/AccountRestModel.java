package nl.pts4.model;

import nl.pts4.controller.AccountController;
import nl.pts4.controller.DatabaseController;

/**
 * Created by Teun on 16-3-2016.
 */
public class AccountRestModel {

    private boolean success;
    private String message = "No message specified";

    public AccountRestModel(String email, String password) {
        AccountModel accountModel = DatabaseController.getInstance().getAccount(email);
        boolean correct = AccountController.checkPassword(accountModel, password);
        if (!correct) {
            message = "Incorrect username or password";
        }
        else {
            message = "Succesful login";
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
