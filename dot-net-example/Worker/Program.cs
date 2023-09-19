// Simple REST client for Camunda 7 Example

using Camunda.Api.Client;
using Camunda.Api.Client.ExternalTask;
using Camunda.Api.Client.Message;

Console.WriteLine("Camunda Example!");

// Create HTTP client
var httpClient = new HttpClient();
httpClient.BaseAddress = new Uri("http://89.221.215.30:8080/engine-rest");
// httpClient.DefaultRequestHeaders.Add("Authorization", "Basic ZGVtbzpkZW1v"); // BASIC AUTH
var camunda = CamundaClient.Create(httpClient);

// // Start new process instance

// Fetch all external tasks


// Fetch and lock tasks for topics
var topics = new List<FetchExternalTaskTopic> { new("sendMessage", 5000) };
List<LockedExternalTask> tasks = await camunda.ExternalTasks.FetchAndLock(new FetchExternalTasks()
    { MaxTasks = 2, Topics = topics, WorkerId = "worker-1" });

foreach (var task in tasks)
{
    Console.WriteLine(task.Id + " - " + task.TopicName);
    
    //Create Message Correlation
    var msg = new CorrelationMessage() { All = true, MessageName = "INTER_MESSAGE"};

// correlate message with process variables
    await camunda.Messages.DeliverMessage(msg);

    await camunda.ExternalTasks[task.Id].Complete(new CompleteExternalTask() { WorkerId = "worker-1"});
}


// Complete a User Task

// var userTasks = await camunda.UserTasks.Query(new TaskQuery() {ProcessInstanceId = processInstance.Id}).List();
//
// foreach(var userTask in userTasks) {
//     if ("UserTask_Example".Equals(userTask.TaskDefinitionKey))
//     {
//         await camunda.UserTasks[userTask.Id].Complete(new CompleteTask());
//     }
// }

//Create Message Correlation
// var msg = new CorrelationMessage() { All = true, MessageName = "NEW_PROCESS_MESSAGE" };
//
// msg.ProcessVariables
//     .Set("todayDate", DateTime.Today)
//     .Set("complexVariable", new { abc = "xyz", num = 123});
//
// // correlate message with process variables
// await camunda.Messages.DeliverMessage(msg);