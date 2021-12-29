package com.example.taskmanager.domain;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ListMessage {
    private String action;
    private List<Task> task;
    private HttpStatus status;

    public ListMessage(){}

    public ListMessage(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<Task> getTask() {
        return task;
    }

    public void setTask(List<Task> task) {
        this.task = task;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Message{" +
                "action='" + action + '\'' +
                ", taskList='" + task + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
