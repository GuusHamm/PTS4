package nl.pts4.socket;

/**
 * Created by Teun on 18-3-2016.
 */
public class SocketLoginAttempt {

    private String email;
    private String password;

    public SocketLoginAttempt(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public SocketLoginAttempt() {

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
