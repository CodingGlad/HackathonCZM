package cz.cvut.fel.camunda.workshops.developer.handlers;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@ExternalTaskSubscription(
        topicName = "handleReservation"
)
public class ReservationHandler implements ExternalTaskHandler {


    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        Map<String, Object> inputVariables = externalTask.getAllVariables();

        log.info("Client name: " + inputVariables.get("jmenoVubec"));

        externalTaskService.complete(externalTask);
    }
}
