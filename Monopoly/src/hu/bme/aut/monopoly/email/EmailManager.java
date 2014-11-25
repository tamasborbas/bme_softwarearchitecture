package hu.bme.aut.monopoly.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class EmailManager
{
    private static final String MONOPOLYBME_EMAILADDRESS = "monopolybme@gmail.com";
    private static final String MONOPOLYBME_PASSWORD = "As123456+";
    private static final String MONOPOLYBME_USERNAME = "monopolybme";

    public static boolean sendEmail(String recipicientEmail, String emailContent, String emailSubject)
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator()
        {
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(MONOPOLYBME_USERNAME, MONOPOLYBME_PASSWORD);
            }
        });

        try
        {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MONOPOLYBME_EMAILADDRESS));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipicientEmail));
            message.setSubject(emailSubject);
            message.setText(emailContent);

            Transport.send(message);

            System.out.println("Email sent.");

        } catch (MessagingException e)
        {
            throw new RuntimeException(e);
        }

        return true;
    }

    public static boolean sendReminderEmail(String recipicientEmail, String username, String password)
    {
        String emailContent = "Dear " + username + "," + "\n\nYour username is: " + username + "\nYour password is: "
                + password;
        String emailSubject = "Monopoly password reminder";
        return sendEmail(recipicientEmail, emailContent, emailSubject);

    }
}