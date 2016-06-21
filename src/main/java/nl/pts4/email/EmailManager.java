package nl.pts4.email;

import nl.pts4.admin.advertisement.VelocityEngineConfiguration;
import nl.pts4.controller.DatabaseController;
import nl.pts4.model.AccountModel;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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
        VelocityEngine engine = VelocityEngineConfiguration.getConfiguredEngine();

        String text = VelocityEngineUtils.mergeTemplateIntoString(engine, "/templates/email/" + template, "UTF-8", parameters);
        sendMessage(text, recepient, subject);
    }

    public void sendAdvertisementTemplate(String template, Map<String, Object> parameters, String subject) {
        VelocityEngine engine = VelocityEngineConfiguration.getConfiguredEngine();

        HashMap<UUID, AccountModel> accountModels = DatabaseController.getInstance().getAllAccounts();
        for (UUID uuid : accountModels.keySet()) {
            AccountModel am = accountModels.get(uuid);

            parameters.put("user", am);

            String text = VelocityEngineUtils.mergeTemplateIntoString(engine, "/templates/advertisements/" + template, "UTF-8", parameters);
            sendMessage(text, am.getEmail(), subject);

            parameters.remove("user");
        }
    }

}
