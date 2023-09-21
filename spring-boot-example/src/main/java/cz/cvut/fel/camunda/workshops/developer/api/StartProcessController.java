package cz.cvut.fel.camunda.workshops.developer.api;

import com.sendgrid.Response;
import cz.cvut.fel.camunda.workshops.developer.handlers.SendgridWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.dto.StartProcessInstanceDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StartProcessController {
    @Autowired SendgridWrapper sendgridWrapper;
    private static final String PROCESS_KEY = "Process_ExternalTaskExample";

    private final ProcessDefinitionApi processDefinitionApi;

    @PostMapping("/startProcess")
    public void startProcess() throws ApiException {
        StartProcessInstanceDto startProcessInstanceDto = new StartProcessInstanceDto();
        startProcessInstanceDto.businessKey(String.valueOf(UUID.randomUUID()))
                        .putVariablesItem("hello", new VariableValueDto().value("world"));
        processDefinitionApi.startProcessInstance(PROCESS_KEY, startProcessInstanceDto);
    }

    @PostMapping("/sendEmail")
    public Response sendEmail(@RequestBody(required = false) String receiver,
                              @RequestBody(required = false) String message) throws IOException {
        if (receiver == null || message == null) {
            return sendgridWrapper.sendMail();
        }
        return sendgridWrapper.sendMail(receiver, message, "test", "text/plain");
    }

    @PostMapping("/sendReminder")
    public Response sendReminder(@RequestBody String receiver) throws IOException {
        return sendgridWrapper.sendReminder(receiver);
    }


}
