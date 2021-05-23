package com.example.equiclubapp.Models;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Client {

    private int clientId ;
    private String sessionToken ;
    private String fName ;
    private String lName ;
    private LocalDateTime birthDate ;
    private String pathPhoto;
    private Bitmap photo ;
    private String identityDoc ;
    private String identityNumber ;
    private LocalDateTime inscriptionDate ;
    private LocalDateTime ensurenceValidity ;
    private LocalDateTime licenceValidity ;
    private String clientEmail ;
    private String passwd ;
    private String clientPhone ;
    private double priceRate ;
    private boolean isActive ;
    private String notes ;

    public Client(int clientId, String fName, String lName, String clientEmail, String clientPhone,
                String identityDoc, String identityNumber, String pathPhoto) {
        this.clientId = clientId;
        this.fName = fName;
        this.lName = lName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.identityDoc = identityDoc;
        this.identityNumber = identityNumber;
        this.pathPhoto = pathPhoto;
    }

    public Client(int clientId, String fName, String lName, String clientEmail, String clientPhone,
                  String identityDoc, String identityNumber, String pathPhoto, boolean isActive) {
        this.clientId = clientId;
        this.fName = fName;
        this.lName = lName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.identityDoc = identityDoc;
        this.identityNumber = identityNumber;
        this.pathPhoto = pathPhoto;
        this.isActive = isActive;
    }

    public Client(int clientId, String fName, String lName, LocalDateTime birthDate,
                  String pathPhoto, String identityDoc, String identityNumber,
                  LocalDateTime inscriptionDate, String clientEmail, String clientPhone,
                  LocalDateTime ensurenceValidity, LocalDateTime licenceValidity, boolean isActive) {
        this.clientId = clientId;
        this.fName = fName;
        this.lName = lName;
        this.birthDate = birthDate;
        this.pathPhoto = pathPhoto;
        this.identityDoc = identityDoc;
        this.identityNumber = identityNumber;
        this.inscriptionDate = inscriptionDate;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
        this.ensurenceValidity = ensurenceValidity;
        this.licenceValidity = licenceValidity;
        this.isActive = isActive;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public String getIdentityDoc() {
        return identityDoc;
    }

    public void setIdentityDoc(String identityDoc) {
        this.identityDoc = identityDoc;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public LocalDateTime getInscriptionDate() {
        return inscriptionDate;
    }

    public void setInscriptionDate(LocalDateTime inscriptionDate) {
        this.inscriptionDate = inscriptionDate;
    }

    public LocalDateTime getEnsurenceValidity() {
        return ensurenceValidity;
    }

    public void setEnsurenceValidity(LocalDateTime ensurenceValidity) {
        this.ensurenceValidity = ensurenceValidity;
    }

    public LocalDateTime getLicenceValidity() {
        return licenceValidity;
    }

    public void setLicenceValidity(LocalDateTime licenceValidity) {
        this.licenceValidity = licenceValidity;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public double getPriceRate() {
        return priceRate;
    }

    public void setPriceRate(double priceRate) {
        this.priceRate = priceRate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPathPhoto() {
        return pathPhoto;
    }

    public void setPathPhoto(String pathPhoto) {
        this.pathPhoto = pathPhoto;
    }
}
