package nl.pts4.model;

/**
 * Created by Teun on 16-3-2016.
 */
public class AccountRestModel {

    private boolean success;
    private String message;

    public AccountRestModel(String email, String password) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
