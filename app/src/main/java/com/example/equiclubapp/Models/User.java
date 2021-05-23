package com.example.equiclubapp.Models;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.RequiresApi;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class User implements Parcelable {

    private int userId;
    private String sessionToken;
    private String userEmail;
    private String userPasswd;
    private int adminLevel;
    private LocalDateTime lastLoginTime;
    private Boolean isActive;
    private String userFname;
    private String userLname;
    private String description;
    private String userType;
    private String userphoto;
    private Bitmap photo ;
    private LocalDateTime contractDate;
    private String userPhone;
    private String displayColor;

    public User(int userId, String userEmail, int adminLevel, LocalDateTime lastLoginTime,
                Boolean isActive, String userFname, String userLname, String description,
                String userType, String userphoto, LocalDateTime contractDate, String userPhone,
                String displayColor) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.adminLevel = adminLevel;
        this.lastLoginTime = lastLoginTime;
        this.isActive = isActive;
        this.userFname = userFname;
        this.userLname = userLname;
        this.description = description;
        this.userType = userType;
        this.userphoto = userphoto;
        this.contractDate = contractDate;
        this.userPhone = userPhone;
        this.displayColor = displayColor;
    }

    public User(int userId, String userEmail, String userFname, String userLname, String userType,
                String userphoto, String userPhone) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFname = userFname;
        this.userLname = userLname;
        this.userType = userType;
        this.userphoto = userphoto;
        this.userPhone = userPhone;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPasswd() {
        return userPasswd;
    }

    public void setUserPasswd(String userPasswd) {
        this.userPasswd = userPasswd;
    }

    public int getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(int adminLevel) {
        this.adminLevel = adminLevel;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Boolean isUserActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getUserFname() {
        return userFname;
    }

    public void setUserFname(String userFname) {
        this.userFname = userFname;
    }

    public String getUserLname() {
        return userLname;
    }

    public void setUserLname(String userLname) {
        this.userLname = userLname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserphoto() {
        return userphoto;
    }

    public void setUserphoto(String userphoto) {
        this.userphoto = userphoto;
    }

    public LocalDateTime getContractDate() {
        return contractDate;
    }

    public void setContractDate(LocalDateTime contractDate) {
        this.contractDate = contractDate;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getDisplayColor() {
        return displayColor;
    }

    public void setDisplayColor(String displayColor) {
        this.displayColor = displayColor;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public User(Parcel in ) {
        readFromParcel( in );
    }

    public static final Parcelable.Creator CREATOR = new Creator<User>() {

        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userFname);
        dest.writeString(userLname);
        dest.writeString(userEmail);
        dest.writeString(userPasswd);
        dest.writeString(userPhone);
        dest.writeString(userphoto);
        dest.writeString(userType);
        dest.writeString(this.sessionToken);
        dest.writeString(this.displayColor);
        dest.writeString(this.description);
        dest.writeInt(this.adminLevel);
        dest.writeInt(this.userId);
        dest.writeLong(contractDate != null ?
                contractDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : -1);
        dest.writeLong(lastLoginTime != null ?
                lastLoginTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() : -1);
        //dest.writeSerializable(this.contractDate);
        //dest.writeSerializable(this.lastLoginTime);
        dest.writeByte((byte) (isActive ? 1 : 0));
        //dest.writeSerializable(this.photo.);
    }
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void readFromParcel(Parcel in ) {
        userFname = in.readString();
        userLname = in.readString();
        userEmail = in.readString();
        userPasswd = in.readString();
        userPhone = in.readString();
        userphoto = in.readString();
        userType = in.readString();
        sessionToken = in.readString();
        displayColor = in.readString();
        description = in.readString();
        adminLevel = in.readInt();
        userId = in.readInt();
        contractDate = Instant.ofEpochMilli(in.readLong()).atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        lastLoginTime = Instant.ofEpochMilli(in.readLong()).atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        isActive = in.readByte() != 0;
    }
}
