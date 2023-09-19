package cz.cvut.fel.camunda.workshops.developer.handlers;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.SpringTopicSubscription;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.spring.event.SubscriptionInitializedEvent;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ExternalTaskSubscription(
        topicName = "fetchUserDetail"
)
public class FetchUserDetailHandler implements ExternalTaskHandler {


    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        VariableMap variables = Variables.createVariables();
        variables.put("customerId", "Hello World");

        UserDetail userDetail = new UserDetail();
        userDetail.setName("Karel");
        userDetail.setUsername("karelnov");

        variables.put("customer", Variables.objectValue(userDetail).serializationDataFormat("application/json").create());

        externalTaskService.complete(externalTask, variables);
    }

    @EventListener(SubscriptionInitializedEvent.class)
    public void catchSubscriptionInitEvent(SubscriptionInitializedEvent event) {

        SpringTopicSubscription topicSubscription = event.getSource();
        if (!topicSubscription.isAutoOpen()) {

            // open topic in case it is not opened already
            topicSubscription.open();

            log.info("Subscription with topic name '{}' has been opened!",
                     topicSubscription.getTopicName());
        }
    }
}
