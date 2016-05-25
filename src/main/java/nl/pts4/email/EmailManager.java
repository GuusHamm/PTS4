package nl.pts4.email;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Teun on 25-5-2016.
 */
public class EmailManager {

    private Session session;
    private final String email = "pts4oauth@gmail.com";

    public EmailManager() {
        String emailPass = "onlyhereforapikeys";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        this.session = Session.getInstance(props, new GMailAuthenticator(email, emailPass));
    }

    public void sendMessage(String msg, String recepient, String subject) {
        MimeMessage message = new MimeMessage(session);

        try {
            message.setRecipient(Message.RecipientType.TO, InternetAddress.parse(recepient)[0]);
            message.setContent(msg, "text/html; charset=utf-8");
            message.setSubject(subject);
            message.setFrom(new InternetAddress(email, "Fotowinkel PTS4"));

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String template, Map<String, Object> parameters, String recepient, String subject) {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());

        String text = VelocityEngineUtils.mergeTemplateIntoString(engine, "/templates/email/" + template, "UTF-8", parameters);
        sendMessage(text, recepient, subject);
    }

}
