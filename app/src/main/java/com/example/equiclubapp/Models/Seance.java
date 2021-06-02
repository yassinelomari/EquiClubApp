package com.example.equiclubapp.Models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.equiclubapp.CalendarActivity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Seance implements Parcelable {
    private int seanceId;
    private int seanceGrpId;
    private int clientId;
    private int monitorId;
    private LocalDateTime startDate;
    private int durationMinut;
    private Boolean isDone;
    private int paymentId;
    private String comments;

    public Seance() {
    }

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

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Seance(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Creator<Seance>() {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public Seance createFromParcel(Parcel source) {
            return new Seance(source);
        }

        @Override
        public Seance[] newArray(int size) {
            return new Seance[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(this.comments);
        dest.writeInt(this.seanceId);
        dest.writeInt(this.seanceGrpId);
        dest.writeInt(this.clientId);
        dest.writeInt(this.monitorId);
        dest.writeInt(this.durationMinut);
        dest.writeInt(this.paymentId);
        dest.writeLong(startDate != null ?
                startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : -1);
        dest.writeByte((byte) (isDone ? 1 : 0));
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void readFromParcel(Parcel in ) {
        comments = in.readString();
        seanceId = in.readInt();
        seanceGrpId = in.readInt();
        clientId = in.readInt();
        monitorId = in.readInt();
        durationMinut = in.readInt();
        paymentId = in.readInt();
        startDate = Instant.ofEpochMilli(in.readLong()).atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        isDone = in.readByte() != 0;
    }

    /*@Override
    public int hashCode() {
        return (Integer.valueOf(seanceId)).hashCode();
    }*/

    @Override
    public boolean equals(@Nullable Object obj) {
        Log.e(Seance.class.getSimpleName(),"seance id1 : " + (((Seance) obj).seanceId));
        Log.e(Seance.class.getSimpleName(),"seance id2 : " + this.seanceId);
        return (((Seance) obj).seanceId == this.seanceId);
    }
}
