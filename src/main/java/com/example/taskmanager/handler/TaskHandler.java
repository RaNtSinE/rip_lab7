package com.example.taskmanager.handler;

import com.example.taskmanager.domain.ListMessage;
import com.example.taskmanager.domain.Message;
import com.example.taskmanager.domain.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class TaskHandler extends TextWebSocketHandler {

    private final TaskRepository taskRepository;
    private final Set<WebSocketSession> sessionList = new HashSet<>();

    @Autowired
    public TaskHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {

        sessionList.add(session);
        ObjectMapper objectMapper = new ObjectMapper();
        var clientMessage = message.getPayload();
        Message requestMessage = objectMapper.readValue(clientMessage, Message.class);

        String action = requestMessage.getAction();

        System.out.println(requestMessage.getAction());

        ListMessage responseListMessage = new ListMessage(
                requestMessage.getAction()
        );
        Message responseMessage = new Message(
                requestMessage.getAction()
        );

        String json;

        switch (action) {
            case "getAll":

                responseListMessage.setTask(taskRepository.findAll(Sort.by(Sort.Direction.ASC, "id")));
                responseMessage.setStatus(HttpStatus.OK);
                json = objectMapper.writeValueAsString(responseListMessage);
                System.out.println("ResultingJSONstring = " + json);

                session.sendMessage(new TextMessage(json));
                break;
            case "createTask": {
                responseMessage.setTask(taskRepository.save(requestMessage.getTask()));
                responseMessage.setStatus(HttpStatus.OK);

                json = objectMapper.writeValueAsString(responseMessage);
                System.out.println("ResultingJSONstring = " + json);

                for (var sessionUnit : sessionList) {
                    if (!sessionUnit.isOpen())
                        sessionList.remove(sessionUnit);
                    else
                        sessionUnit.sendMessage(new TextMessage(json));
                }

                break;
            }
            case "updateTask": {
                Optional<Task> taskFromDb = taskRepository.findById(requestMessage.getTask().getId());
                Task task = new Task();
                if(taskFromDb.isPresent())
                    task = taskFromDb.get();

                BeanUtils.copyProperties(requestMessage.getTask(), task, "id");
                responseMessage.setTask(taskRepository.save(task));
                responseMessage.setStatus(HttpStatus.OK);

                json = objectMapper.writeValueAsString(responseMessage);
                System.out.println("ResultingJSONstring = " + json);

                for (var sessionUnit : sessionList) {
                    if (!sessionUnit.isOpen())
                        sessionList.remove(sessionUnit);
                    else
                        sessionUnit.sendMessage(new TextMessage(json));
                }
                break;
            }
            case "updateIsComplete": {
                Optional<Task> taskFromDb = taskRepository.findById(requestMessage.getTask().getId());
                Task task = new Task();
                if(taskFromDb.isPresent())
                    task = taskFromDb.get();
                Boolean isComplete = task.getIsComplete();
                task.setIsComplete(!isComplete);
                responseMessage.setTask(taskRepository.save(task));
                responseMessage.setStatus(HttpStatus.OK);

                json = objectMapper.writeValueAsString(responseMessage);
                System.out.println("ResultingJSONstring = " + json);

                for (var sessionUnit : sessionList) {
                    if (!sessionUnit.isOpen())
                        sessionList.remove(sessionUnit);
                    else
                        sessionUnit.sendMessage(new TextMessage(json));
                }
                break;
            }
            case "deleteTask": {
                Optional<Task> taskFromDb = taskRepository.findById(requestMessage.getTask().getId());
                Task task = new Task();
                if(taskFromDb.isPresent())
                    task = taskFromDb.get();
                responseMessage.setTask(task);
                taskRepository.delete(task);
                responseMessage.setStatus(HttpStatus.OK);

                json = objectMapper.writeValueAsString(responseMessage);
                System.out.println("ResultingJSONstring = " + json);

                for (var sessionUnit : sessionList) {
                    if (!sessionUnit.isOpen())
                        sessionList.remove(sessionUnit);
                    else
                        sessionUnit.sendMessage(new TextMessage(json));
                }
                break;
            }
            default:

                session.sendMessage(new TextMessage("Unknown command"));
                break;
        }
    }
}
