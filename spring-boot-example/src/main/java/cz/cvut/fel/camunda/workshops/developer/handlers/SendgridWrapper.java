package cz.cvut.fel.camunda.workshops.developer.handlers;

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
import java.util.UUID;

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
        String subject = subjectInput;
        Email to = new Email(receiver);
        Content content = new Content(contentType, textContent);

        Mail mail = new Mail(from, subject, to, content);

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



    public Response sendReminder(String receiver) throws IOException {
        return sendMail(receiver, "Reminder with database data", "reminder", "text/plain");
    }

    public Response sendReservationConfirmation(String receiver, UUID orderId, String dateTime) throws IOException {
        String[] parts = dateTime.split(" ");
        String dateString = parts[0];
        String timeString = parts[1];
        String template = """
                <p><strong>Vážený zákazníku,</strong></p>
                <p>
                    Děkujeme vám za rezervaci termínu pro návštěvu našeho pneuservisu. Náš profesionální mechanik se rád postará o váše vozidlo a ušetří váš čas.
                </p>
                <p><strong>Podrobnosti vaší rezervace:</strong></p>
                <ul>
                    <li>Číslo objednávky: %s</li>
                    <li>Datum: %s</li>
                    <li>Čas: %s</li>
                </ul>
                <p>Upozorňujeme, že zrušení rezervace je možné do 1 týdně před termínem. V případě, že nás neinformujete o své neschopnosti dorazit, hrozí pokuta ve výši 500 korun.</p>
                <p><strong>S pozdravem,</strong></p>
                <p>Václav Stopa</p>
                """;
        String content = String.format(template, orderId, dateString, timeString);

        String subject = "Potvrzení o objednávce č. " + orderId;
        return sendMail(receiver, content, subject, "text/html");
    }
}
