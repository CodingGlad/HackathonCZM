package cz.cvut.fel.camunda.workshops.developer.handlers;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SendgridWrapper {
    public Response sendMail() throws IOException {
        return sendMail("poreothepwner@gmail.com",
                "This is a test email sent from SendGrid.");
    }

    public Response sendMail(String receiver, String textContent) throws IOException {
        String apiKey = "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";

        Email from = new Email("jakubwodecki@seznam.cz");
        String subject = "Hello, SendGrid!";
        Email to = new Email(receiver);
        Content content = new Content("text/plain", textContent);

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
        return sendMail(receiver, "Reminder with database data");
    }
}
