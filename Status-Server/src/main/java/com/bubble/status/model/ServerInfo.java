package com.bubble.status.model;

import lombok.Data;
@Data
public class ServerInfo extends ConfigInfo{
    private String host;
    private int connectedPort = -1;
    private String connectedIP = "";
    private boolean isOnline = false;
}
