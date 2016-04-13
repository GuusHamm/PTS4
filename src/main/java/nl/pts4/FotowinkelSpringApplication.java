package nl.pts4;

import nl.pts4.socket.SocketIORegistration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FotowinkelSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(FotowinkelSpringApplication.class, args);
        SocketIORegistration.start();
    }


}
