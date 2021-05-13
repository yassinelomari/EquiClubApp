package com.example.equiclubapp.Models;

import java.time.LocalDateTime;

public class Seance {
    private int seanceId;
    private int seanceGrpId;
    private int clientId;
    private int monitorId;
    private LocalDateTime startDate;
    private int durationMinut;
    private Boolean isDone;
    private int paymentId;
    private String comments;

    public Seance(int seanceId, int seanceGrpId, int clientId, int monitorId,
                  LocalDateTime startDate, int durationMinut, Boolean isDone, int paymentId,
                  String comments) {
        this.seanceId = seanceId;
        this.seanceGrpId = seanceGrpId;
        this.clientId = clientId;
        this.monitorId = monitorId;
        this.startDate = startDate;
        this.durationMinut = durationMinut;
        this.isDone = isDone;
        this.paymentId = paymentId;
        this.comments = comments;
    }

    public int getSeanceId() {
        return seanceId;
    }

    public void setSeanceId(int seanceId) {
        this.seanceId = seanceId;
    }

    public int getSeanceGrpId() {
        return seanceGrpId;
    }

    public void setSeanceGrpId(int seanceGrpId) {
        this.seanceGrpId = seanceGrpId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(int monitorId) {
        this.monitorId = monitorId;
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

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
