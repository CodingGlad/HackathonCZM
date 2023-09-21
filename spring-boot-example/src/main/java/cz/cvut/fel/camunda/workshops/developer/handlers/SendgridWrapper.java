package cz.cvut.fel.camunda.workshops.developer.handlers;

import lombok.extern.slf4j.Slf4j;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import cz.cvut.fel.camunda.workshops.developer.congif.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class SendgridWrapper {
    @Autowired
    private AppConfig appConfig;

    public Response sendMail() throws IOException {
        return sendMail("poreothepwner@gmail.com",
                "This is a test email sent from SendGrid.", "test subject", "text/plain");
    }

    public Response sendMail(String receiver, String textContent, String subjectInput, String contentType) throws IOException {
        String apiKey = appConfig.getApiKey();

        Email from = new Email("jakubwodecki@seznam.cz");
        Email to = new Email(receiver);
        Content content = new Content(contentType, textContent);

        Mail mail = new Mail(from, subjectInput, to, content);
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            return sg.api(request);

        } catch (IOException ex) {
            throw new IOException("Something went wrong with sending your email", ex);
        }
    }



    public Response setScheduledEmail(String receiver, String textContent, String subjectInput, String contentType,
                                      long scheduledTimeInUnixTimspamp) throws IOException {
        String apiKey = appConfig.getApiKey();

        Email from = new Email("jakubwodecki@seznam.cz");
        Email to = new Email(receiver);
        Content content = new Content(contentType, textContent);

        Mail mail = new Mail(from, subjectInput, to, content);
        mail.setSendAt(scheduledTimeInUnixTimspamp);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Date date = new Date(scheduledTimeInUnixTimspamp * 1000L); // Multiply by 1000 to convert from seconds to milliseconds

            // Create a SimpleDateFormat object for the desired date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            // Format the Date object as a string
            String formattedDate = dateFormat.format(date);


            log.info("MAIL TO " + receiver + " WILL ARRIVE AT " + formattedDate);
            return sg.api(request);

        } catch (IOException ex) {
            throw new IOException("Something went wrong with sending your email", ex);
        }
    }

    public Response sendReservationConfirmation(String receiver, UUID orderId, String dateTime) throws IOException {

        String template = """
                <p><strong>Vážený zákazníku,</strong></p>
                <p>
                    Děkujeme vám za rezervaci termínu pro návštěvu našeho pneuservisu. Náš profesionální mechanik se rád postará o váše vozidlo a ušetří váš čas.
                </p>
                <p><strong>Podrobnosti vaší rezervace:</strong></p>
                <ul>
                    <li>Číslo rezervace: %s</li>
                    <li>Datum: %s</li>
                    <li>Čas: %s</li>
                </ul>
                <p>Upozorňujeme, že zrušení rezervace je možné do 1 týdne před termínem. V případě, že nás neinformujete o své neschopnosti dorazit, hrozí pokuta ve výši 500 korun.</p>
                <p><strong>S pozdravem</strong></p>
                <p>Václav Stopa</p>
                """;
        String content = contentCreator(orderId, dateTime, template);

        String subject = "Potvrzení o rezervaci č. " + orderId;
        return sendMail(receiver, content, subject, "text/html");
    }

    public Response sendReminder (String receiver, UUID orderId, String dateTime, long timeToSendMailUnixTimestamp) throws IOException {
        String template = """
                    <p><strong>Vážený zákazníku,</strong></p>
                    <p>
                        Toto je připomenutí ohledně vaší rezervace na návštěvu našeho pneuservisu. Rádi bychom vás přivítali v našem servisním centru.
                    </p>
                    <p><strong>Podrobnosti vaší rezervace:</strong></p>
                    <ul>
                        <li>Číslo rezervace: %s</li>
                        <li>Datum: %s</li>
                        <li>Čas: %s</li>
                    </ul>
                    <p>Upozorňujeme, že zrušení rezervace je možné do 1 týdne před termínem. V případě, že nás neinformujete o své neschopnosti dorazit, hrozí pokuta ve výši 500 korun.</p>
                    <p><strong>S pozdravem</strong></p>
                    <p>Václav Stopa</p>
                """;

        String content = contentCreator(orderId, dateTime, template);

        String subject = "Připomenutí rezervace č. " + orderId;

        return setScheduledEmail(receiver, content, subject, "text/html", timeToSendMailUnixTimestamp);

    };

    public String contentCreator(UUID orderId, String dateTime, String template) {
        String[] parts = dateTime.split(" ");
        String dateString = parts[0];
        String timeString = parts[1];
        return String.format(template, orderId, dateString, timeString);
    }

}
