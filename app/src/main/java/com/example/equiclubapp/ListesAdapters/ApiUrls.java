package com.example.equiclubapp.ListesAdapters;

public interface ApiUrls {
    String BASE = "https://192.168.1.108:44352/api";
    String CLIENTS_WS = "/Clients/";
    String PHOTO_WS = "/Clients/photo/";
    String PHOTO_CLIENTID_WS = "/Clients/photoById/";
    String USERS_WS = "/Users/";
    String USERS_MODIF_PHOTO_WS = "/Users/photo/";
    String SEANCES_WS = "/Seances/";
    String SEANCES_MONITOR_WS = "/Seances/monitor/";
    String GP_SC_WS = "/Seances/groups/";
    String MAX_GP_WS = "/Seances/groupIdMax/";
    String USERS_DS_WS= "/Users/disable/";
    String USERS_EN_WS = "/Users/enable/";
    String CLIENTS_DS_WS = "/Clients/disable/";
    String CLIENTS_EN_WS = "/Clients/enable/";
    String LOGIN_WS = "/Users/login/";
    String TASKS_WS = "/Tasks/";
}
