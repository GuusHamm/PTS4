package nl.pts4.model;

/**
 * Created by Teun on 23-3-2016.
 */
public class SettingsRestModel {

    private boolean result;
    private String message;

    public SettingsRestModel(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public boolean isResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }
}
