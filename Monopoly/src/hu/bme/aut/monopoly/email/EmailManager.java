package hu.bme.aut.monopoly.email;

import hu.bme.aut.monopoly.model.Game;
import hu.bme.aut.monopoly.model.HouseBuying;
import hu.bme.aut.monopoly.model.Player;
import hu.bme.aut.monopoly.model.Step;

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

    public static boolean sendInvitationEmail(String recipicientEmail, Game game)
    {
        String emailContent = "Dear Monopoly User" + "," + "\n\nYou got a new invitation. Check the following link: ";
        String link = "https://localhost:8443/Monopoly/pages/game.html?email=" + recipicientEmail + "&gameid="
                + game.getId();
        String emailSubject = "Monopoly invitation";
        return sendEmail(recipicientEmail, emailContent + link, emailSubject);
    }

    public static boolean sendStepSummaryEmail(String recipicientEmail, Game game)
    {
        String link = "https://localhost:8443/Monopoly/pages/game.html?email=" + recipicientEmail + "&gameid="
                + game.getId();

        String stepProerty = "";
        for (Player player : game.getPlayers())
        {
            stepProerty = stepProerty.concat("Player: " + player.getUser().getEmail() + "\n");
            Step step = player.getSteps().get(player.getSteps().size() - 1);
            stepProerty = stepProerty
                    .concat("Place number: " + step.getFinishPlace().getPlaceSequenceNumber() + "\n");
            if (step.getHouseBuyings().size() != 0)
            {
                stepProerty = stepProerty.concat("The player bought " + step.getHouseBuyings().size()
                        + " building(s).");
                int numOfBuying = 1;
                for (HouseBuying aHouseBuying : step.getHouseBuyings())
                {
                    stepProerty = stepProerty.concat("\n\t" + numOfBuying + ".");
                    stepProerty = stepProerty.concat("\tName of bought house: "
                            + aHouseBuying.getForBuilding().getBuilding().getName());
                    stepProerty = stepProerty.concat("\tNumber of bought houses: "
                            + aHouseBuying.getBuyedHouseNumber());
                    numOfBuying++;
                }
            }

            stepProerty = stepProerty.concat("\nMoney: " + player.getMoney() + "\n\n");

        }
        System.out.println("STEP" + stepProerty);

        String emailContent = "Dear Monopoly User" + "," + "\n\nThis happened, since your last turn:\n" + stepProerty
                + "\nIf you are ready for your next step, check the following link: " + link;
        String emailSubject = "Monopoly step summary";
        return sendEmail(recipicientEmail, emailContent, emailSubject);

    }
}