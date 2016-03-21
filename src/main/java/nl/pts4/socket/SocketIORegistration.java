package nl.pts4.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.lambdaworks.crypto.SCryptUtil;
import nl.pts4.FotowinkelSpringApplication;
import nl.pts4.controller.DatabaseController;
import nl.pts4.model.AccountModel;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by Teun on 17-3-2016.
 */
public class SocketIORegistration {

    private static SocketIORegistration socketIORegistration;

    public static void start() {
        if (socketIORegistration == null) socketIORegistration = new SocketIORegistration();
    }

    private SocketIORegistration() {
        SocketIOServer server = new SocketIOServer(getConfiguration());
        addEvents(server);
        server.start();
    }

    private void addEvents(SocketIOServer server) {
        // Registration email check
        server.addEventListener("emailCheck", String.class, (socketIOClient, email, ackRequest) -> {
            AccountModel am = DatabaseController.getInstance().getAccount(email);
            if (am != null) {
                socketIOClient.sendEvent("emailTaken", email);
            } else {
                socketIOClient.sendEvent("emailOk", email);
            }
        });
    }

    private Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8081);
        return config;
    }
}