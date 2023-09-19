package cz.cvut.fel.camunda.workshops.developer.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.community.rest.client.api.ProcessDefinitionApi;
import org.camunda.community.rest.client.dto.StartProcessInstanceDto;
import org.camunda.community.rest.client.dto.VariableValueDto;
import org.camunda.community.rest.client.invoker.ApiException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StartProcessController {

    private static final String PROCESS_KEY = "Process_ExternalTaskExample";

    private final ProcessDefinitionApi processDefinitionApi;

    @PostMapping("/startProcess")
    public void startProcess() throws ApiException {
        StartProcessInstanceDto startProcessInstanceDto = new StartProcessInstanceDto();
        startProcessInstanceDto.businessKey(String.valueOf(UUID.randomUUID()))
                        .putVariablesItem("hello", new VariableValueDto().value("world"));
        processDefinitionApi.startProcessInstance(PROCESS_KEY, startProcessInstanceDto);
    }

}
