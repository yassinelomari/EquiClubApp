package com.example.equiclubapp.Models;

import java.time.LocalDateTime;

public class Task {
    private int taskId;
    private LocalDateTime startDate;
    private int durationMinut;
    private String title;
    private String detail;
    private LocalDateTime isDone;
    private int user_Fk;

    public Task(int taskId, LocalDateTime startDate, int durationMinut, String title, String detail,
                LocalDateTime isDone, int user_Fk) {
        this.taskId = taskId;
        this.startDate = startDate;
        this.durationMinut = durationMinut;
        this.title = title;
        this.detail = detail;
        this.isDone = isDone;
        this.user_Fk = user_Fk;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public int getDurationMinut() {
        return durationMinut;
    }

    public void setDurationMinut(int durationMinut) {
        this.durationMinut = durationMinut;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getIsDone() {
        return isDone;
    }

    public void setIsDone(LocalDateTime isDone) {
        this.isDone = isDone;
    }

    public int getUser_Fk() {
        return user_Fk;
    }

    public void setUser_Fk(int user_Fk) {
        this.user_Fk = user_Fk;
    }
}
