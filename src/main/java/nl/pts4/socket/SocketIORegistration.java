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
    private SocketIOServer server;

    private SocketIORegistration() {
        server = new SocketIOServer(getConfiguration());
        addEvents(server);
        start();
    }

    public void start() {
        server.start();
    }

    public static SocketIORegistration getInstance() {
        if (socketIORegistration == null) socketIORegistration = new SocketIORegistration();

        return socketIORegistration;
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

    public void shutdown() {
        server.stop();
    }
}
