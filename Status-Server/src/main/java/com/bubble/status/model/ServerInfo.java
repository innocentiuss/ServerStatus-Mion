package com.bubble.status.model;

import lombok.Data;

@Data
public class ServerInfo {
    private String username;
    private String name;
    private String type;
    private String host;
    private String location;
    private String password;
    private int connectedPort = -1;
    private String connectedIP = "";
    private boolean isOnline = false;
    private boolean disabled = false;

    public ServerInfo(String username, String name, String type, String host, String location, String password) {
        this.username = username;
        this.name = name;
        this.type = type;
        this.host = host;
        this.location = location;
        this.password = password;
    }

    public ServerInfo() {
    }
}
