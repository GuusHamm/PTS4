package nl.pts4.socket;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import nl.pts4.controller.DatabaseController;
import nl.pts4.model.AccountModel;

/**
 * Created by Teun on 17-3-2016.
 */
public class SocketIORegistration {

    private static SocketIORegistration socketIORegistration;

    private SocketIORegistration() {
        SocketIOServer server = new SocketIOServer(getConfiguration());
        addEvents(server);
        server.start();
    }

    public static void start() {
        if (socketIORegistration == null) socketIORegistration = new SocketIORegistration();
    }

    private void addEvents(SocketIOServer server) {
        // Registration email check
        server.addEventListener("emailCheck", String.class, (socketIOClient, email, ackRequest) -> {
            AccountModel am = DatabaseController.getInstance().getAccount(email);
            socketIOClient.sendEvent("email", am != null);
        });
    }

    private Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8081);
        return config;
    }
}
