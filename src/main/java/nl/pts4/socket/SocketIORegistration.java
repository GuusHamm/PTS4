package nl.pts4.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;
import nl.pts4.controller.DatabaseController;
import nl.pts4.model.AccountModel;

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
        server.addEventListener("emailCheck", String.class, (socketIOClient, email, ackRequest) -> {
            AccountModel am = DatabaseController.getInstance().getAccount(email);
            if (am != null) {
                socketIOClient.sendEvent("emailTaken", email);
            } else {
                socketIOClient.sendEvent("emailOk", email);
            }
        });
        server.start();
    }

    private Configuration getConfiguration() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(8081);
        return config;
    }

}
